package fr.an.hadoop.fs.dirserver.attrtree.fsdata.nio;

import java.nio.file.Paths;

import org.junit.Test;

import fr.an.hadoop.fs.dirserver.fsdata.nio.CountFsNodeDataEntryCallback;
import fr.an.hadoop.fs.dirserver.fsdata.nio.JavaNIOFileToNodeFsDataScanner;
import lombok.val;

public class JavaNIOFileToNodeFsDataScannerTest {

	@Test
	public void testScan_data_rootDir1() {
		val path = Paths.get("./src/test/data/rootDir1");
		val callback = new CountFsNodeDataEntryCallback();
		JavaNIOFileToNodeFsDataScanner.scan(path, callback);
		System.out.println("done scan => " + callback.getCountFile() + " files, " + callback.getCountDir() + " dirs");
	}
}
