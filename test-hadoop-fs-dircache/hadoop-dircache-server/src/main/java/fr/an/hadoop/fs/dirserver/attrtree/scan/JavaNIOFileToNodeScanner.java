package fr.an.hadoop.fs.dirserver.attrtree.scan;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributeView;
import java.util.ArrayList;
import java.util.List;

import fr.an.hadoop.fs.dirserver.attrtree.DirNode;
import fr.an.hadoop.fs.dirserver.attrtree.DirNode.DirNodeBuilder;
import fr.an.hadoop.fs.dirserver.attrtree.FileNode;
import fr.an.hadoop.fs.dirserver.attrtree.Node;
import fr.an.hadoop.fs.dirserver.attrtree.NodeAttr;
import lombok.val;

public class JavaNIOFileToNodeScanner {

	public static Node scan(Path rootScanDir) {
		DirNodeBuilder parentNodeBuilder = new DirNodeBuilder("<parent>");
		try {
			Files.walkFileTree(rootScanDir, new JavaNIOToNodeFileVisitor(parentNodeBuilder));
		} catch (IOException e) {
			throw new RuntimeException("Failed", e);
		}
		// tocheck root != parent
		DirNode parentNode = parentNodeBuilder.build();
		return parentNode.getSortedChildNodes()[0];
	}
	
	
	protected static class JavaNIOToNodeFileVisitor extends SimpleFileVisitor<Path> {
		List<DirNodeBuilder> currNodeDirNodeBuilders = new ArrayList<>();
		DirNodeBuilder currNodeDirNodeBuilder;
		
		protected JavaNIOToNodeFileVisitor(DirNodeBuilder rootNodeDirNodeBuilder) {
			this.currNodeDirNodeBuilder = rootNodeDirNodeBuilder;
			currNodeDirNodeBuilders.add(rootNodeDirNodeBuilder);
		}
		
		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			// attrs.lastModif ..
			String name;
			try {
				name = dir.getFileName().toString();
			} catch(NullPointerException ex) {
				name = dir.toString(); // for root "c:" ?!
			}
			DirNodeBuilder nodeDirNodeBuilder = new DirNodeBuilder(name);
			currNodeDirNodeBuilders.add(nodeDirNodeBuilder);
			this.currNodeDirNodeBuilder = nodeDirNodeBuilder;
			
			return super.preVisitDirectory(dir, attrs);
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			Node dirNode = this.currNodeDirNodeBuilder.build();
			int level = currNodeDirNodeBuilders.size() - 1;
			currNodeDirNodeBuilders.remove(level);
			val parentNode = currNodeDirNodeBuilders.get(level-1);
			this.currNodeDirNodeBuilder = parentNode;
			
			parentNode.withChild(dirNode);
			
			return super.postVisitDirectory(dir, exc);
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			String name = file.getFileName().toString();
			Node fileNode = null;

			long creationTime = attrs.creationTime().toMillis();
			long lastModifiedTime = attrs.lastModifiedTime().toMillis();
			
			NodeAttr[] sortedAttrArray = Node.EMPTY_ATTRS;
			if (attrs.isRegularFile()) {
				long fileLength = attrs.size();
				
				fileNode = new FileNode(name, creationTime, lastModifiedTime, sortedAttrArray, fileLength);
				
			} else if (attrs.isDirectory()) {
				// should not occur?
				
			} else if (attrs.isSymbolicLink()) {
				// TODO ..
			} else {
				// unrecognized?!
			}

			if (fileNode != null) {
				if (attrs instanceof PosixFileAttributeView) {
	//		    UserPrincipal owner();
	//		    GroupPrincipal group();
	//		    Set<PosixFilePermission> permissions();
				}
				
				this.currNodeDirNodeBuilder.withChild(fileNode);
			}

			return FileVisitResult.CONTINUE;
		}
		
		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			// TODO Auto-generated method stub
			return super.visitFileFailed(file, exc);
		}

	}
}
