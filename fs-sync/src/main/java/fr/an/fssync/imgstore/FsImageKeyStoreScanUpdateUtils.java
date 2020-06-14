package fr.an.fssync.imgstore;

import java.io.File;

import fr.an.fssync.fs.io.JavaIoDirScannerImageBuilder;
import fr.an.fssync.model.FsPath;

public class FsImageKeyStoreScanUpdateUtils {

    public static void scanDirAndUpdateDb(File scanDir, FsImageKeyStore db) {

        StorageUpdateImageEntryVisitor dbUpdater = new StorageUpdateImageEntryVisitor(db);
        JavaIoDirScannerImageBuilder scanner = new JavaIoDirScannerImageBuilder(dbUpdater);

        dbUpdater.begin();

        // *** The Biggy ***
        scanner.recurseScan(scanDir, FsPath.ROOT);

        dbUpdater.end();
    }
}
