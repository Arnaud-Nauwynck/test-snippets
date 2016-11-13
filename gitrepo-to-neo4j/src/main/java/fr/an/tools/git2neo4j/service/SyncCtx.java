package fr.an.tools.git2neo4j.service;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevWalk;

import fr.an.tools.git2neo4j.domain.AbstractRepoRefEntity;
import fr.an.tools.git2neo4j.domain.ObjectIdRepoRefEntity;
import fr.an.tools.git2neo4j.domain.PersonIdentEntity;
import fr.an.tools.git2neo4j.domain.RevCommitEntity;
import fr.an.tools.git2neo4j.domain.RevTreeEntity;
import fr.an.tools.git2neo4j.domain.SymbolicRepoRefEntity;

public class SyncCtx {

	Git git;
	Repository repository;
	RevWalk revWalk;
	SaveService saveService;

	Map<String, SymbolicRepoRefEntity> symbolicRefEntities = new HashMap<>();
	Map<String, ObjectIdRepoRefEntity> objecIdRefEntities = new HashMap<>();
	Map<ObjectId, RevCommitEntity> sha2revCommitEntities = new HashMap<>();
	Map<String, PersonIdentEntity> email2person = new HashMap<>();

	Map<ObjectId, RevTreeEntity> sha2revTreeEntities = new WeakHashMap<>();

	public SyncCtx(Git git, SaveService saveService, Map<String, SymbolicRepoRefEntity> symbolicRefEntities,
			Map<String, ObjectIdRepoRefEntity> objecIdRefEntities, Iterable<RevCommitEntity> revCommitEntities,
			Iterable<PersonIdentEntity> personEntities) {
		this.git = git;
		this.repository = git.getRepository();
		this.revWalk = new RevWalk(repository);
		this.saveService = saveService;
		if (symbolicRefEntities != null) {
			this.symbolicRefEntities.putAll(symbolicRefEntities);
		}
		if (objecIdRefEntities != null) {
			this.objecIdRefEntities.putAll(objecIdRefEntities);
		}
		putRevCommits(revCommitEntities);
		for (PersonIdentEntity e : personEntities) {
			email2person.put(e.getEmailAddress(), e);
		}
	}

	private void putRevCommits(Iterable<RevCommitEntity> revCommitEntities) {
		for (RevCommitEntity e : revCommitEntities) {
			sha2revCommitEntities.put(e.getCommitId(), e);
		}
	}

	public AbstractRepoRefEntity getRef(String refName) {
		AbstractRepoRefEntity res = symbolicRefEntities.get(refName);
		if (res == null) {
			res = objecIdRefEntities.get(refName);
		}
		return res;
	}

}