package fr.an.fssync.fs.nio;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributeView;

import org.apache.hadoop.hdfs.inotify.Event.CreateEvent.INodeType;

import fr.an.fssync.model.FsEntry;
import fr.an.fssync.model.FsEntryInfo;
import fr.an.fssync.model.FsPath;
import fr.an.fssync.model.visitor.FsEntryVisitor;
import lombok.val;

public class JavaNioDirScannerImageBuilder {

    private FsEntryVisitor fsEntryVisitor;

    public JavaNioDirScannerImageBuilder(FsEntryVisitor fsEntryVisitor) {
	this.fsEntryVisitor = fsEntryVisitor;
    };

    public void scan(Path rootScanDir) {
	try {
	    Files.walkFileTree(rootScanDir, new SimpleFileVisitor<Path>() {
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		    val info = FsEntryInfo.builder();
		    if (attrs.isRegularFile()) {
			info.iNodeType(INodeType.FILE);
			info.fileSize(attrs.size());

		    } else if (attrs.isDirectory()) {
			info.iNodeType(INodeType.DIRECTORY);

		    } else if (attrs.isSymbolicLink()) {
			info.iNodeType(INodeType.SYMLINK);
			// TODO ..
		    } else {
			// unrecognized?!
			return FileVisitResult.CONTINUE;
		    }
		    info.mtime(attrs.lastModifiedTime().toMillis());
		    info.ctime(attrs.creationTime().toMillis());

		    if (attrs instanceof PosixFileAttributeView) {
//        		    UserPrincipal owner();
//        		    GroupPrincipal group();
//        		    Set<PosixFilePermission> permissions();
		    }

		    FsPath relPath = FsPath.of(file.relativize(rootScanDir).toString().replace('\\', '/'));
		    fsEntryVisitor.visit(new FsEntry(relPath, info.build()));

		    return FileVisitResult.CONTINUE;
		}
	    });
	} catch (IOException e) {
	    throw new RuntimeException("Failed", e);
	}
    }

}
