package fr.an.hadoop.fsimagetool.io.leveldb;

import java.io.File;

import fr.an.hadoop.fsimagetool.io.DirScannerImageBuilder;
import fr.an.hadoop.fsimagetool.io.leveldb.ImageEntryLevelDBStorage.ImageEntryLevelDBUpdater;

public class LevelDbImageUtils {

	public static void scanDirAndUpdateDb(File scanDir,
			ImageEntryLevelDBStorage db) {

		ImageEntryLevelDBUpdater dbUpdater = new ImageEntryLevelDBUpdater(db);
		DirScannerImageBuilder scanner = new DirScannerImageBuilder(dbUpdater);

		// *** The Biggy ***
		scanner.recurseScan(scanDir);
	
		dbUpdater.close();
	}
}
