package fr.an.tools.git2neo4j.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
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
import fr.an.tools.git2neo4j.repository.RevTreeDAO;
import fr.an.tools.git2neo4j.utils.ObjectIdUtils;

@Component
public class RevTreeToEntityScanner {
	
	private static final Logger LOG = LoggerFactory.getLogger(RevTreeToEntityScanner.class);
	
	@Autowired 
	protected RevTreeDAO revTreeDAO;
	
	public static class ScanRevTreeResult {
		RevTreeEntity rootRevTree;
		Map<ObjectId,RevTreeEntity> revTree = new HashMap<>();
		Map<String,RevTreeEntity> path = new LinkedHashMap<>(); 
	}
	
	public static class ScanRevTreeMissingResult {
		ScanRevTreeResult scanRes = new ScanRevTreeResult();
		Map<ObjectId,MissingTree> missingTrees = new HashMap<>();
	}
	
	protected static class MissingTree {
		RevTreeEntity resEntity;
		ObjectId objectId;
		// FileMode ??  dir / blob / symLink / gitLink
		List<MissingPathEntry> missingPathEntries = new ArrayList<>(1);
		public MissingTree(RevTreeEntity resEntity, ObjectId objectId) {
			this.resEntity = resEntity;
			this.objectId = objectId;
		}
	}
	
	protected static class MissingPathEntry {
		final MissingTree owner;
		DirTreeEntity parentEntity;
		FileMode fileMode;
		String name;
		// String path;
		
		public MissingPathEntry(MissingTree owner, 
				DirTreeEntity parentEntity, FileMode fileMode, String name) {
			this.owner = owner;
			this.parentEntity = parentEntity;
			this.fileMode = fileMode;
			this.name = name;
		}
	}
	
	/**
	 * recursive synchronise Git RevTree child Dir/Blob/.. to Neo4J RevTreeEntity (Dir,Blob/..) - DirEntryEntity
	 */
	public ScanRevTreeResult recursiveSyncRevTree(Repository repository, ObjectId rootTreeId,
			Map<ObjectId, RevTreeEntity> sha2revTreeEntities) {

		if (sha2revTreeEntities == null) {
			List<RevTreeEntity> tmpChild = revTreeDAO.findRecursiveChildFileBySHA1(rootTreeId.name());
			sha2revTreeEntities = ObjectIdUtils.lsToMap(tmpChild);
		}
		
		ScanRevTreeMissingResult scanMissingRes = recursiveScanMissingRevTree(repository, rootTreeId, sha2revTreeEntities);
		
		ScanRevTreeResult res = syncRevTree(repository, rootTreeId, scanMissingRes);
		
		return res;
	}
	
	/**
	 * 
	 */
	public ScanRevTreeMissingResult recursiveScanMissingRevTree(Repository repository, ObjectId rootTreeId,
			Map<ObjectId, RevTreeEntity> sha2revTreeEntities) {

		ScanRevTreeMissingResult res = new ScanRevTreeMissingResult();
		Map<ObjectId,MissingTree> missingTrees = res.missingTrees;
		Map<ObjectId,RevTreeEntity> resRevTree = res.scanRes.revTree;
		
		Map<String,DirTreeEntity> path2DirEntities = new HashMap<>();
		
		DirTreeEntity rootDirEntity = (DirTreeEntity) sha2revTreeEntities.get(rootTreeId);
		if (rootDirEntity == null) {
			rootDirEntity = new DirTreeEntity();
			rootDirEntity.setObjectId(rootTreeId);
			resRevTree.put(rootTreeId, rootDirEntity);
		}
		res.scanRes.rootRevTree = rootDirEntity;
		
		System.out.println("recursiveScan revTree " + rootTreeId + " {");
		int treePathCount = 0; 
		try (TreeWalk treeWalk = new TreeWalk(repository)) {
			treeWalk.addTree(rootTreeId);

			treeWalk.setRecursive(false);
			while (treeWalk.next()) {
				treePathCount++;
				FileMode fileMode = treeWalk.getFileMode();
				String name = treeWalk.getNameString();
				String path = treeWalk.getPathString();
				ObjectId objectId = treeWalk.getObjectId(0);
				int depth = treeWalk.getDepth();
			    if (treeWalk.isSubtree()) {
			        // System.out.println("dir: " + path);
			        // cf next ... treeWalk.enterSubtree();
			    } else {
			        // System.out.println("file: " + path);
			    }
				
				// System.out.println("" + fileMode + " " + treeWalk.getPathString() + " " + objectId.name());
				
				RevTreeEntity foundTreeEntity = sha2revTreeEntities.get(objectId);
				if (foundTreeEntity == null) {
					foundTreeEntity = resRevTree.get(objectId);

					if (foundTreeEntity == null) {
				
						DirTreeEntity parentEntity = null; // TODO
						if (depth > 0) {
							int pathSep = path.lastIndexOf("/");
							if (pathSep == -1) {
								throw new RuntimeException(); 
							}
							String parentPath = path.substring(0, pathSep);
							parentEntity = path2DirEntities.get(parentPath);
							if (parentEntity == null) {
								// TODO LOG.error("parent not found " + parentPath);
							}
						} else {
							parentEntity = rootDirEntity;
						}
						
						RevTreeEntity tmpEntity = createTreeEntity(fileMode, objectId);
						res.scanRes.revTree.put(objectId, foundTreeEntity);
						
						if (tmpEntity instanceof DirTreeEntity) {
							path2DirEntities.put(path, (DirTreeEntity) tmpEntity);
						}
						MissingTree missingTree = missingTrees.get(objectId);
						if (missingTree == null) {
							missingTree = new MissingTree(tmpEntity, objectId);
							missingTrees.put(objectId, missingTree);
						}
						missingTree.missingPathEntries.add(new MissingPathEntry(missingTree, parentEntity, fileMode, name));
					}
				}
				
				if (treeWalk.isSubtree()) {
			        // System.out.println("dir: " + path);
			        treeWalk.enterSubtree();
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		LOG.info("recursiveScan revTree " + rootTreeId + " => " + treePathCount + " treePaths, " + missingTrees.size() + " missing Entity trees");
		return res;
	}
	
	/**
	 * 
	 */
	protected ScanRevTreeResult syncRevTree(Repository repository, ObjectId rootTreeId,
			ScanRevTreeMissingResult scanMissingRes) {
		ScanRevTreeResult res = scanMissingRes.scanRes;
		Map<ObjectId, MissingTree> missingTrees = scanMissingRes.missingTrees;
		
		List<String> sha1s = ObjectIdUtils.toNameList(missingTrees.keySet());
		List<RevTreeEntity> cacheFoundRevTrees = revTreeDAO.findBySHA1s(sha1s);
		if (! cacheFoundRevTrees.isEmpty()) {
			LOG.info("found " + cacheFoundRevTrees + " exiting tree(s) by SHA1, which where not already connected to parent tree");
			for(RevTreeEntity treeEntity : cacheFoundRevTrees) {
				ObjectId oid = treeEntity.getObjectId();
				res.revTree.put(oid, treeEntity);
				// missingTrees.remove(oid);
			}
		}
		
		if (! missingTrees.isEmpty()) {
			// create + connect missing RevTree Entities ... 
			
			for(MissingTree missingTree : missingTrees.values()) {
				ObjectId objectId = missingTree.objectId;
				RevTreeEntity treeEntity = missingTree.resEntity;
				// revTreeDAO.save(treeEntity); // useless?
				res.revTree.put(objectId, treeEntity);
				
				for(MissingPathEntry e : missingTree.missingPathEntries) {
					DirTreeEntity parentDirEntity = e.parentEntity;
					if (parentDirEntity != null) {
						DirEntryEntity childEntry = new DirEntryEntity(parentDirEntity, treeEntity, e.name, e.fileMode.getBits());
						parentDirEntity.addEntry(childEntry);
					} else {
//						LOG.error("missing parent for entry?");
					}
				}
			}
			
			// recursive save
			DirTreeEntity rootDirEntity = (DirTreeEntity) res.revTree.get(rootTreeId);
			// LOG.info("save Tree ...");
			long saveStartTime = System.currentTimeMillis();
			
			if (rootDirEntity != null) {
				revTreeDAO.save(rootDirEntity);
			} else {
				LOG.error("rootDirEntity null");
			}
			
			long saveMillis = System.currentTimeMillis() - saveStartTime;
			if (saveMillis > 200) {
				LOG.info("... done save Tree, took " + saveMillis + " ms");
			}
			
		}
		
			
//		// check DirTree have correct child list: remove extra! (should not occur..)
//		try (TreeWalk treeWalk = new TreeWalk(repository)) {
//			treeWalk.addTree(rootTreeId);
//			treeWalk.setRecursive(true);
//
//			Map<String,List<RevTreeEntity>> remainPath2RevTrees = new HashMap<>(); 
//			while (treeWalk.next()) {
//				FileMode fileMode = treeWalk.getFileMode();
//				// String name = treeWalk.getNameString();
//				ObjectId objectId = treeWalk.getObjectId(0);
//				// System.out.println("" + fileMode + " " + treeWalk.getPathString() + " " + objectId.name());
//				RevTreeEntity treeEntity = res.revTree.get(objectId);
//				if (treeEntity != null) {
//					
//				}
//			}
//		} catch (IOException ex) {
//			throw new RuntimeException(ex);
//		}
			
		return res;
	}


	
	
	
	
	public static RevTreeEntity createTreeEntity(FileMode fileMode, ObjectId objectId) {
		RevTreeEntity res;
		final int objType = fileMode.getObjectType();
		final int fileModeBits = fileMode.getBits();
		switch(objType) {
		case Constants.OBJ_TREE:
			res = new DirTreeEntity();
			break;
		case Constants.OBJ_BLOB:
			if (FileMode.REGULAR_FILE.equals(fileModeBits) || FileMode.EXECUTABLE_FILE.equals(fileModeBits)) {
				res = new BlobEntity();
			} else if (FileMode.SYMLINK.equals(fileModeBits)) {
				res = new SymLinkEntity();
			} else {
				throw new RuntimeException("should not occurs");
			}
			break;
		case Constants.OBJ_COMMIT:
			res = new GitLinkEntity();
			break;
		default:
			throw new RuntimeException("should not occurs");
		}
		res.setObjectId(objectId);
		return res;
	}
	
}
