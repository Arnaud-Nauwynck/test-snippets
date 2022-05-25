package fr.an.hadoop.fs.dirserver.attrtree.encoder;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.management.ManagementFactory;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import fr.an.hadoop.fs.dirserver.attrtree.AttrInfoRegistry;
import fr.an.hadoop.fs.dirserver.attrtree.scan.CountNodeVisitor;
import fr.an.hadoop.fs.dirserver.attrtree.scan.JavaNIOFileToNodeScanner;
import lombok.val;

public class NodeTreeFileWriterTest {

	static {
		JavaNIOFileToNodeScanner.scan(Paths.get("./src/test/data/rootDir1"));
	}

	@Test
	public void testWriteRecursiveNode_rootDir1() throws Exception {
		val path = Paths.get("./src/test/data/rootDir1");
		val rootNode = JavaNIOFileToNodeScanner.scan(path);
		
		val file = new File("target/testTree1.dat");
		try (val out = new FileOutputStream(file)) {
			val attrRegistry = new AttrInfoRegistry();
			val writer = new NodeTreeFileWriter(attrRegistry, out, 0);

			writer.writeRecursiveNode("", rootNode);
			writer.flush();
		}
	}

	@Test
	public void testWriteRecursiveNode_m2Repo() throws Exception {
		val userHome = System.getProperty("user.home");
		val path = Paths.get(userHome + "/.m2");
		System.out.println("scan " + path + " ...");
		
		val outputFile = new File("target/testTree_m2.dat");
		doScanWriteRecursiveNode(outputFile, path);
	}

	@Test
	public void testWriteRecursiveNode_c() throws Exception {
		val path = Paths.get("c:\\"); // => AccesDeniedException "c:\\$Recycle.Bin" ?!
		val childList = Files.list(path).collect(Collectors.toList());
		for(val child : childList) {
			val name = child.getFileName().toString();
			if (name.endsWith("$Recycle.Bin")) {
				continue;
			}
			System.out.println("scan " + child + " ...");
			val outputFile = new File("target/testTree_c_" + name + ".dat");
			try {
				doScanWriteRecursiveNode(outputFile, child);
			} catch(Exception ex) {
				System.out.println("Failed scan " + child + ": " + ex.getMessage());
			}
		}
	}

	private void doScanWriteRecursiveNode(File ouputFile, Path scanRootPath) throws Exception {
		System.gc();
		val memBean = ManagementFactory.getMemoryMXBean();
		val heapBefore = memBean.getHeapMemoryUsage().getUsed();
		
		long startTime = System.currentTimeMillis();
		
		val rootNode = JavaNIOFileToNodeScanner.scan(scanRootPath);
		
		long millis = System.currentTimeMillis() - startTime;
		System.out.println("done scanned " + scanRootPath + ", took " + millis + "ms");

		System.gc();
		val heapAfter = memBean.getHeapMemoryUsage().getUsed();
		val usedMem = heapAfter - heapBefore;

		val counter = new CountNodeVisitor();
		rootNode.accept(counter);
		val countFile = counter.getCountFile();
		val countDir = counter.getCountDir();
		val countTotal = countFile + countDir;
		
		System.out.println("count: " + countTotal + "(files:" + countFile + ", dirs:" + countDir + ")"
				+ " used memory: " + usedMem
				+ " (=" + (usedMem/countTotal) + " b/u)");

		try (val out = new FileOutputStream(ouputFile)) {
			val attrRegistry = new AttrInfoRegistry();
			val writer = new NodeTreeFileWriter(attrRegistry, out, 0);

			writer.writeRecursiveNode("", rootNode);
			writer.flush();
		}
	}
}
