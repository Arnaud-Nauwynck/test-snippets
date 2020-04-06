package fr.an.fssync.imgstore;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import fr.an.fssync.model.FsEntry;
import fr.an.fssync.model.FsPath;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * implementation of FsImageKeyStore, using LevelDB
 */
@Slf4j
public class LevelDbFsImageStore extends FsImageKeyStore {

    private File dbDir;
    private org.iq80.leveldb.DB db;

    public LevelDbFsImageStore(File dbDir) {
	this.dbDir = dbDir;
    }

    @Override
    public void init() {
	Options dbOptions = new Options();
	dbOptions.createIfMissing(true);
	try {
	    this.db = JniDBFactory.factory.open(dbDir, dbOptions);
	} catch (Throwable ex) {
	    try {
		log.error("Failed to init levelDB jni .. fallback to java, ex:" + ex.getMessage());
		this.db = Iq80DBFactory.factory.open(dbDir, dbOptions);
	    } catch (Exception ex2) {
		throw new RuntimeException("Failed", ex);
	    }
	}
    }

    @Override
    public void close() {
	org.iq80.leveldb.DB tmp = db;
	if (tmp == null) {
	    return;
	}
	this.db = null;
	try {
	    tmp.close();
	} catch (IOException ex) {
	    throw new RuntimeException("Failed to close!", ex);
	}
    }
    
    // ------------------------------------------------------------------------
    
    @Override
    protected void doWriteKeyValue(byte[] key, byte[] value) throws Exception {
	db.put(key, value);
    }

    @Override
    protected byte[] doReadKeyValue(byte[] key) throws Exception {
	return db.get(key);
    }

    @Override
    protected void doDeleteKey(byte[] key) throws Exception {
	db.delete(key);
    }
    
    protected class InnerFsEntryIterator implements Iterator<FsEntry> {
	DBIterator dbIter;
	FsEntry curr = new FsEntry(null, null); // mutable.. could be immutable new but optim gc(?)
	
	public InnerFsEntryIterator(DBIterator dbIter) {
	    this.dbIter = dbIter;
	}

	@Override
	public boolean hasNext() {
	    return dbIter.hasNext();
	}

	@Override
	public FsEntry next() {
	    Entry<byte[], byte[]> dbEntry = dbIter.next();
	    String path = new String(dbEntry.getKey());
	    FsPath fsPath = FsPath.of(path);
	    val info = dbEntryInfoCodec.readBytes(dbEntry.getValue());
	    curr.set(fsPath, info);
	    return curr; // new PathWithInfo(path, info);
	}

    }
    
    @Override
    public Iterator<FsEntry> entryIterator() {
	return new InnerFsEntryIterator(db.iterator());
    }

    @Override
    public Iterator<FsEntry> entryIterator(FsPath seekFirstPath) {
	DBIterator dbIterator = db.iterator();
	dbIterator.seek(seekFirstPath.toUri().getBytes());
	return new InnerFsEntryIterator(dbIterator);
    }

}
