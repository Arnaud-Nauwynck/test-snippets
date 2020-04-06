package fr.an.fssync.imgstore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Iterator;

import org.junit.Test;

import fr.an.fssync.imgstore.LevelDbFsImageStore;
import fr.an.fssync.model.FsEntry;
import fr.an.fssync.model.visitor.FsEntryVisitor;
import fr.an.fssync.model.visitor.utils.MulticastFsEntryVisitor;
import fr.an.fssync.model.visitor.utils.PrettyPrinterFsEntryVisitor;
import fr.an.fssync.model.visitor.utils.ProgressLogFsEntryVisitor;
import fr.an.fssync.sync.FsImageKeyStoreScanUpdateUtils;
import lombok.val;

public class FsImageKeyStoreScanUpdateUtilsTest {

    File scanDir = new File("d:/arn/downloadTools");
    File dbDir = new File("out/downloadTools-db");

    @Test
    public void testScanUpdateDb() {
	if (!dbDir.exists()) {
	    dbDir.mkdirs();
	}
	try (LevelDbFsImageStore db = new LevelDbFsImageStore(dbDir)) {
	    FsImageKeyStoreScanUpdateUtils.scanDirAndUpdateDb(scanDir, db);
	}
    }

    @Test
    public void testReadDb() {
	if (!dbDir.exists()) {
	    dbDir.mkdirs();
	}
	try (LevelDbFsImageStore db = new LevelDbFsImageStore(dbDir)) {

	    File resFile = new File(dbDir, "img-result-sorted-all.txt");
	    PrintStream resOut;
	    try {
		resOut = new PrintStream(new BufferedOutputStream(new FileOutputStream(resFile)));
	    } catch (FileNotFoundException ex) {
		throw new RuntimeException("Failed", ex);
	    }
	    FsEntryVisitor resHandler = new PrettyPrinterFsEntryVisitor(resOut);

	    FsEntryVisitor imageHandler = new MulticastFsEntryVisitor(
		    new FsEntryVisitor[] { new ProgressLogFsEntryVisitor(), resHandler });
	    for(Iterator<FsEntry> iter = db.entryIterator(); iter.hasNext(); ) {
		val dbe = iter.next();
		imageHandler.visit(dbe);
	    }
	}
    }

}
