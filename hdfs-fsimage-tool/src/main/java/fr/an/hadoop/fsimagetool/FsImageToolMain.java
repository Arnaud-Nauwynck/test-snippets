package fr.an.hadoop.fsimagetool;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hdfs.server.namenode.FSImageFormatPBINode;
import org.apache.hadoop.hdfs.tools.offlineImageViewer.FsImageDumpTool;
import org.apache.hadoop.hdfs.tools.offlineImageViewer.FsImageDumpTool.EntryCallback;

import fr.an.hadoop.fsimagetool.fsimg.FsImageEntryCallback;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FsImageToolMain {

	public static void main(String[] args) {
		// System.setProperty("hadoop.home.dir", new File(".").getAbsolutePath()); // dummy..
		File dataDir = new File("src/test/data");

//		File editFile = new File(dataDir, "edits_0000000000000000001-0000000000000000090");
//		if (! editFile.exists()) {
//			log.error("file not found");
//			return;
//		}
//		FsEditDumpTool.dumpEditFile(editFile);

//		new FsEditDumpTool().scanDumpEditFiles(nnDir, 1, -1);
	
		File fsImage = new File(dataDir, "fs2/fsimage_0000000000000017663");
//		File fsImage = new File(dataDir, "fs2/fsimage_0000000000000010786");
//		File fsImage = new File(dataDir, "fs1/fsimage_small");
		EntryCallback callback = new FsImageEntryCallback();
		
		try {
			FsImageDumpTool.processFsImage(fsImage , callback);
		} catch (IOException ex) {
			System.err.println("Failed");
			ex.printStackTrace(System.err);
		}
	}
	
}
