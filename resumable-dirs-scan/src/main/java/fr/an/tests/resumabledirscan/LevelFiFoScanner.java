package fr.an.tests.resumabledirscan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.an.tests.resumabledirscan.TreeScanNode.RootTreeScanNode;

public class LevelFiFoScanner {

	protected static int MAX_LEVEL = 50;

	protected final LevelEntry[] levels;
	
	protected int maxLevel = -1;
	
	protected static class LevelEntry {
		public final int level;
		protected final ArrayDeque<TreeScanNode> nodeQueue = new ArrayDeque<>();
		
		public LevelEntry(int level) {
			this.level = level;
		}

		public void add(TreeScanNode node) {
			nodeQueue.add(node);
		}

		public void addAll(List<TreeScanNode> childDirs) {
			nodeQueue.addAll(childDirs);
		}
		
	}

	// ------------------------------------------------------------------------
	
	public LevelFiFoScanner() {
		levels = new LevelEntry[MAX_LEVEL];
		for(int i = 0; i < MAX_LEVEL; i++) {
			levels[i] = new LevelEntry(i);
		}
	}
	
	public void startScanRoot(String rootDir) {
		Path rootPath = Paths.get(rootDir);
		RootTreeScanNode rootNode = new RootTreeScanNode(rootPath);
		levels[0].add(rootNode);
		this.maxLevel = 0;
		
		// new Thread(() -> doRunScans()).start();
		doRunScans();
	}
	
	// ------------------------------------------------------------------------

	
	protected void doRunScans() {
		for(;;) {
			int currLevel = maxLevel;
			LevelEntry level = levels[currLevel];
			TreeScanNode node;
			try {
				node = level.nodeQueue.remove();
			} catch(NoSuchElementException ex) {
				// should not occur?
				break;
			}
			
			Path dirPath = node.getFile();
			List<Path> childLs;
			try {
				Stream<Path> child = Files.list(dirPath);
				childLs = child.collect(Collectors.toList());
			} catch (IOException e) {
				// TODO..
				childLs = new ArrayList<Path>();
			}


			List<TreeScanNode> childDirs = new ArrayList<>(childLs.size());
			for(Path childPath : childLs) {
				try {
					boolean isChildDir = Files.isDirectory(childPath);
					if (isChildDir) {
						String childName = childPath.getName(childPath.getNameCount()-1).toString();
						TreeScanNode childNode = new TreeScanNode(node, childName, childPath);
						childDirs.add(childNode);
						long fileLastModif = Files.getLastModifiedTime(childPath).toMillis();
						node.addDirStat(fileLastModif);
					} else {
						long fileLastModif = Files.getLastModifiedTime(childPath).toMillis();
						long fileLen = Files.size(childPath);
						node.addFileStat(fileLastModif, fileLen);
					}
				} catch(IOException ex) {
					// ignore, no rethrow?
				}
			}
			if (! childDirs.isEmpty()) {
				node.putChildDirs(childDirs);
				int childLevelIdx = currLevel+1;
				LevelEntry childLevelQueue = levels[childLevelIdx];
				childLevelQueue.addAll(childDirs);
				this.maxLevel = childLevelIdx;
			} else {
				// check remain
				if (level.nodeQueue.isEmpty()) {
					this.maxLevel = currLevel-1;
					if (this.maxLevel == 0) {
						break;
					}
				}
				if (node.getParent() != null) {
					onScannedChildTree(node.getParent(), node);
				}
			}
		}
	}

	private void onScannedChildTree(TreeScanNode parent, TreeScanNode childNode) {
		FileStats fs = childNode.getFileStats();
		parent.addSynthetizedChildFileStats(fs);
		
		if (parent.childScanedCount == parent.getChild().size()) {
			// finished scanning direct child
			// TODO

			TreeScanNode grandParent = parent.getParent();
			if (grandParent != null) {
				onScannedChildTree(grandParent, parent);
			}
		} else {
			
		}
		
	}
}
