package fr.an.hadoop.fs.dirserver.attrtree;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;

import com.google.common.collect.ImmutableMap;

import fr.an.hadoop.fs.dirserver.attrtree.cache.CachedNodeDataLoader;
import fr.an.hadoop.fs.dirserver.dto.MountedDirDTO;
import fr.an.hadoop.fs.dirserver.fsdata.NodeFsDataProvider;
import fr.an.hadoop.fs.dirserver.fsdata.NodeFsDataProviderFactory;
import fr.an.hadoop.fs.dirserver.util.LsUtils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * 
 */
public class ReactiveNodeTree {

	private final DirNode rootNode;

	private final CachedNodeDataLoader cachedNodeDataLoader;

	private NodeFsDataProviderFactory nodeFsDataProviderFactory = NodeFsDataProviderFactory.defaultInstance;

	private final Object lock = new Object();
	
	private final Configuration hadoopConf;
	
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
		
		public TreePathSubscription(TreePathSubscriptionId id, String path) {
			this.id = id;
			this.path = path;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	public ReactiveNodeTree(DirNode rootNode, Configuration hadoopConf) {
		this.rootNode = rootNode;
		this.hadoopConf = hadoopConf;
		
		this.cachedNodeDataLoader = null; // TODO
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
			String baseMountUrl = req.baseMountUrl;
			NodeFsDataProvider fsDataProvider = nodeFsDataProviderFactory.create(baseMountUrl);
			MountedDir mountDir = new MountedDir(name, baseMountUrl, fsDataProvider);

			this.mountedDirs = ImmutableMap.<String,MountedDir>builder()
					.putAll(mountedDirs).put(name, mountDir)
					.build();
			// TODO notify?
			
			return mountDir.toDTO();
		}
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
		
			return found.toDTO();
		}
	}
	
	@AllArgsConstructor
	protected static class ResolvedMountedPath {
		public final MountedDir mountedDir;
		public final String[] subpath;
	}
	
	protected ResolvedMountedPath resolveMountPath(String mountedPath) {
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
		return new ResolvedMountedPath(mountDir, subpath);
	}

	// ------------------------------------------------------------------------

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
	
	public ResolvedNodePath resolveMkDirs(String path) {
		String[] pathElts = path.split("/");
		int parentPathCount = pathElts.length - 1;
		val parentDirs = new DirNode[parentPathCount];
		DirNode currDir = rootNode;
		int pathIdx = 0;
		parentDirs[pathIdx] = currDir;
		for(; pathIdx < parentPathCount; pathIdx++) {
			val childName = pathElts[pathIdx];
			Node childDir = currDir._friend_getOrResolveChildNode(pathElts, pathIdx, cachedNodeDataLoader);
			if (childDir != null || !(childDir instanceof DirNode)) {
				// changed from file to dir.. removeChild()
				doRemoveChild(currDir, childName);
				childDir = null;
			}
			if (childDir == null) {
				DirNode childDirNode = new DirNode(childName);
				// long creationTime, long lastModifiedTime, NodeAttr[] sortedAttrArray, //
				// TreeMap<String,Node> sortedChildMap
				doAddChild(currDir, childDirNode);
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
	
	private void doRemoveChild(DirNode parentDir, String childName) {
		// TODO Auto-generated method stub
		
	}

	private void doAddChild(DirNode parentDir, Node child) {
		// TODO Auto-generated method stub
		
	}

	public void mergeUpdates(String path, Node node) {
		
	}
	
	// Memory eviction
	// ------------------------------------------------------------------------
	
	public void memCacheEvictSomeNodes(int minLevelEvictDir, int minLevelEvictFile, long untilFreedMemSize) {
		val visitor = new MemCacheEvictNodeVisitor(minLevelEvictDir, minLevelEvictFile, untilFreedMemSize);
		rootNode.accept(visitor);
	}
	
	@RequiredArgsConstructor
	private static class MemCacheEvictNodeVisitor extends NodeVisitor {

		private final int minLevelEvictDir;
		private final int minLevelEvictFile;
		private final long untilFreedMemSize;
		
		private int currLevel;
		private long currFreedMemSize;
		
		@Override
		public void caseFile(FileNode node) {
			// do nothing!	
		}
		@Override
		public void caseDir(DirNode node) {
			int childCount = node.getChildCount();
			if (childCount == 0) {
				return;
			}
			currLevel++;
			Object[] childNameOrNodeArray = node._friend_getSortedChildNameOrNodeArray();
			for(int i = 0; i < childCount; i++) {
				val childNameOrNode = childNameOrNodeArray[i];
				if (childNameOrNode instanceof Node) {
					Node childNode = (Node) childNameOrNode;
					if (childNode instanceof DirNode) {
						DirNode childDir = (DirNode) childNode;
						
						// *** recurse ***
						childDir.accept(this);
						
						if (currLevel > minLevelEvictDir && currFreedMemSize < untilFreedMemSize) {
							// cache evict child dir
							childNameOrNodeArray[i] = null;
							int estimateNodeMem = 120 + 50 * childNode._friend_getAttrs().length + 16 * childDir.getChildCount();
							currFreedMemSize += estimateNodeMem;
							if (currFreedMemSize > untilFreedMemSize) {
								currLevel--;
								return;
							}
						}
					} else { // if (childNode instanceof FileNode) {
						if (currLevel > minLevelEvictFile && currFreedMemSize < untilFreedMemSize) {
							// cache evict child file
							childNameOrNodeArray[i] = null;
							int estimateNodeMem = 120 + 50 * childNode._friend_getAttrs().length;
							currFreedMemSize += estimateNodeMem;
							if (currFreedMemSize > untilFreedMemSize) {
								currLevel--;
								return;
							}
						}
					}
				} // else instanceof String.. nothing to do
			}
			currLevel--;

		}
		
	}
}
