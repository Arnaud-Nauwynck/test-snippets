package fr.an.tools.git2neo4j.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
@Transactional
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
		Map<ObjectId, RevCommitEntity> sha2revCommitEntities = new HashMap<>();
		Map<ObjectId, RevTreeEntity> sha2revTreeEntities = new HashMap<>();
		Map<String, PersonIdentEntity> email2person = new HashMap<>();

		public SyncCtx(Repository repository, Iterable<RevCommitEntity> revCommitEntities,
				Iterable<PersonIdentEntity> personEntities, Iterable<RevTreeEntity> revTreeEntities) {
			this.repository = repository;
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

	public void syncRepo(Git git) {
		LOG.info("sync git repo ...");
		Iterable<RevCommitEntity> revCommitEntities = revCommitRepository.findAll();
		Iterable<PersonIdentEntity> personEntities = personIdentRepository.findAll();
		Iterable<RevTreeEntity> revTreeEntities = revTreeRepository.findAll();
		SyncCtx ctx = new SyncCtx(git.getRepository(), revCommitEntities, personEntities, revTreeEntities);

		Iterable<RevCommit> revCommits;
		try {
			revCommits = git.log().setMaxCount(1000).all() // from all refs
															// (=master +
															// branchs ..)
					.call();
		} catch (Exception ex) {
			throw new RuntimeException("Failed to get git commit log history", ex);
		}

		for (RevCommit revCommit : revCommits) {
			System.out.println("RevCommit: " + revCommit + " " + revCommit.getShortMessage());
			findOrCreateRevCommitEntity(ctx, revCommit);
		}

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

			revCommitRepository.save(res);
		} else {
			// update (if sync code changed ..)
			git2entity(ctx, src, res);
		}
		return res;
	}

	private void git2entity(SyncCtx ctx, RevCommit src, RevCommitEntity res) {
		// mapperFacade.map(revCommit, revCommitEntity); // TODO ...
		// MappingException: No converter registered for conversion from
		// RevCommit to Long
		RevCommit[] parents = src.getParents();

		if (parents == null) {
			// initial commit
			return;
		}

		// recursive find or create parent commits!
		List<RevCommitEntity> parentEntities = new ArrayList<>();
		for (RevCommit parent : parents) {
			RevCommitEntity parentEntity = findOrCreateRevCommitEntity(ctx, parent);
			if (parentEntity != null) {
				parentEntities.add(parentEntity);
			} // else should not occur?!
		}
		res.setParents(parentEntities);

		res.setCommitTime(src.getCommitTime());

		res.setShortMessage(src.getShortMessage());
		res.setFullMessage(src.getFullMessage());

		res.setAuthorIdent(findOrCreatePersonEntity(ctx, src.getAuthorIdent()));
		res.setCommitterIdent(findOrCreatePersonEntity(ctx, src.getCommitterIdent()));
		
		res.setRevTree(findOrCreateRevTree(ctx, src.getTree()));
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
		TreeWalk treeWalk = new TreeWalk(ctx.repository);
		MutableObjectId moid = new MutableObjectId(); 
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
				System.out.println("revTree " + revTreeSha + " - entry: " + path);
				// System.out.println("found: " + treeWalk.getPathString());
				
				//TODO
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

	}

}
