package fr.an.test.fsscancomp.nio;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.junit.Test;

import fr.an.test.fsscancomp.img.ImageEntryHandler;
import fr.an.test.fsscancomp.img.ImageFragmentsWriterUtils;
import fr.an.test.fsscancomp.utils.ProgressLogImageHandler;
import lombok.val;

public class JavaNioDirScannerImageBuilderTest {
	
	Path dir = FileSystems.getDefault().getPath("d:/arn/downloadTools"); // huge.. 
	Path subdir = FileSystems.getDefault().getPath("d:/arn/downloadTools/jhipster"); // 880M / 14_000 files
	Path subdir2 = FileSystems.getDefault().getPath("d:/arn/downloadTools/hadoop"); // 1.6Go  / 108_000 files
	Path subdir3 = FileSystems.getDefault().getPath("d:/arn/downloadTools/hadoop/ambari"); // 770Mo  / 75_000 files

	@Test
	public void testBenchScanDir() {
		long startTime = System.currentTimeMillis();

		ImageEntryHandler entryHandler = new ProgressLogImageHandler();
		JavaNioDirScannerImageBuilder scanner = new JavaNioDirScannerImageBuilder(entryHandler);
				
		// *** The Biggy ***
		scanner.scan(dir);

		entryHandler.close();

		long millis = System.currentTimeMillis() - startTime;
		System.out.println("scanning using recursiveScan java.nio. took " + millis + "ms");
	}
	
	@Test
	public void testScanDirWriteImage() {
		File destImgDir = new File("out2");
		
		File debugTextFile = null; // new File(destImgDir, "debugScan.txt");
			
		scanDirWriteImage(dir, 
				destImgDir, "img", 5, 10_000,
				debugTextFile, null);
	}

	// scan+computing MD5 takes ~3s on subdir: 880M / 14_000 files
	// scan+computing MD5 subdir3 takes ~??s on dir: 770M / 75_000 files
	@Test
	public void testScanDir_EnrichMD5() {
		long startTime = System.currentTimeMillis();

		val progressHandler = new ProgressLogImageHandler();
		val entryHandler = new Md5EnricherEntryHandler(progressHandler);

		val scanner = new JavaNioDirScannerImageBuilder(entryHandler);
		scanner.scan(subdir);
		entryHandler.close();

		long millis = System.currentTimeMillis() - startTime;
		long md5Secs = TimeUnit.NANOSECONDS.toSeconds(entryHandler.getMd5NanoTotal());
		System.out.println("scanning using (java.nio) recursiveScan+md5 " + subdir + " took " + millis + "ms, md5 computation: " + md5Secs + "s");
	}
	
	// scan+computing MD5 on subdir  takes ~18s (total cpu:124s) on subdir: 880M / 14_000 files
	// scan+computing MD5 on subdir2 takes ~150s (total cpu: 1075s) on dir: 770M / 75_000 files
	@Test
	public void testScanDir_EnrichMD5_ThreadPool() {
		int nThreads = Math.max(2, Runtime.getRuntime().availableProcessors());
		System.out.println("using " + nThreads + " threads");
		val threadPool = Executors.newFixedThreadPool(nThreads);

		long startTime = System.currentTimeMillis();

		val progressHandler = new ProgressLogImageHandler();
		val entryHandler = new ExecutorMd5EnricherEntryHandler(progressHandler, threadPool);

		val scanner = new JavaNioDirScannerImageBuilder(entryHandler);
		scanner.scan(subdir2);
		entryHandler.close();

		long millis = System.currentTimeMillis() - startTime;
		long md5Secs = TimeUnit.NANOSECONDS.toSeconds(entryHandler.getMd5NanoTotal());
		System.out.println("scanning using (java.nio) recursiveScan+md5 " + subdir + " took " + millis + "ms, md5 computation: " + md5Secs + "s");
	}

	
	@Test
	public void testScanDirWriteImage_EnrichMD5_ThreadPool() {
		File destImgDir = new File("out2");
		
		File debugTextFile = null; // new File(destImgDir, "debugScan.txt");
		int nThreads = Math.max(2, Runtime.getRuntime().availableProcessors());
		val threadPool = Executors.newFixedThreadPool(nThreads);
		Function<ImageEntryHandler,ImageEntryHandler> md5EntryHandlerEnricher = 
				e -> new ExecutorMd5EnricherEntryHandler(e, threadPool);
		
		scanDirWriteImage(subdir, 
				destImgDir, "subimg_with_md5", 5, 10_000,
				debugTextFile,
				md5EntryHandlerEnricher);
	}
	
	
	
	public static void scanDirWriteImage(Path rootScanDir, 
			File toBaseDir, String destImageName, int hashSize, int maxFragmentSize,
			File debugTextFile,
			Function<ImageEntryHandler,ImageEntryHandler> entryEnricher
			) {
		ImageEntryHandler entryHandler = ImageFragmentsWriterUtils.createSplitThenSortBufferedWriter(toBaseDir, destImageName, hashSize,
				maxFragmentSize, debugTextFile,
				entryEnricher);
		val scanner = new JavaNioDirScannerImageBuilder(entryHandler);

		// *** The Biggy ***
		scanner.scan(rootScanDir);
		
		entryHandler.close();
	}

}
