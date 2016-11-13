package fr.an.tools.git2neo4j.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.MutableObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.an.tools.git2neo4j.domain.BlobEntity;
import fr.an.tools.git2neo4j.domain.DirEntryEntity;
import fr.an.tools.git2neo4j.domain.DirTreeEntity;
import fr.an.tools.git2neo4j.domain.GitLinkEntity;
import fr.an.tools.git2neo4j.domain.RevTreeEntity;
import fr.an.tools.git2neo4j.domain.SymLinkEntity;
import fr.an.tools.git2neo4j.repository.BlobDAO;
import fr.an.tools.git2neo4j.repository.DirEntryDAO;
import fr.an.tools.git2neo4j.repository.DirTreeDAO;
import fr.an.tools.git2neo4j.repository.GitLinkDAO;
import fr.an.tools.git2neo4j.repository.RevTreeDAO;
import fr.an.tools.git2neo4j.repository.SymLinkDAO;

@Component
public class DirTreeSyncService {

	private static final Logger LOG = LoggerFactory.getLogger(DirTreeSyncService.class);

	@Autowired 
	private ExplicitXAHelper xaHelper;

	@Autowired 
	private SaveService saveService;

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

	@Autowired
	private RevTreeToEntityScanner revTreeToEntityScanner;
	
	private int batchSizeUpdateDirTree = 50;
	
	// ------------------------------------------------------------------------

	public DirTreeSyncService() {
	}

	// ------------------------------------------------------------------------


	
	private void recursiveUpdateDirTrees(SyncCtx ctx) {
		if (! ctx.dirTreeToUpdate.isEmpty()) {
			while(! ctx.dirTreeToUpdate.isEmpty()) {
				LOG.info("remain " + ctx.dirTreeToUpdate.size() + " DirTree(s) to recursive update");
				Map<RevTree,DirTreeEntity> tmpDirTreeToUpdate = ctx.copyAndClearDirTreeToUpdate();
				// split by batch of batchSizeUpdateDirTree(=500) .. then commit separatly
				List<Map<RevTree,DirTreeEntity>> batchDirTreeToUpdate = splitBatchEntries(tmpDirTreeToUpdate, batchSizeUpdateDirTree);
				for(Map<RevTree,DirTreeEntity> splitBatch : batchDirTreeToUpdate) {
					xaHelper.doInXA(() -> batchRecursiveUpdateDirTrees(ctx, splitBatch));
					System.out.print(".");
				}
			}
		}
	}

	private static <K,V> List<Map<K,V>> splitBatchEntries(Map<K,V> map, int batchSize) {
		List<Map<K,V>> res = new ArrayList<>();
		int count = 0;
		Map<K,V> currMap = new LinkedHashMap<>();
		for(Map.Entry<K, V> e : map.entrySet()) {
			currMap.put(e.getKey(), e.getValue());
			if (++count >= batchSize) {
				count = 0;
				res.add(currMap);
				currMap = new LinkedHashMap<>();
			}
		}
		return res;
	}

	//?? @Transactional
	protected void batchRecursiveUpdateDirTrees(SyncCtx ctx, Map<RevTree,DirTreeEntity> tmpDirTreeToUpdate) {
		for(Map.Entry<RevTree,DirTreeEntity> e : tmpDirTreeToUpdate.entrySet()) {
			RevTree revTree = e.getKey();
			DirTreeEntity revTreeEntity = e.getValue();
			dirSynchroniser.git2entity(ctx, revTree, revTreeEntity);
		}
		ctx.flush();
	}

	
	

	public <TEntity extends RevTreeEntity> RevTreeEntity findOrCreateRevTree(SyncCtx ctx, RevTree src, FileMode fileMode) {
		RevTreeEntity res;
		int fileModeBits = fileMode.getObjectType();
		if (FileMode.TREE.equals(fileModeBits)) {
			res = findOrCreateRevTree(ctx, src, dirSynchroniser);
		} else if (FileMode.REGULAR_FILE.equals(fileModeBits)) {
			res = findOrCreateRevTree(ctx, src, blobSynchroniser);
		} else if (FileMode.SYMLINK.equals(fileModeBits)) {
			res = findOrCreateRevTree(ctx, src, symLinkSynchroniser);
		} else if (FileMode.GITLINK.equals(fileModeBits)) {
			res = findOrCreateRevTree(ctx, src, gitLinkSynchroniser);
		} else {
			res = null; // should not occurs
		}
		return res;
	}
	

	protected static abstract class RevTreeEntitySynchroniser<TEntity extends RevTreeEntity> {
		public abstract TEntity createEntity();
		public abstract void git2entity(SyncCtx ctx, RevTree src, TEntity res);
	}
	
	protected <TEntity extends RevTreeEntity> RevTreeEntity findOrCreateRevTree(SyncCtx ctx, RevTree src, RevTreeEntitySynchroniser<TEntity> callback) {
		if (src == null) {
			return null;
		}
		final ObjectId commitId = src.getId();
		@SuppressWarnings("unchecked")
		TEntity res = (TEntity) ctx.sha2revTreeEntities.get(commitId);
		if (res == null) {
			res = callback.createEntity();
			
			res.setObjectId(commitId);
			ctx.sha2revTreeEntities.put(commitId, res);

			// TODO recurse Dir-DirEntry/Blob/SymLink/GitLink  ???? not from here ????
			callback.git2entity(ctx, src, res);
			
			// ctx.save(res);
			res = treeDAO.save(res);
		} else {
			// update (if sync code changed ..)
			// callback.git2entity(src, res);
		}
		return res;
	}

		
	protected class DirSynchroniser extends RevTreeEntitySynchroniser<DirTreeEntity> {

		@Override
		public DirTreeEntity createEntity() {
			return new DirTreeEntity();
		}

		@Override
		public void git2entity(SyncCtx ctx, RevTree src, DirTreeEntity res) {
			MutableObjectId moid = new MutableObjectId(); 
			
			try (TreeWalk treeWalk = new TreeWalk(ctx.repository)) {
				ObjectId revTreeId = src.getId();
				// String revTreeSha = revTreeId.name(); 
				treeWalk.addTree(revTreeId);
				treeWalk.setRecursive(false);

				List<DirEntryEntity> resEntries = res.getEntries();
				if (resEntries == null) {
					resEntries = new ArrayList<>();
					res.setEntries(resEntries);
				}
				Map<String,DirEntryEntity> remainEntryByName = new HashMap<>();
				for(DirEntryEntity e : resEntries) {
					remainEntryByName.put(e.getName(), e);
				}
				while (treeWalk.next()) {
					FileMode fileMode = treeWalk.getFileMode();
					String path = treeWalk.getPathString();
					treeWalk.getObjectId(moid, 0);
					
					DirEntryEntity entryEntity = remainEntryByName.remove(path);
					if (entryEntity == null) {
						entryEntity = new DirEntryEntity();
						entryEntity.setName(path);
						entryEntity.setFileMode(fileMode.getBits());
						resEntries.add(entryEntity);
						// LOG.debug("revTree entry: " + fileMode + " '" + path + "' dirEntry:" + entryEntity);
					}
					
					// recurse ??
				}
				if (! remainEntryByName.isEmpty()) {
					for(DirEntryEntity e : remainEntryByName.values()) {
						resEntries.remove(e);
					}
				}
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}

		}

	}

	protected class BlobSynchroniser extends RevTreeEntitySynchroniser<BlobEntity> {

		@Override
		public BlobEntity createEntity() {
			return new BlobEntity();
		}

		@Override
		public void git2entity(SyncCtx ctx, RevTree src, BlobEntity res) {
		}
		
	}

	protected class SymLinkSynchroniser extends RevTreeEntitySynchroniser<SymLinkEntity> {

		@Override
		public SymLinkEntity createEntity() {
			return new SymLinkEntity();
		}

		@Override
		public void git2entity(SyncCtx ctx, RevTree src, SymLinkEntity res) {
		}
		
	}
	
	protected class GitLinkSynchroniser extends RevTreeEntitySynchroniser<GitLinkEntity> {

		@Override
		public GitLinkEntity createEntity() {
			return new GitLinkEntity();
		}

		@Override
		public void git2entity(SyncCtx ctx, RevTree src, GitLinkEntity res) {
		}
				
	}
			
	private DirSynchroniser dirSynchroniser = new DirSynchroniser();
	private BlobSynchroniser blobSynchroniser = new BlobSynchroniser();
	private SymLinkSynchroniser symLinkSynchroniser = new SymLinkSynchroniser();
	private GitLinkSynchroniser gitLinkSynchroniser = new GitLinkSynchroniser();


}
