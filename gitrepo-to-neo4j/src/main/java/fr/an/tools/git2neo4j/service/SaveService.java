package fr.an.tools.git2neo4j.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.an.tools.git2neo4j.domain.BlobEntity;
import fr.an.tools.git2neo4j.domain.DirEntryEntity;
import fr.an.tools.git2neo4j.domain.RevCommitEntity;
import fr.an.tools.git2neo4j.domain.RevTreeEntity;
import fr.an.tools.git2neo4j.repository.BlobDAO;
import fr.an.tools.git2neo4j.repository.DirEntryDAO;
import fr.an.tools.git2neo4j.repository.DirTreeDAO;
import fr.an.tools.git2neo4j.repository.GitLinkDAO;
import fr.an.tools.git2neo4j.repository.PersonDAO;
import fr.an.tools.git2neo4j.repository.ObjectIdRepoRefDAO;
import fr.an.tools.git2neo4j.repository.RevCommitDAO;
import fr.an.tools.git2neo4j.repository.RevTreeDAO;
import fr.an.tools.git2neo4j.repository.SymLinkDAO;
import fr.an.tools.git2neo4j.repository.SymbolicRepoRefDAO;

@Component
public class SaveService {

	private static final Logger LOG = LoggerFactory.getLogger(SaveService.class);

	@Autowired 
	private ExplicitXAHelper xaHelper;
	
	@Autowired
	private RevCommitDAO revCommitDAO;
	
	@Autowired
	private PersonDAO personDAO;
	
	@Autowired
	private ObjectIdRepoRefDAO repoRefDAO;
	@Autowired
	private SymbolicRepoRefDAO symbolicRepoRefDAO;
	
	@Autowired
	private RevTreeDAO treeDAO;
	@Autowired
	private DirTreeDAO dirTreeDAO;
	@Autowired
	private DirEntryDAO dirEntryDAO;
	@Autowired
	private BlobDAO blobDAO;
	@Autowired
	private SymLinkDAO symLinkDAO;
	@Autowired
	private GitLinkDAO gitLinkDAO;

	
	// @Autowired
	// private MapperFacade mapperFacade;

	// ------------------------------------------------------------------------

	public SaveService() {
	}

	// ------------------------------------------------------------------------

	public Iterable<RevCommitEntity> saveRevCommits(Iterable<RevCommitEntity> e) {
		return revCommitDAO.save(e, 1);
	}
	
	public Iterable<RevTreeEntity> saveTrees(Iterable<RevTreeEntity> e) {
		return treeDAO.save(e, 1);
	}

	public Iterable<DirEntryEntity> saveDirEntries(List<DirEntryEntity> e) {
		return dirEntryDAO.save(e, 1);
	}
	
	public Iterable<BlobEntity> saveBlobs(List<BlobEntity> e) {
		return blobDAO.save(e, 1);
	}


}
