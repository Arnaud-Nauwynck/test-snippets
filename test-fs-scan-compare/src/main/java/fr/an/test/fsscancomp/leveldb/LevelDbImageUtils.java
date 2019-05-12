package fr.an.test.fsscancomp.leveldb;

import java.io.File;

import fr.an.test.fsscancomp.io.JavaIoDirScannerImageBuilder;
import fr.an.test.fsscancomp.leveldb.ImageEntryLevelDBStorage.ImageEntryLevelDBUpdater;

public class LevelDbImageUtils {

	public static void scanDirAndUpdateDb(File scanDir,
			ImageEntryLevelDBStorage db) {

		ImageEntryLevelDBUpdater dbUpdater = new ImageEntryLevelDBUpdater(db);
		JavaIoDirScannerImageBuilder scanner = new JavaIoDirScannerImageBuilder(dbUpdater);

		// *** The Biggy ***
		scanner.recurseScan(scanDir);
	
		dbUpdater.close();
	}
}
