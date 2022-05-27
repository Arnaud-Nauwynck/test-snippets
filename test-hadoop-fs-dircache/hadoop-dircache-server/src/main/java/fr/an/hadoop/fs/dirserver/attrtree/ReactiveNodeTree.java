package fr.an.hadoop.fs.dirserver.attrtree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ImmutableMap;

import fr.an.hadoop.fs.dirserver.attrtree.attrinfo.AttrInfo;
import fr.an.hadoop.fs.dirserver.attrtree.attrinfo.AttrInfoIndexes;
import fr.an.hadoop.fs.dirserver.attrtree.attrinfo.AttrInfoRegistry;
import fr.an.hadoop.fs.dirserver.attrtree.cache.CachedNodeDataLoader;
import fr.an.hadoop.fs.dirserver.attrtree.impl.MemCacheEvictNodeVisitor;
import fr.an.hadoop.fs.dirserver.attrtree.impl.ResolvedNodePath;
import fr.an.hadoop.fs.dirserver.dto.MountedDirDTO;
import fr.an.hadoop.fs.dirserver.dto.NodeTreeConfDTO;
import fr.an.hadoop.fs.dirserver.dto.NodeTreeConfDTO.NodeTreeMountDirConfDTO;
import fr.an.hadoop.fs.dirserver.dto.TreePathSubscriptionId;
import fr.an.hadoop.fs.dirserver.fsdata.NodeFsData;
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

	private final DirNode rootNode;

	private final BlobStorage blobStorage;

	private final AttrInfoRegistry attrInfoRegistry;
	private final AttrInfoIndexes attrInfoIndexes;
	
	private final CachedNodeDataLoader cachedNodeDataLoader;

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
		
		public TreePathSubscription(TreePathSubscriptionId id, String path) {
			this.id = id;
			this.path = path;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	public ReactiveNodeTree(DirNode rootNode, BlobStorage blobStorage,
			AttrInfoRegistry attrInfoRegistry
			) {
		this.rootNode = rootNode;
		this.blobStorage = blobStorage;
		this.attrInfoRegistry = attrInfoRegistry;
		
		this.cachedNodeDataLoader = null; // TODO

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

	public void mergeUpdateNodeFsData(String path, NodeFsData nodeFsData) {
		
	}
	
	// Memory eviction
	// ------------------------------------------------------------------------
	
	public void memCacheEvictSomeNodes(int minLevelEvictDir, int minLevelEvictFile, long untilFreedMemSize) {
		val visitor = new MemCacheEvictNodeVisitor(minLevelEvictDir, minLevelEvictFile, untilFreedMemSize);
		rootNode.accept(visitor);
	}

}
