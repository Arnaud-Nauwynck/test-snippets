package fr.an.hadoop.fs.dirserver.attrtree.encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.junit.Test;

import fr.an.hadoop.fs.dirserver.attrtree.Node;
import fr.an.hadoop.fs.dirserver.attrtree.attrinfo.AttrInfoIndexes;
import fr.an.hadoop.fs.dirserver.attrtree.attrinfo.AttrInfoRegistry;
import fr.an.hadoop.fs.dirserver.attrtree.encoder.NodeTreeFileReader.ReadNodeEntry;
import fr.an.hadoop.fs.dirserver.attrtree.scan.CountNodeVisitor;
import fr.an.hadoop.fs.dirserver.attrtree.scan.JavaNIOFileToNodeScanner;
import lombok.Getter;
import lombok.val;

public class NodeTreeFileWriterTest {

	static {
		JavaNIOFileToNodeScanner.scan(Paths.get("./src/test/data/rootDir1"));
	}

	private static final AttrInfoRegistry attrRegistry = new AttrInfoRegistry();
	private static final AttrInfoIndexes attrIndexes = new AttrInfoIndexes(new ArrayList<>());

	@Test
	public void testWriteRecursiveNode_rootDir1() throws Exception {
		val path = Paths.get("./src/test/data/rootDir1");
		val rootNode = JavaNIOFileToNodeScanner.scan(path);
		countNodes(rootNode);
		
		val file = new File("target/testTree1.dat");
		doWriteFile(file, rootNode);
		doReadFile(attrRegistry, file);
	}

	private void countNodes(Node rootNode) {
		val counter = new CountNodeVisitor();
		rootNode.accept(counter);
		val countFile = counter.getCountFile();
		val countDir = counter.getCountDir();
		val countTotal = countFile + countDir;
		
		System.out.println("count: " + countTotal + "(files:" + countFile + ", dirs:" + countDir + ")");
	}
	
	private void doWriteFile(File outputFile, Node rootNode) throws IOException, FileNotFoundException {
		System.out.println("write to file: " + outputFile);
		long filePos;
		val startMillis = System.currentTimeMillis();
		try (val out = new FileOutputStream(outputFile)) {
			val writer = new NodeTreeFileWriter(attrIndexes, out, 0);

			writer.writeRecursiveNode("", rootNode);
			writer.flush();
			filePos = writer.getFilePos();
		}
		val millis = System.currentTimeMillis() - startMillis;
		System.out.println(".. done write => " + (filePos/1024) + " kb, took " + millis + " ms");
	}

	private void doReadFile(AttrInfoRegistry attrRegistry, File file) throws IOException, FileNotFoundException {
		System.out.println("read from file: " + file);
		val startMillis = System.currentTimeMillis();
		int count;
		try (val in = new FileInputStream(file)) {
			val reader = new NodeTreeFileReader(attrIndexes, in);
			val callback = new CountingReadNodeEntry();
			
			reader.readRecursiveNode(callback);
		
			count = callback.getCount();
		}
		val millis = System.currentTimeMillis() - startMillis;
		System.out.println(".. done read file:" + file + " => count:" + count + ", took " + millis + " ms");
	}

	private static class CountingReadNodeEntry implements Consumer<ReadNodeEntry> {
		@Getter
		int count;

		@Override
		public void accept(ReadNodeEntry t) {
			count++;
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

	private void doScanWriteRecursiveNode(File outputFile, Path scanRootPath) throws Exception {
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

		doWriteFile(outputFile, rootNode);

		doReadFile(attrRegistry, outputFile);
	}

}
