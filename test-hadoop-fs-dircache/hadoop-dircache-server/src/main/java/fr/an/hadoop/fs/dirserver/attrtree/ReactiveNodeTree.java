package fr.an.hadoop.fs.dirserver.attrtree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import fr.an.attrtreestore.api.NodeTree;
import fr.an.attrtreestore.api.attrinfo.AttrInfo;
import fr.an.attrtreestore.api.attrinfo.AttrInfoRegistry;
import fr.an.attrtreestore.impl.Node;
import fr.an.attrtreestore.storage.AttrInfoIndexes;
import fr.an.hadoop.fs.dirserver.attrtree.impl.MemCacheEvictNodeVisitor;
import fr.an.hadoop.fs.dirserver.attrtree.impl.ResolvedNodePath;
import fr.an.hadoop.fs.dirserver.dto.MountedDirDTO;
import fr.an.hadoop.fs.dirserver.dto.NodeTreeConfDTO;
import fr.an.hadoop.fs.dirserver.dto.NodeTreeConfDTO.NodeTreeMountDirConfDTO;
import fr.an.hadoop.fs.dirserver.dto.TreePathSubscriptionId;
import fr.an.hadoop.fs.dirserver.fsdata.DirEntryNameAndType;
import fr.an.hadoop.fs.dirserver.fsdata.NodeFsData;
import fr.an.hadoop.fs.dirserver.fsdata.NodeFsData.DirNodeFsData;
import fr.an.hadoop.fs.dirserver.fsdata.NodeFsData.FileNodeFsData;
import fr.an.hadoop.fs.dirserver.fsdata.NodeFsDataProvider;
import fr.an.hadoop.fs.dirserver.fsdata.NodeFsDataProviderFactory;
import fr.an.hadoop.fs.dirserver.spi.storage.BlobStorage;
import fr.an.hadoop.fs.dirserver.util.LsUtils;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
public class ReactiveNodeTree {

	private static final String CONST_treeConfFile = "tree-conf.json";

	private final NodeTree nodeTree;

	private final BlobStorage blobStorage;

	private final AttrInfoRegistry attrInfoRegistry;
	private final AttrInfoIndexes attrInfoIndexes;
	
	private NodeFsDataProviderFactory nodeFsDataProviderFactory = NodeFsDataProviderFactory.defaultInstance;

	private final Object lock = new Object();
	
	// @GuardedBy("lock")
	private volatile ImmutableMap<String,MountedDir> mountedDirs = ImmutableMap.of();
	
	protected class MountedDir {

		public final String name;
		
		public final String baseMountUrl;

		public final NodeFsDataProvider fsDataProvider;
		
		public MountedDir(String name, String baseMountUrl, NodeFsDataProvider fsDataProvider) {
			this.name = name;
			this.baseMountUrl = baseMountUrl;
			this.fsDataProvider = fsDataProvider;
		}

		public MountedDirDTO toDTO() {
			return new MountedDirDTO(name, baseMountUrl);
		}
	}
	
	
	private final Random subscriptionIdGenerator = new Random();

	private final Map<String,Map<TreePathSubscriptionId,TreePathSubscription>> pathSubscriptions = new HashMap<>();
	
	private static class TreePathSubscription {

		private final TreePathSubscriptionId id;
		
		private final String path;

		// null for all attributes
		private ImmutableSet<String> attrNames;
		
		// -1 for fully recursive
		private int recurseLevel; 
		
		public TreePathSubscription(TreePathSubscriptionId id, String path, Set<String> attrNames, int recurseLevel) {
			this.id = id;
			this.path = path;
			this.attrNames = (attrNames != null)? ImmutableSet.copyOf(attrNames) : null;
			this.recurseLevel = recurseLevel;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	public ReactiveNodeTree(NodeTree nodeTree, BlobStorage blobStorage,
			AttrInfoRegistry attrInfoRegistry
			) {
		this.nodeTree = nodeTree;
		this.blobStorage = blobStorage;
		this.attrInfoRegistry = attrInfoRegistry;

		if (blobStorage.exists(CONST_treeConfFile)) {
			val conf = blobStorage.readFileJson(CONST_treeConfFile, NodeTreeConfDTO.class);

			// init mounts
			for(val mount: conf.mounts) {
				doAddMount(mount.mountName, mount.baseUrl);
			}
			
			// init attr indexes
			List<String> indexedAttrNames = conf.indexedAttrNames;
			List<AttrInfo<Object>> attrs = new ArrayList<>();
			val remainAttrs = new LinkedHashMap<String,AttrInfo<Object>>(attrInfoRegistry.getAttrs());
			for(val name: indexedAttrNames) {
				val attr = remainAttrs.remove(name);
				if (attr == null) {
					log.error("Attr not found '" + name + "' !");
					continue;
				}
				attrs.add(attr);
			}
			if (! remainAttrs.isEmpty()) {
				attrs.addAll(remainAttrs.values()); // unecessary?
			}
			attrInfoIndexes = new AttrInfoIndexes(attrs);
		} else {
			// init mounts
			log.info(CONST_treeConfFile + " file not found => empty mounts!");
			// init attr indexes
			List<AttrInfo<Object>> attrs = new ArrayList<>(attrInfoRegistry.getAttrs().values());
			attrInfoIndexes = new AttrInfoIndexes(attrs);
		}
	}

	// ------------------------------------------------------------------------

	public List<MountedDirDTO> getMountedDirs() {
		return LsUtils.map(mountedDirs.values(), x -> x.toDTO());
	}

	public MountedDirDTO addMountedDir(MountedDirDTO req) {
		synchronized (lock) {
			String name = req.name;
			MountedDir found = mountedDirs.get(name);
			if (found != null) {
				throw new IllegalArgumentException("mounted dir already found '" + name + "'");
			}
			MountedDir mountDir = doAddMount(name, req.baseMountUrl);
			// TODO notify?

			saveNodeTreeConf();
			
			return mountDir.toDTO();
		}
	}

	private MountedDir doAddMount(String name, String baseMountUrl) {
		NodeFsDataProvider fsDataProvider = nodeFsDataProviderFactory.create(baseMountUrl);
		MountedDir mountDir = new MountedDir(name, baseMountUrl, fsDataProvider);

		this.mountedDirs = ImmutableMap.<String,MountedDir>builder()
				.putAll(mountedDirs).put(name, mountDir)
				.build();
		return mountDir;
	}

	public MountedDirDTO removeMountedDir(String name) {
		synchronized (lock) {
			MountedDir found = mountedDirs.get(name);
			if (found == null) {
				throw new IllegalArgumentException("mounted dir not found '" + name + "'");
			}
			val tmp = new LinkedHashMap<>(mountedDirs);
			tmp.remove(name);
			// TODO cleanup? notify?
			this.mountedDirs = ImmutableMap.copyOf(tmp);
		
			saveNodeTreeConf();
			
			return found.toDTO();
		}
	}


	private void saveNodeTreeConf() {
		NodeTreeConfDTO conf;
		synchronized (lock) {
			val mounts = LsUtils.map(mountedDirs.values(), x -> new NodeTreeMountDirConfDTO(x.name, x.baseMountUrl));
			val attrNames = LsUtils.map(attrInfoIndexes.getIndex2Attr(), x -> x.name);
			conf = new NodeTreeConfDTO(mounts, attrNames);
		}
		
		blobStorage.writeFileJson(CONST_treeConfFile, conf);
	}

	
	
	@AllArgsConstructor
	protected static class MountedDirAndSubpath {
		public final MountedDir mountedDir;
		public final String[] subpath;
	}
	
	protected MountedDirAndSubpath resolveMountPath(String mountedPath) {
		String[] pathElts = mountedPath.split("/");
		// currently implemented: only 1 mount at root level
		val rootMountedDirs = this.mountedDirs;
		String mountName = pathElts[0];
		val mountDir = rootMountedDirs.get(mountName);
		if (null == mountDir) {
			throw new IllegalArgumentException("mount not found '" + mountName + "'");
		}
		val len = pathElts.length;
		String[] subpath = new String[len-1];
		System.arraycopy(pathElts, 1, subpath, 0, len-1);
		return new MountedDirAndSubpath(mountDir, subpath);
	}

	// ------------------------------------------------------------------------
	
	public void mergeUpdateNodeFsData(String path, NodeFsData nodeFsData) {
		String[] pathElts = path.split("/");
		val pathEltCount = pathElts.length;
		val childName = pathElts[pathEltCount-1];
		ResolvedNodePath resolvedNodePath = resolveMkDirsParent(pathElts);
		DirNode parentDirNode = resolvedNodePath.pathDirs[resolvedNodePath.pathDirs.length-1];
		if (nodeFsData == null) {
			// node to remove
			val parentDirPath = new DirNodePath(pathElts, resolvedNodePath.pathDirs.length-1, parentDirNode);
			doRemoveChild(parentDirPath, childName);
		} else {
			Node node = parentDirNode._friend_getOrResolveChildNode(pathElts, pathEltCount, cachedNodeDataLoader);
			if (nodeFsData instanceof DirNodeFsData) {
				val dirFsData = (DirNodeFsData) nodeFsData;
				if (node instanceof DirNode) {
					doMergeUpdateCompareDirFsData(pathElts, (DirNode) node, dirFsData);
				} else {
					// change file -> dir
					val parentDirPath = new DirNodePath(pathElts, resolvedNodePath.pathDirs.length-1, parentDirNode);
					DirNode newNode = doCreateDirNode(pathElts, dirFsData);
					doUpdateChild(parentDirPath, node, newNode);
				}
			} else if (nodeFsData instanceof FileNodeFsData) {
				val fileFsData = (FileNodeFsData) nodeFsData;
				if (node instanceof FileNode) {
					doMergeUpdateCompareFileFsData(pathElts, (FileNode) node, fileFsData);
				} else {
					// change dir -> file
					val parentDirPath = new DirNodePath(pathElts, resolvedNodePath.pathDirs.length-1, parentDirNode);
					FileNode newNode = doCreateFileNode(pathElts, fileFsData);
					doUpdateChild(parentDirNode, childName, node, newNode);
				}
			} // else should not occur
		}
	}
	
	public ResolvedNodePath resolve(String path) {
		String[] pathElts = path.split("/");
		int parentPathCount = pathElts.length - 1;
		val parentDirs = new DirNode[parentPathCount];
		DirNode currDir = rootNode;
		int pathIdx = 0;
		parentDirs[pathIdx] = currDir;
		for(; pathIdx < parentPathCount; pathIdx++) {
			val childDir = currDir._friend_getOrResolveChildNode(pathElts, pathIdx, cachedNodeDataLoader);
			if (childDir == null || !(childDir instanceof DirNode)) {
				throw new IllegalArgumentException();
			}
			currDir = (DirNode) childDir;
			parentDirs[pathIdx] = currDir;
		}
		val node = currDir._friend_getOrResolveChildNode(pathElts, pathIdx, cachedNodeDataLoader);
		if (node == null) {
			throw new IllegalArgumentException();
		}
		return new ResolvedNodePath(parentDirs, node);
	}
	
	// Memory eviction
	// ------------------------------------------------------------------------
	
	public void memCacheEvictSomeNodes(int minLevelEvictDir, int minLevelEvictFile, long untilFreedMemSize) {
		val visitor = new MemCacheEvictNodeVisitor(minLevelEvictDir, minLevelEvictFile, untilFreedMemSize);
		rootNode.accept(visitor);
	}


	
	// PRIVATE 
	// ------------------------------------------------------------------------

	private ResolvedNodePath resolveMkDirsParent(String[] pathElts) {
		int parentPathCount = pathElts.length - 1;
		val parentDirs = new DirNode[parentPathCount];
		DirNode currDir = rootNode;
		int pathIdx = 0;
		parentDirs[pathIdx] = currDir;
		for(; pathIdx < parentPathCount; pathIdx++) {
			currDir = getOrAddChildDir(currDir, pathElts, pathIdx);
			parentDirs[pathIdx] = currDir;
		}
		val node = currDir._friend_getOrResolveChildNode(pathElts, pathIdx, cachedNodeDataLoader);
		if (node == null) {
			throw new IllegalArgumentException();
		}
		return new ResolvedNodePath(parentDirs, node);
	}

	// deprecated?
	private DirNode getOrAddChildDir(DirNode parentDir, String[] pathElts, int childPathIdx) {
		Node childNode = parentDir._friend_getOrResolveChildNode(pathElts, childPathIdx, cachedNodeDataLoader);
		if (childNode instanceof DirNode) {
			return (DirNode) childNode;
		}
		val childName = pathElts[childPathIdx];
		DirNode res = new DirNode(childName);
		// long creationTime, long lastModifiedTime, NodeAttr[] sortedAttrArray, //
		// TreeMap<String,Node> sortedChildMap

		if (childNode != null) {
			// changed from file to dir.. removeChild()
			doUpdateChild(parentDir, childName, childNode, res);
		} else {
			doAddChild(parentDir, res);
		}
		return res;
	}

	// deprecated?
	private FileNode getOrAddChildFile(String[] pathElts, int childPathIdx, DirNode parentDir) {
		Node childNode = parentDir._friend_getOrResolveChildNode(pathElts, childPathIdx, cachedNodeDataLoader);
		if (childNode instanceof FileNode) {
			return (FileNode) childNode;
		}
		val childName = pathElts[childPathIdx];
		FileNode res = new FileNode(childName);
		// long creationTime, long lastModifiedTime, NodeAttr[] sortedAttrArray, //
		// TreeMap<String,Node> sortedChildMap
		
		val parentDirPath = new DirNodePath(pathElts, childPathIdx-1, parentDir);
		if (childNode != null) {
			// changed from dir to file .. removeChild()
			doUpdateChild(parentDirPath, childNode, res);
		} else {
			doAddChild(parentDirPath, res);
		}
		return res;
	}

	private void doUpdateChild(DirNodePath parentDir, Node prevChild, Node newChild) {
		parentDir.node.updateChild(newChild);
		
		// TODO
	}
	
	private void doRemoveChild(DirNodePath parentDir, String childName) {
		parentDir.node.removeChild(childName);
		
		// TODO
	}

	private void doAddChild(DirNodePath parentDir, Node child) {
		parentDir.node.addChild(child);
		
		// TODO 
	}

	private DirNode doCreateDirNode(String[] pathElts, DirNodeFsData childFsData) {
		val childName = pathElts[pathElts.length-1];
		DirNode res = new DirNode(childName);
		// TODO Auto-generated method stub
		
		return res;
	}

	private DirNode doCreateDirNode_AsyncRefresh(String[] pathElts) {
		val childName = pathElts[pathElts.length-1];
		DirNode res = new DirNode(childName);
		// TODO Auto-generated method stub
		
		return res;
	}


	private FileNode doCreateFileNode(String[] pathElts, FileNodeFsData childFsData) {
		val childName = pathElts[pathElts.length-1];
		FileNode res = new FileNode(childName);
		// TODO Auto-generated method stub
		
		return res;
	}


	private FileNode doCreateFileNode_AsyncRefresh(String[] pathElts) {
		val childName = pathElts[pathElts.length-1];
		FileNode res = new FileNode(childName);
		// TODO Auto-generated method stub
		
		return res;
	}

	private void doMergeUpdateCompareDirFsData(String[] pathElts, DirNode node, DirNodeFsData data) {
		val chg = node.updateDirEntries(data);
		if (null == chg) {
			return; // no change, do nothing
		} else {
			// removed Node if any will be GC.. nothing to do?
			for(val removed: chg.removed) {
				// TODO
				
			}
			val upsertEntries = new ArrayList<DirEntryNameAndType>();
			upsertEntries.addAll(chg.updated);
			upsertEntries.addAll(chg.added);
			
			for(val upsertEntry: upsertEntries) {
				// TODO need to async reeval attr on nodes
				
			}

			// fire event
			// TODO 
			
			// propagate to attr change
			// TODO
			
			//			if (!toRemoveChildNames.isEmpty()) {
//				for(val child: toRemoveChildNames) {
//					doRemoveChild(node, child);
//				}
//			}
//			if (!toAddChildEntries.isEmpty()) {
//				for(val childEntry: toAddChildEntries) {
//					String[] childPathElts = PathUtils.toChildPath(pathElts, childEntry.name);
//					switch(childEntry.type) {
//					case DIR:
//						DirNode childDirNode = doCreateDirNode_AsyncRefresh(childPathElts);
//						doAddChild(node, childDirNode);
//						break;
//					case FILE:
//						FileNode childFileNode = doCreateFileNode_AsyncRefresh(childPathElts);
//						doAddChild(node, childFileNode);
//						break;
//					}
//				}
//			}
			
			// TODO
		}
	}

	private void doMergeUpdateCompareFileFsData(String[] pathElts, FileNode node, FileNodeFsData data) {
		if (node.getLastModifiedTime() == data.lastModifiedTime &&
				node.getLastModifiedTime() == data.getLastModifiedTime() &&
				node.getFileLength() == data.fileLength) {
			return; // no change, do nothing
		} else {
			node.updateData(data);
			// TODO fireChange
		}
	}


}
