package fr.an.tools.git2neo4j.service;

import static org.eclipse.jgit.lib.RefDatabase.ALL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableInt;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectIdRef;
import org.eclipse.jgit.lib.ObjectIdRef.PeeledTag;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.SymbolicRef;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.an.tools.git2neo4j.domain.AbstractRepoRefEntity;
import fr.an.tools.git2neo4j.domain.ObjectIdRepoRefEntity;
import fr.an.tools.git2neo4j.domain.PersonIdentEntity;
import fr.an.tools.git2neo4j.domain.RevCommitEntity;
import fr.an.tools.git2neo4j.domain.RevTreeEntity;
import fr.an.tools.git2neo4j.domain.SymbolicRepoRefEntity;
import fr.an.tools.git2neo4j.repository.ObjectIdRepoRefDAO;
import fr.an.tools.git2neo4j.repository.PersonDAO;
import fr.an.tools.git2neo4j.repository.RepoRefDAO;
import fr.an.tools.git2neo4j.repository.RevCommitDAO;
import fr.an.tools.git2neo4j.repository.RevTreeDAO;
import fr.an.tools.git2neo4j.repository.SymbolicRepoRefDAO;
import fr.an.tools.git2neo4j.service.RevTreeToEntityScanner.ScanRevTreeResult;

@Component
public class RevCommitSyncService {

	private static final Logger LOG = LoggerFactory.getLogger(RevCommitSyncService.class);

	@Autowired 
	private ExplicitXAHelper xaHelper;
	
	@Autowired 
	private SaveService saveService;
	
	@Autowired
	private RevCommitDAO revCommitDAO;
	
	@Autowired
	private PersonDAO personDAO;
	
	@Autowired
	private RepoRefDAO repoRefDAO;
	@Autowired
	private ObjectIdRepoRefDAO objectIdRepoRefDAO;
	@Autowired
	private SymbolicRepoRefDAO symbolicRepoRefDAO;

	@Autowired
	private RevTreeToEntityScanner revTreeToEntityScanner;
	
	@Autowired
	private RevTreeDAO revTreeDAO;
	
	
	// ------------------------------------------------------------------------

	public RevCommitSyncService() {
	}

	// ------------------------------------------------------------------------

	public void syncRepo(Git git) {
		LOG.info("sync git repo ...");
		SyncCtx ctx = xaHelper.doInXA(() -> preloadSyncCtx(git));
		
		Map<Ref, AbstractRepoRefEntity> ref2RefEntities = xaHelper.doInXA(() -> findOrCreateAllRefs(ctx));
				
		Map<RevCommit, RevCommitEntity> revCi2Entities = xaHelper.doInXA(() -> findOrCreateRevCommits(ctx));
		

		Map<String,Ref> refByName = refsToNameMap(ref2RefEntities.keySet());
		Ref headRef = refByName.get("HEAD");
		SymbolicRepoRefEntity headRefEntity = (SymbolicRepoRefEntity) ref2RefEntities.get(headRef);
		if (headRef != null && headRef instanceof SymbolicRef) {
			Ref targetHeadRef = headRef.getTarget();
			ObjectIdRepoRefEntity targetHeadRefEntity = (ObjectIdRepoRefEntity) headRefEntity.getTarget();
			
			xaHelper.doInXA(() -> syncRefCommitRevTree(ctx, targetHeadRef, targetHeadRefEntity));
		}

		
		xaHelper.doInXA(() -> updateRevCommitParents(ctx, revCi2Entities));

		// update refs (commitIds / targetRefs)
		xaHelper.doInXA(() -> updateRefs(ctx, ref2RefEntities));

		
		int revCiCount = revCi2Entities.size();
		int revCiIndex = 0;
		int batchSize = 50;
		List<Map<RevCommit, RevCommitEntity>> splitMaps = splitMap(revCi2Entities, batchSize);
		for(Map<RevCommit, RevCommitEntity> split : splitMaps) {
			xaHelper.doInXA(() -> {
				for(Map.Entry<RevCommit, RevCommitEntity>  e : split.entrySet()) {
					RevCommit commit = e.getKey();
					RevCommitEntity commitEntity = e.getValue();
					syncRevCommitTree(ctx, commit, commitEntity);
				}
			});
			revCiIndex += batchSize;
			LOG.info("\n[" + revCiIndex + "/" + revCiCount + "] update Tree for Commit\n");
		}
		
		
		
		// OutOfMemoryError + very very slow ...
		// update RevTree
		// recursiveUpdateDirTrees(ctx);
		
		LOG.info("done sync git repo");
	}

	public static <K,V> List<Map<K,V>> splitMap(Map<K,V> map, int batchSize) {
		List<Map<K,V>> res = new ArrayList<>();
		Map<K,V> curr = new LinkedHashMap<>();
		int currCount = 0;
		for(Map.Entry<K, V> e : map.entrySet()) {
			curr.put(e.getKey(), e.getValue());
			currCount++;
			if (currCount >= batchSize) {
				currCount = 0;
				res.add(curr);
				curr = new LinkedHashMap<>();
			}
		}
		return res;
	}
	
	private void syncRefCommitRevTree(SyncCtx ctx, Ref ref, ObjectIdRepoRefEntity refEntity) {
		ObjectId commitId = ref.getObjectId();
		RevCommit commit;
		try {
			commit = ctx.revWalk.parseCommit(commitId);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		RevCommitEntity commitEntity = refEntity.getRefCommit();
		if (commitEntity == null) {
			throw new RuntimeException("RevCommitEntity not found");
		}
		
		syncRevCommitTree(ctx, commit, commitEntity);
	}

	private void syncRevCommitTree(SyncCtx ctx, RevCommit commit, RevCommitEntity commitEntity) {
		RevTreeEntity revTreeEntity = commitEntity.getRevTree();
		if (revTreeEntity != null) {
			return;// TODO TEMPORARY ... immutable => assume sync ok .. no need to update
		}
		
		ObjectId revTreeId = commit.getTree();
		
		ScanRevTreeResult syncRevTreeRes = revTreeToEntityScanner.recursiveSyncRevTree(ctx.repository, revTreeId, ctx.sha2revTreeEntities);
		
		if (commitEntity.getRevTree() != syncRevTreeRes.rootRevTree) {
			if (commitEntity.getRevTree() == null) {
				revTreeDAO.save(syncRevTreeRes.rootRevTree);
			}
			
			commitEntity.setRevTree(syncRevTreeRes.rootRevTree);
			
			revCommitDAO.save(commitEntity, 1);
			// revCommitDAO.save(headCommitEntity) // => StackOverFlow...
		} else {
			// no change, do nothing
		}
		
		ctx.sha2revTreeEntities.putAll(syncRevTreeRes.revTree);
	}

	//?? //?? @Transactional
	protected SyncCtx preloadSyncCtx(Git git) {
		LOG.info("preloading symbolicRef");
		Map<String,SymbolicRepoRefEntity> symbolicRefEntities = refsToRefByNameMap(symbolicRepoRefDAO.findAll());
		LOG.info("preloading objectIdRef");
		Map<String,ObjectIdRepoRefEntity> objecIdRefEntities = refsToRefByNameMap(objectIdRepoRefDAO.findAll());
		LOG.info("preloading revCommits");
		Iterable<RevCommitEntity> revCommitEntities = revCommitDAO.findAll();
		LOG.info("preloading persons");
		Iterable<PersonIdentEntity> personEntities = personDAO.findAll();

		
		LOG.info("done preloading");
		SyncCtx ctx = new SyncCtx(git, saveService,
				symbolicRefEntities, objecIdRefEntities,
				revCommitEntities, personEntities 
				);
		return ctx;
	}


	//?? @Transactional
	protected Map<RevCommit, RevCommitEntity> findOrCreateRevCommits(SyncCtx ctx) {
		Iterable<RevCommit> revCommits;
		try {
			revCommits = ctx.git.log().all() // from all refs(=master + branchs ..)
					.call();
		} catch (Exception ex) {
			throw new RuntimeException("Failed to get git commit log history", ex);
		}

		// recursive save => resolve parent => save ... stack overflow...
		// => put in reverse order
		// should 1/ insert without relationships  then 2/ insert relationships
		List<RevCommit> reverseOrderRevCommits = new ArrayList<>();
		for (RevCommit e : revCommits) {
			reverseOrderRevCommits.add(e);
		}
		Collections.reverse(reverseOrderRevCommits);
		
		Map<RevCommit,RevCommitEntity> tmpRevCommitEntities = new LinkedHashMap<>();
		int progressFreq = 100;
		int progressCount = progressFreq;
		System.out.println("findOrCreateRevCommitEntity " + reverseOrderRevCommits.size() + " commit(s), show progress each " + progressFreq);
		for (RevCommit revCommit : reverseOrderRevCommits) {
			try {
				RevCommitEntity tmpres = findOrCreateRevCommitEntity(ctx, revCommit);
				tmpRevCommitEntities.put(revCommit, tmpres);
			} catch(Exception ex) {
				LOG.error("Failed", ex);
			}
			if (progressCount-- <= 0) {
				progressCount = progressFreq;
				System.out.print(".");
			}
		}

		revCommitDAO.save(tmpRevCommitEntities.values(), 1);
		
		return tmpRevCommitEntities;
	}
	
	protected void updateRevCommitParents(SyncCtx ctx, Map<RevCommit, RevCommitEntity> tmpRevCommitEntities) {
		LOG.info("updateRevCommitParents");		
		for (Map.Entry<RevCommit,RevCommitEntity>  e : tmpRevCommitEntities.entrySet()) {
			RevCommit revCommit = e.getKey();
			RevCommitEntity revCommitEntity = e.getValue();
			updateRevCommitParents(ctx, revCommit, revCommitEntity);
		}

		// do not save recursive... StackOverFlow !!
		// revCommitDAO.save(tmpRevCommitEntities.values());
	}
	

	//?? @Transactional
	protected Map<Ref,AbstractRepoRefEntity> findOrCreateAllRefs(SyncCtx ctx) {
		Map<Ref,AbstractRepoRefEntity> res = new HashMap<>();
		RefDatabase refDatabase = ctx.repository.getRefDatabase();
		Map<String, Ref> refs;
		try {
			refs = refDatabase.getRefs(ALL);
		} catch (IOException ex) {
			throw new RuntimeException("Failed to read refs", ex);
		}
		
		// find or create refs (no update target Ref, or target CommitId ) ... must be done at end after findOrCreate commitIds...  
		for (Ref ref : refs.values()) {
			LOG.debug("ref: " + ref);
			AbstractRepoRefEntity refEntity = findOrCreateRef(ctx, ref);
			res.put(ref, refEntity);
		}
		
		// update SymbolicRepoRefEntity
		for(Map.Entry<Ref,AbstractRepoRefEntity> e : res.entrySet()) {
			Ref ref = e.getKey();
			AbstractRepoRefEntity refEntity = e.getValue();
			if (ref instanceof SymbolicRef) {
				SymbolicRef symRef = (SymbolicRef) ref;
				SymbolicRepoRefEntity symRefEntity = (SymbolicRepoRefEntity) refEntity;
				updateGitToEntityRef(ctx, symRef, symRefEntity);
			}
		}
					
		List<AbstractRepoRefEntity> refEntities = new ArrayList<>(res.values());
		repoRefDAO.save(refEntities, 1);
		
		return res;
	}
	
	private AbstractRepoRefEntity findOrCreateRef(SyncCtx ctx, Ref ref) {
		AbstractRepoRefEntity res;
		if (ref instanceof SymbolicRef) {
			SymbolicRef symRef = (SymbolicRef) ref;
			String refName = symRef.getName();
			SymbolicRepoRefEntity entity = ctx.symbolicRefEntities.get(refName);
			if (entity == null) {
				entity = new SymbolicRepoRefEntity();
				entity.setName(refName);
				symbolicRepoRefDAO.save(entity, 1);
				ctx.symbolicRefEntities.put(refName, entity);
			}
			res = entity;
		} else if (ref instanceof ObjectIdRef) {
			ObjectIdRef oidRef = (ObjectIdRef) ref;
			String refName = oidRef.getName();
			ObjectIdRepoRefEntity entity = ctx.objecIdRefEntities.get(refName);
			if (entity == null) {
				entity = new ObjectIdRepoRefEntity();
				entity.setName(refName);
				objectIdRepoRefDAO.save(entity, 1);
				ctx.objecIdRefEntities.put(refName, entity);
			}
			res = entity;
		} else {
			LOG.error("unrecognised ref type (expeceting SymbolicRef / ObjectIdRef)");
			return null;
		}
		return res;
	}


	//?? @Transactional
	protected void updateRefs(SyncCtx ctx, Map<Ref,AbstractRepoRefEntity> refs) {
		for(Map.Entry<Ref,AbstractRepoRefEntity> e : refs.entrySet()) {
			Ref ref = e.getKey();
			AbstractRepoRefEntity refEntity = e.getValue();
			if (ref instanceof SymbolicRef) {
				SymbolicRef symRef = (SymbolicRef) ref;
				SymbolicRepoRefEntity symRefEntity = (SymbolicRepoRefEntity) refEntity;
				updateGitToEntityRef(ctx, symRef, symRefEntity);
			} else if (ref instanceof ObjectIdRef) {
				ObjectIdRef oidRef = (ObjectIdRef) ref;
				ObjectIdRepoRefEntity oidRefEntity = (ObjectIdRepoRefEntity) refEntity;
				updateGitToEntityRef(ctx, oidRef, oidRefEntity);
			}
		}
	}
	
	private void updateGitToEntityRef(SyncCtx ctx, ObjectIdRef src, ObjectIdRepoRefEntity res) {
		ObjectId oid = src.getObjectId();
		if (src instanceof PeeledTag) {
			PeeledTag peeledTag = (PeeledTag) src;
			ObjectId peeledObjectId = peeledTag.getPeeledObjectId();
			// LOG.info("update PeeledTag ref " + src + " peeledObjectId:" + peeledObjectId);
			oid = peeledObjectId;
		}
		
		RevCommitEntity revCommitEntity = ctx.sha2revCommitEntities.get(oid);
		if (revCommitEntity == null) {
			LOG.warn("Should not occurs: revCommit " + oid.name() + " not found for ObjectIdRef " + src);
			// findOrCreateRevCommitEntity(ctx, revCommit);
		}

		

		RevCommitEntity prev = res.getRefCommit();
		if (prev != revCommitEntity) {
			LOG.info("update ref " + res + " commitId:" + revCommitEntity);
			res.setRefCommit(revCommitEntity);
			objectIdRepoRefDAO.save(res, 1);
		}
	}

	private void updateGitToEntityRef(SyncCtx ctx, SymbolicRef src, SymbolicRepoRefEntity res) {
		Ref targetRef = src.getTarget();
		if (targetRef == null) {
			LOG.warn("should not occur? null target for SymbolicRef " + src);
			res.setTarget(null);
			symbolicRepoRefDAO.save(res, 1);
			return;
		}
		String targetRefName = targetRef.getName();
		AbstractRepoRefEntity targetRefEntity = ctx.getRef(targetRefName);
		AbstractRepoRefEntity prev = res.getTarget();
		if (prev != targetRefEntity) {
			LOG.info("update SymbolicRef " + res + " target:" + targetRefEntity);
			res.setTarget(targetRefEntity);
			symbolicRepoRefDAO.save(res, 1);
		}
	}

	private static <T extends AbstractRepoRefEntity> Map<String,T> refsToRefByNameMap(Iterable<T> refs) {
		Map<String,T> res = new HashMap<>();
		for(T e : refs) {
			String name = e.getName();
			res.put(name, e);
		}
		return res;
	}

	private static Map<String,Ref> refsToNameMap(Iterable<Ref> refs) {
		Map<String,Ref> res = new HashMap<>();
		for(Ref e : refs) {
			String name = e.getName();
			res.put(name, e);
		}
		return res;
	}

	protected RevCommitEntity findOrCreateRevCommitEntity(SyncCtx ctx, RevCommit src) {
		if (src == null) {
			return null;
		}
		
		final ObjectId commitId = src.getId();
		
		RevCommitEntity res = ctx.sha2revCommitEntities.get(commitId);
		if (res == null) {
			res = new RevCommitEntity();
			res.setCommitId(commitId);
			ctx.sha2revCommitEntities.put(commitId, res);

			git2entity(ctx, src, res);

			LOG.debug("git RevCommit " + commitId);
		} else {
			// update (if sync code changed ..)
			git2entity(ctx, src, res);
		}
		return res;
	}

	private void git2entity(SyncCtx ctx, RevCommit src, RevCommitEntity res) {
		ObjectId commitId = src.getId();
		// JGit Bug ... need to re-read fully the RevCommit ??!!!!
		try {
			src = ctx.revWalk.parseCommit(commitId);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		String shortMessage;
		try {
			shortMessage = src.getShortMessage();
		} catch (NullPointerException e) {
			throw new RuntimeException(e);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("RevCommit " + commitId + " " + shortMessage);
		}
		
		res.setShortMessage(shortMessage);
		res.setFullMessage(src.getFullMessage());

		// may StackOverflow !! do not recurse from here
		// updateRevCommitParents(ctx, src, res);

		res.setCommitTime(src.getCommitTime());


		res.setAuthor(findOrCreatePersonEntity(ctx, src.getAuthorIdent()));
		res.setCommitter(findOrCreatePersonEntity(ctx, src.getCommitterIdent()));
		
		// TODO ...
		// res.setRevTree(findOrCreateRevTree(ctx, src.getTree(), dirSynchroniser));
	}

	protected void updateRevCommitParents(SyncCtx ctx, RevCommit src, RevCommitEntity res) {
		RevCommit[] parents = src.getParents();
		if (parents == null || parents.length == 0) {
			return; // only the initial commit..
		}
		
		// recursive find or create parent commits!
		boolean chg = false;
		List<RevCommitEntity> parentEntities = res.getParents();
		if (parentEntities == null) {
			parentEntities = new ArrayList<>();
			res.setParents(parentEntities);
			chg = true;
		} else if (parentEntities.size() != parents.length) {
			//? update change?? (was not created before)
			chg = true;
		}
		
		for (RevCommit parent : parents) {
			RevCommitEntity parentEntity = findOrCreateRevCommitEntity(ctx, parent);
			if (parentEntity != null) {
				parentEntities.add(parentEntity);
			} // else should not occur?!
		}
		
		if (chg) {
			revCommitDAO.save(res, 1); //do not recuse ... StackOverflow !!
		}
	}

	protected PersonIdentEntity findOrCreatePersonEntity(SyncCtx ctx, PersonIdent src) {
		if (src == null) {
			return null;
		}
		final String email = src.getEmailAddress();
		PersonIdentEntity res = ctx.email2person.get(email);
		if (res == null) {
			res = new PersonIdentEntity();
			res.setEmailAddress(email);
			res.setName(src.getName());
			personDAO.save(res);

			ctx.email2person.put(email, res);
		}
		return res;
	}

}
