package fr.an.test.fsscancomp.io;

import java.io.File;

import org.junit.Test;

import fr.an.test.fsscancomp.img.ImageEntryHandler;
import fr.an.test.fsscancomp.utils.ProgressLogImageHandler;

public class JavaIoDirScannerImageBuilderTest {
	File dir = new File("d:/arn/downloadTools");

	@Test
	public void testBenchScanDir() {
		long startTime = System.currentTimeMillis();

		ImageEntryHandler entryHandler = new ProgressLogImageHandler();
		JavaIoDirScannerImageBuilder scanner = new JavaIoDirScannerImageBuilder(entryHandler);
				
		// *** The Biggy ***
		scanner.recurseScan(dir);

		entryHandler.close();

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
