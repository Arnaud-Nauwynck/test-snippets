package fr.an.tools.git2neo4j.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fr.an.tools.git2neo4j.domain.RevCommitEntity;
import fr.an.tools.git2neo4j.repository.GitCommitRepositoy;
import ma.glasnost.orika.MapperFacade;

@Component
@Transactional
public class Git2Neo4JSyncService {

	
	private static final Logger LOG = LoggerFactory.getLogger(Git2Neo4JSyncService.class);
	
	@Autowired
	private GitCommitRepositoy gitCommitRepository;

	@Autowired
	private MapperFacade mapperFacade;
	
	public void syncRepo(Git git) {
		Iterable<RevCommitEntity> revCommitEntities = gitCommitRepository.findAll();
		Map<ObjectId,RevCommitEntity> sha2revCommitEntities = new HashMap<>();
		for(RevCommitEntity e : revCommitEntities) {
			sha2revCommitEntities.put(e.getCommitId(), e);
		}
		
		Iterable<RevCommit> revCommits;
		try {
			revCommits = git.log()
				.setMaxCount(1000)
				.all() // from all refs (=master + branchs ..)
				.call();
		} catch (Exception ex) {
			throw new RuntimeException("Failed to get git commit log history", ex);
		}
		for(RevCommit revCommit : revCommits) {
			System.out.println("RevCommit: " + revCommit + " " + revCommit.getShortMessage());
			findOrCreateRevCommitEntity(sha2revCommitEntities, revCommit);
		}
	}

	private RevCommitEntity findOrCreateRevCommitEntity(Map<ObjectId, RevCommitEntity> sha2revCommitEntities, RevCommit revCommit) {
		if (revCommit == null) {
			return null;
		}
		ObjectId commitId = revCommit.getId();
		RevCommitEntity revCommitEntity = sha2revCommitEntities.get(commitId);
		if (revCommitEntity == null) {
			revCommitEntity = new RevCommitEntity();
			
			revCommitEntity.setCommitId(commitId);
			sha2revCommitEntities.put(commitId, revCommitEntity);
			
			// mapperFacade.map(revCommit, revCommitEntity); // TODO ... MappingException: No converter registered for conversion from RevCommit to Long
			RevCommit[] parents = revCommit.getParents();
			
			if (parents != null) {
				try {
					revCommitEntity.setShortMessage(revCommit.getShortMessage());
					revCommitEntity.setFullMessage(revCommit.getFullMessage());
				} catch(NullPointerException ex) {
					LOG.warn("Failed revCommit.getShortMessage"); // only for initial commit??
				}
			}
			
			if (parents != null) {
				// recursive find or create commit!
				List<RevCommitEntity> parentEntities = new ArrayList<>();
				for(RevCommit parent : parents) {
					RevCommitEntity parentEntity = findOrCreateRevCommitEntity(sha2revCommitEntities, parent);
					if (parentEntity != null) {
						parentEntities.add(parentEntity);
					}// else should not occur?!
				}
				revCommitEntity.setParents(parentEntities);
			}
			
			gitCommitRepository.save(revCommitEntity);
		}
		return revCommitEntity;
	}

	
}
