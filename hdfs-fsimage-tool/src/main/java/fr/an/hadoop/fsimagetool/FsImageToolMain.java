package fr.an.hadoop.fsimagetool;

import java.io.File;

import org.apache.hadoop.hdfs.server.namenode.FsEditDumpTool;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FsImageToolMain {

	public static void main(String[] args) {
		// System.setProperty("hadoop.home.dir", new File(".").getAbsolutePath()); // dummy..
		File nnDir = new File("src/test/data");

//		File editFile = new File(nnDir, "edits_0000000000000000001-0000000000000000090");
//		if (! editFile.exists()) {
//			log.error("file not found");
//			return;
//		}
//		FsEditDumpTool.dumpEditFile(editFile);

		FsEditDumpTool.scanDumpEditFiles(nnDir, 1, -1);
	
	}
	
}
