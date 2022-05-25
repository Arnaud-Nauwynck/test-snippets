package fr.an.hadoop.fs.dirserver.attrtree.scan;

import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import lombok.val;

public class JavaNIOFileToNodeScannerTest {

	@Test
	public void testScan_data_rootDir1() {
		val path = Paths.get("./src/test/data/rootDir1");
		val rootNode = JavaNIOFileToNodeScanner.scan(path);
		Assert.assertNotNull(rootNode);
	}
}
