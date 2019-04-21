package fr.an.hadoop.fsimagetool.io.leveldb;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import fr.an.hadoop.fsimagetool.io.ImageEntryHandler;
import fr.an.hadoop.fsimagetool.io.utils.MulticastImageHandler;
import fr.an.hadoop.fsimagetool.io.utils.PrettyPrinterImageHandler;
import fr.an.hadoop.fsimagetool.io.utils.ProgressLogImageHandler;

public class LevelDbImageUtilsTest {

	File scanDir = new File("d:/arn/downloadTools");
	File dbDir = new File("out/downloadTools-db");

	@Test
	public void testScanUpdateDb() {
		if (!dbDir.exists()) {
			dbDir.mkdirs();
		}
		try (ImageEntryLevelDBStorage db = new ImageEntryLevelDBStorage(dbDir)) {
			LevelDbImageUtils.scanDirAndUpdateDb(scanDir, db);
		}
	}

	@Test
	public void testReadDb() {
		try (ImageEntryLevelDBStorage db = new ImageEntryLevelDBStorage(dbDir)) {

			File resFile = new File(dbDir, "img-result-sorted-all.txt");
			PrintStream resOut;
			try {
				resOut = new PrintStream(
						new BufferedOutputStream(new FileOutputStream(resFile)));
			} catch (FileNotFoundException ex) {
				throw new RuntimeException("Failed", ex);
			}
			ImageEntryHandler resHandler = new PrettyPrinterImageHandler(resOut);
			
			try (ImageEntryHandler imageHandler = new MulticastImageHandler(new ImageEntryHandler[] {
					new ProgressLogImageHandler(),
					resHandler})) {
				db.scan((dbe) -> {
					imageHandler.handle(dbe.imageEntry);
				});
			}
		}
	}
	
}
