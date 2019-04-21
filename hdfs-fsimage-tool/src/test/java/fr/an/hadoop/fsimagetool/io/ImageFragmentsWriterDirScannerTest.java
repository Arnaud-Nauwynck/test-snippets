package fr.an.hadoop.fsimagetool.io;

import java.io.File;

import org.junit.Test;

public class ImageFragmentsWriterDirScannerTest {

	@Test
	public void testScanDirWriteImage() {
		File dir = new File("d:/arn/downloadTools");
		File destImgDir = new File("out2");
		
		File debugTextFile = new File(destImgDir, "debugScan.txt");
			
		ImageFragmentsWriterDirScanner.scanDirWriteImage(dir, 
				destImgDir, "img", 5, 10_000,
				debugTextFile);
	}
}
