package fr.an.fssync.fs.io;

import java.io.File;

import org.apache.hadoop.hdfs.inotify.Event.CreateEvent.INodeType;

import fr.an.fssync.model.FsEntry;
import fr.an.fssync.model.FsEntryInfo;
import fr.an.fssync.model.FsPath;
import fr.an.fssync.model.visitor.FsEntryVisitor;
import fr.an.fssync.sortedimgfrag.ImageFragmentsWriterUtils;
import lombok.val;

/**
 * (Slow) Dir scanner using java.io then
 * file.isFile(),file.lastModified(),file.size()
 *
 */
public class JavaIoDirScannerImageBuilder {

    private FsEntryVisitor fileHandler;

    public JavaIoDirScannerImageBuilder(FsEntryVisitor fileHandler) {
	this.fileHandler = fileHandler;
    };

    public void recurseScan(File dir, FsPath dirPath) {
	File[] files = dir.listFiles();
	if (files != null && files.length != 0) {
	    for (File f : files) {
		FsPath childPath = dirPath.child(f.getName());
		val info = FsEntryInfo.builder();
		if (f.isFile()) {
		    info.iNodeType(INodeType.FILE);
		    info.fileSize(f.length());

		} else if (f.isDirectory()) {
		    info.iNodeType(INodeType.DIRECTORY);

		} else {
		    continue; // unrecognized?!
		}
		info.mtime(f.lastModified());
		info.ctime(info.mtime); // ? no creation time on java.io.File

		FsEntry e = new FsEntry(childPath, info.build());

		fileHandler.visit(e);

		if (f.isDirectory()) {
		    recurseScan(f, childPath);
		}
	    }
	}
    }

    public static void scanDirWriteImage(File dir, File toBaseDir, 
	    String destImageName, int hashSize,
	    int maxFragmentSize, File debugTextFile) {
	FsEntryVisitor entryHandler = ImageFragmentsWriterUtils.createSplitThenSortBufferedWriter(toBaseDir,
		destImageName, hashSize, maxFragmentSize, debugTextFile, null);

	JavaIoDirScannerImageBuilder scanner = new JavaIoDirScannerImageBuilder(entryHandler);

	entryHandler.begin();

	// *** The Biggy ***
	scanner.recurseScan(dir, FsPath.ROOT);

	entryHandler.end();
    }

}
