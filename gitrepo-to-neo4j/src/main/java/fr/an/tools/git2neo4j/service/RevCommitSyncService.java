package fr.an.tools.git2neo4j.service;

import static org.eclipse.jgit.lib.RefDatabase.ALL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectIdRef;
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
import fr.an.tools.git2neo4j.domain.DirTreeEntity;
import fr.an.tools.git2neo4j.domain.ObjectIdRepoRefEntity;
import fr.an.tools.git2neo4j.domain.PersonIdentEntity;
import fr.an.tools.git2neo4j.domain.RevCommitEntity;
import fr.an.tools.git2neo4j.domain.SymbolicRepoRefEntity;
import fr.an.tools.git2neo4j.repository.PersonDAO;
import fr.an.tools.git2neo4j.repository.RepoRefDAO;
import fr.an.tools.git2neo4j.repository.RevCommitDAO;
import fr.an.tools.git2neo4j.repository.SymbolicRepoRefDAO;

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
	private SymbolicRepoRefDAO symbolicRepoRefDAO;


	// ------------------------------------------------------------------------

	public RevCommitSyncService() {
	}

	// ------------------------------------------------------------------------

	public void syncRepo(Git git) {
		LOG.info("sync git repo ...");
		SyncCtx ctx = xaHelper.doInXA(() -> preloadSyncCtx(git));
		
		Map<Ref, AbstractRepoRefEntity> refs = xaHelper.doInXA(() -> findOrCreateAllRefs(ctx));
				
		xaHelper.doInXA(() -> findOrCreateRevCommits(ctx));
		
		// update refs (commitIds / targetRefs)
		xaHelper.doInXA(() -> updateRefs(ctx, refs));
		
		// OutOfMemoryError + very very slow ...
		// update RevTree
		// recursiveUpdateDirTrees(ctx);
	

		
		LOG.info("done sync git repo");
	}

	//?? //?? @Transactional
	protected SyncCtx preloadSyncCtx(Git git) {
		LOG.info("preloading symbolicRef");
		Map<String,SymbolicRepoRefEntity> symbolicRefEntities = refsToRefByNameMap(symbolicRepoRefDAO.findAll());
		LOG.info("preloading objectIdRef");
		Map<String,ObjectIdRepoRefEntity> objecIdRefEntities = refsToRefByNameMap(repoRefDAO.findAll());
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
				
				ctx.dirTreeToUpdate.put(revCommit.getTree(), (DirTreeEntity) tmpres.getRevTree());
			} catch(Exception ex) {
				LOG.error("Failed", ex);
			}
			if (progressCount-- <= 0) {
				progressCount = progressFreq;
				System.out.print(".");
			}
		}
		System.out.println();
		LOG.info("flush save Revcommit + RevTree");
		ctx.flush();
		
		for (Map.Entry<RevCommit,RevCommitEntity>  e : tmpRevCommitEntities.entrySet()) {
			RevCommit revCommit = e.getKey();
			RevCommitEntity revCommitEntity = e.getValue();
			updateRevCommitParents(ctx, revCommit, revCommitEntity);
		}
		
		ctx.flush();

		return tmpRevCommitEntities;
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
			LOG.info("ref: " + ref);
			AbstractRepoRefEntity refEntity = findOrCreateRef(ctx, ref);
			res.put(ref, refEntity);
		}
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
				ctx.symbolicRefEntities.put(refName, entity);
				symbolicRepoRefDAO.save(entity, 1);
			}
			res = entity;
		} else if (ref instanceof ObjectIdRef) {
			ObjectIdRef oidRef = (ObjectIdRef) ref;
			String refName = oidRef.getName();
			ObjectIdRepoRefEntity entity = ctx.objecIdRefEntities.get(refName);
			if (entity == null) {
				entity = new ObjectIdRepoRefEntity();
				entity.setName(refName);
				ctx.objecIdRefEntities.put(refName, entity);
				repoRefDAO.save(entity, 1);
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
		ctx.flush();
	}
	
	private void updateGitToEntityRef(SyncCtx ctx, ObjectIdRef src, ObjectIdRepoRefEntity res) {
		ObjectId oid = src.getObjectId();
		RevCommitEntity revCommitEntity = ctx.sha2revCommitEntities.get(oid);
		if (revCommitEntity == null) {
			LOG.warn("Should not occurs: revCommit " + oid + " not found for ObjectIdRef " + src);
			// findOrCreateRevCommitEntity(ctx, revCommit);
		}
		RevCommitEntity prev = res.getRefCommit();
		if (prev != revCommitEntity) {
			LOG.info("update ref " + res + " commitId:" + revCommitEntity);
			res.setRefCommit(revCommitEntity);
			repoRefDAO.save(res, 1);
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
			ctx.save(res);
		} else {
			// update (if sync code changed ..)
			git2entity(ctx, src, res);
		}
		return res;
	}

	private void git2entity(SyncCtx ctx, RevCommit src, RevCommitEntity res) {
		// mapperFacade.map(revCommit, revCommitEntity); // TODO ...
		// MappingException: No converter registered for conversion from RevCommit to Long

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
			chg = true; //? update change?? (was not created before) 
		}
		
		for (RevCommit parent : parents) {
			RevCommitEntity parentEntity = findOrCreateRevCommitEntity(ctx, parent);
			if (parentEntity != null) {
				parentEntities.add(parentEntity);
			} // else should not occur?!
		}
		
		if (chg) {
			// save??
			ctx.save(res);
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

			ctx.email2person.put(email, res);
			personDAO.save(res);
		}
		return res;
	}

}
