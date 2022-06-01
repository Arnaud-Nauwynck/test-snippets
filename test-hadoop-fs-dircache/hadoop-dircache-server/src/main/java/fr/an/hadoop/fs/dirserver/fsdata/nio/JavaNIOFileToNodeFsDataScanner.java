package fr.an.hadoop.fs.dirserver.fsdata.nio;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributeView;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.google.common.collect.ImmutableMap;

import fr.an.hadoop.fs.dirserver.fsdata.DirEntryNameAndType;
import fr.an.hadoop.fs.dirserver.fsdata.FsNodeDataEntryCallback;
import fr.an.hadoop.fs.dirserver.fsdata.FsNodeType;
import fr.an.hadoop.fs.dirserver.fsdata.NodeFsData;
import fr.an.hadoop.fs.dirserver.fsdata.NodeFsData.DirNodeFsData;
import fr.an.hadoop.fs.dirserver.fsdata.NodeFsData.FileNodeFsData;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavaNIOFileToNodeFsDataScanner {

	public static void scan(Path rootScanDir, FsNodeDataEntryCallback callback) {
		val visitor = new JavaNIOToNodeFsDataFileVisitor(callback);
		try {
			Files.walkFileTree(rootScanDir, visitor);
		} catch (IOException e) {
			throw new RuntimeException("Failed", e);
		}
	}
	
	@RequiredArgsConstructor
	private static class DirNodeFsDataBuilder {
		private final String path;
		private final String name;
		private final long creationTime;
		private final long lastModifiedTime;
		private final ImmutableMap<String,Object> extraFsAttrs;
		private final TreeMap<String,DirEntryNameAndType> childEntries = new TreeMap<String,DirEntryNameAndType>();
	}
	
	protected static class JavaNIOToNodeFsDataFileVisitor extends SimpleFileVisitor<Path> {
		private final FsNodeDataEntryCallback callback;
		private final List<DirNodeFsDataBuilder> currDirBuilderStack = new ArrayList<>();
		private DirNodeFsDataBuilder currDirBuilder;
		
		protected JavaNIOToNodeFsDataFileVisitor(FsNodeDataEntryCallback callback) {
			this.callback = callback;
			currDirBuilder = new DirNodeFsDataBuilder("", "", 0, 0, null);
			currDirBuilderStack.add(currDirBuilder);
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
			long creationTime = attrs.creationTime().toMillis();
			long lastModifiedTime = attrs.lastModifiedTime().toMillis();
			val extraFsAttrs = ImmutableMap.<String,Object> of(
					// TOADD
					);
			
			val path = currDirBuilder.path + "/" + name;
			DirNodeFsDataBuilder dirBuilder = new DirNodeFsDataBuilder(path, name, creationTime, lastModifiedTime, extraFsAttrs);

			currDirBuilderStack.add(dirBuilder);
			this.currDirBuilder = dirBuilder;
			
			return super.preVisitDirectory(dir, attrs);
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			DirNodeFsData dirNode = new DirNodeFsData(currDirBuilder.name, 
					currDirBuilder.creationTime, currDirBuilder.lastModifiedTime, currDirBuilder.extraFsAttrs,
					currDirBuilder.childEntries);
			callback.handle(currDirBuilder.path, dirNode);
			
			// pop and add dir to parent
			int level = currDirBuilderStack.size() - 1;
			currDirBuilderStack.remove(level);
			
			val parentNode = currDirBuilderStack.get(level-1);
			this.currDirBuilder = parentNode;
			parentNode.childEntries.put(dirNode.name, new DirEntryNameAndType(dirNode.name, FsNodeType.DIR));
			
			return super.postVisitDirectory(dir, exc);
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			String name = file.getFileName().toString();
			NodeFsData fileNode = null;

			long creationTime = attrs.creationTime().toMillis();
			long lastModifiedTime = attrs.lastModifiedTime().toMillis();
			
			if (attrs.isRegularFile()) {
				long fileLength = attrs.size();
				val extraFsAttrs = ImmutableMap.<String,Object>of(
						// TOADD
						);
				
				fileNode = new FileNodeFsData(name, creationTime, lastModifiedTime, extraFsAttrs, fileLength);

				val path = currDirBuilder.path + "/" + name;
				callback.handle(path, fileNode);

			} else if (attrs.isDirectory()) {
				// should not occur?
				
			} else if (attrs.isSymbolicLink()) {
				// TODO ..
			} else {
				// unrecognized?!
			}

			if (attrs instanceof PosixFileAttributeView) {
//		    UserPrincipal owner();
//		    GroupPrincipal group();
//		    Set<PosixFilePermission> permissions();
			}
			
			this.currDirBuilder.childEntries.put(name, new DirEntryNameAndType(name, FsNodeType.FILE));

			return FileVisitResult.CONTINUE;
		}
		
		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			log.error("Failed " + file, exc);
			return FileVisitResult.CONTINUE;
		}

	}
}
