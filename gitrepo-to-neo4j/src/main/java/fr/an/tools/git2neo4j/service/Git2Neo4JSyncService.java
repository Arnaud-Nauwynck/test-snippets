package fr.an.tools.git2neo4j.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.MutableObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fr.an.tools.git2neo4j.domain.PersonIdentEntity;
import fr.an.tools.git2neo4j.domain.RevCommitEntity;
import fr.an.tools.git2neo4j.domain.RevTreeEntity;
import fr.an.tools.git2neo4j.repository.PersonIdentRepository;
import fr.an.tools.git2neo4j.repository.RevCommitRepository;
import fr.an.tools.git2neo4j.repository.RevTreeRepository;

@Component
public class Git2Neo4JSyncService {

	private static final Logger LOG = LoggerFactory.getLogger(Git2Neo4JSyncService.class);

	@Autowired
	private RevCommitRepository revCommitRepository;
	@Autowired
	private PersonIdentRepository personIdentRepository;
	@Autowired
	private RevTreeRepository revTreeRepository;

	// @Autowired
	// private MapperFacade mapperFacade;

	// ------------------------------------------------------------------------

	public Git2Neo4JSyncService() {
	}

	// ------------------------------------------------------------------------

	public static class SyncCtx {
		Repository repository;
		RevWalk revWalk;

		Map<ObjectId, RevCommitEntity> sha2revCommitEntities = new HashMap<>();
		Map<ObjectId, RevTreeEntity> sha2revTreeEntities = new HashMap<>();
		Map<String, PersonIdentEntity> email2person = new HashMap<>();

		public SyncCtx(Repository repository, Iterable<RevCommitEntity> revCommitEntities,
				Iterable<PersonIdentEntity> personEntities, Iterable<RevTreeEntity> revTreeEntities) {
			this.repository = repository;
			this.revWalk = new RevWalk(repository);
			for (RevCommitEntity e : revCommitEntities) {
				sha2revCommitEntities.put(e.getCommitId(), e);
			}
			for (PersonIdentEntity e : personEntities) {
				email2person.put(e.getEmailAddress(), e);
			}
			for (RevTreeEntity e : revTreeEntities) {
				sha2revTreeEntities.put(e.getCommitId(), e);
			}
		}
	}

	@Transactional
	public void syncRepo(Git git) {
		LOG.info("sync git repo ...");
		Iterable<RevCommitEntity> revCommitEntities = revCommitRepository.findAll();
		Iterable<PersonIdentEntity> personEntities = personIdentRepository.findAll();
		Iterable<RevTreeEntity> revTreeEntities = revTreeRepository.findAll();
		SyncCtx ctx = new SyncCtx(git.getRepository(), revCommitEntities, personEntities, revTreeEntities);

		Iterable<RevCommit> revCommits;
		try {
			revCommits = git.log().all() // from all refs(=master + branchs ..)
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
		for (RevCommit revCommit : reverseOrderRevCommits) {
			try {
				RevCommitEntity tmpres = findOrCreateRevCommitEntity(ctx, revCommit);
				tmpRevCommitEntities.put(revCommit, tmpres);
			} catch(Exception ex) {
				LOG.error("Failed", ex);
			}
		}

//		for (Map.Entry<RevCommit,RevCommitEntity>  e : tmpRevCommitEntities.entrySet()) {
//			RevCommit revCommit = e.getKey();
//			RevCommitEntity revCommitEntity = e.getValue();
//			updateRevCommitParents(ctx, revCommit, revCommitEntity);
//			// revCommitRepository.save(revCommitEntity);
//		}

		
		LOG.info("done sync git repo");
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

			LOG.info("git RevCommit " + commitId);
			revCommitRepository.save(res);
		} else {
			// update (if sync code changed ..)
			git2entity(ctx, src, res);
			revCommitRepository.save(res);
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
		LOG.info("RevCommit " + commitId + " " + shortMessage);
		
		res.setShortMessage(shortMessage);
		res.setFullMessage(src.getFullMessage());

		// may StackOverflow ...
		updateRevCommitParents(ctx, src, res);

		res.setCommitTime(src.getCommitTime());


		res.setAuthorIdent(findOrCreatePersonEntity(ctx, src.getAuthorIdent()));
		res.setCommitterIdent(findOrCreatePersonEntity(ctx, src.getCommitterIdent()));
		
		res.setRevTree(findOrCreateRevTree(ctx, src.getTree()));
	}

	protected void updateRevCommitParents(SyncCtx ctx, RevCommit src, RevCommitEntity res) {
		RevCommit[] parents = src.getParents();

		// recursive find or create parent commits!
		List<RevCommitEntity> parentEntities = new ArrayList<>();
		for (RevCommit parent : parents) {
			RevCommitEntity parentEntity = findOrCreateRevCommitEntity(ctx, parent);
			if (parentEntity != null) {
				parentEntities.add(parentEntity);
			} // else should not occur?!
		}
		res.setParents(parentEntities);
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
			personIdentRepository.save(res);
		}
		return res;
	}

	protected RevTreeEntity findOrCreateRevTree(SyncCtx ctx, RevTree src) {
		if (src == null) {
			return null;
		}
		final ObjectId commitId = src.getId();
		RevTreeEntity res = ctx.sha2revTreeEntities.get(commitId);
		if (res == null) {
			res = new RevTreeEntity();
			res.setCommitId(commitId);
			ctx.sha2revTreeEntities.put(commitId, res);

			git2entity(ctx, src, res);

			revTreeRepository.save(res);
		} else {
			// update (if sync code changed ..)
			// git2entity(ctx, src, res);
		}
		return res;
	}

	private void git2entity(SyncCtx ctx, RevTree src, RevTreeEntity res) {
		MutableObjectId moid = new MutableObjectId(); 
		TreeWalk treeWalk = new TreeWalk(ctx.repository);
		try {
			ObjectId revTreeId = src.getId();
			String revTreeSha = revTreeId.name(); 
			treeWalk.addTree(revTreeId);
			treeWalk.setRecursive(false);
			while (treeWalk.next()) {
				int treeCount = treeWalk.getTreeCount();
				// treeWalk.getTree(nth, clazz);
				
				FileMode fileMode = treeWalk.getFileMode();
				String path = treeWalk.getPathString();
				// System.out.println("revTree " + revTreeSha + " - entry: " + path);
				// System.out.println("found: " + treeWalk.getPathString());
				
				//TODO
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} finally {
			treeWalk.close();
		}

	}

}
