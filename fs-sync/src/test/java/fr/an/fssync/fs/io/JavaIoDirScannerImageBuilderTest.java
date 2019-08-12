package fr.an.fssync.fs.io;

import java.io.File;

import org.junit.Test;

import fr.an.fssync.fs.io.JavaIoDirScannerImageBuilder;
import fr.an.fssync.model.FsPath;
import fr.an.fssync.model.visitor.FsEntryVisitor;
import fr.an.fssync.model.visitor.utils.ProgressLogFsEntryVisitor;

public class JavaIoDirScannerImageBuilderTest {
	File dir = new File("d:/arn/downloadTools");

	@Test
	public void testBenchScanDir() {
		long startTime = System.currentTimeMillis();

		FsEntryVisitor entryHandler = new ProgressLogFsEntryVisitor();
		JavaIoDirScannerImageBuilder scanner = new JavaIoDirScannerImageBuilder(entryHandler);
				
		entryHandler.begin();
		
		// *** The Biggy ***
		scanner.recurseScan(dir, FsPath.ROOT);

		entryHandler.end();

		long millis = System.currentTimeMillis() - startTime;
		System.out.println("scanning using recursiveScan java.io. took " + millis + "ms");
	}
	
	@Test
	public void testScanDirWriteImage() {
		File destImgDir = new File("out2");
		
		File debugTextFile = null; // new File(destImgDir, "debugScan.txt");
			
		JavaIoDirScannerImageBuilder.scanDirWriteImage(dir, 
				destImgDir, "img", 5, 10_000,
				debugTextFile);
	}
}
