package fr.an.fssync.imgstore;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

import fr.an.fssync.model.FsEntry;
import fr.an.fssync.model.FsEntryInfo;
import fr.an.fssync.model.FsEntryInfoChangeListener;
import fr.an.fssync.model.FsPath;
import fr.an.fssync.utils.CopyOnWriteArrayUtils;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * abstract Storage class for an FsImage
 * 
 * using codec to encode/decode value in key-value store (LevelDB,RocksDb,HashMap,..)
 *
 */
@Slf4j
public abstract class FsImageKeyStore implements Closeable {

    protected FsEntryInfoCodec dbEntryInfoCodec = FsEntryInfoCodec.DEFAULT;

    private FsEntryInfoChangeListener[] listeners = new FsEntryInfoChangeListener[0];

    // ------------------------------------------------------------------------
    
    protected FsImageKeyStore() {
    }
    
    public void init() {
    }
    
    @Override
    public void close() throws IOException {
	// TODO Auto-generated method stub
	
    }
    
    // ------------------------------------------------------------------------

    public void addListener(FsEntryInfoChangeListener p) {
        this.listeners = CopyOnWriteArrayUtils.withAdd(listeners, p);
    }

    public void removeListener(FsEntryInfoChangeListener p) {
        this.listeners = CopyOnWriteArrayUtils.withRemove(listeners, p);
    }

    protected void fireEntryChange(FsPath path, FsEntryInfo entryInfo, FsEntryInfo prevEntryInfo) {
	for(val l : listeners) {
	    l.onChange(path, entryInfo, prevEntryInfo);
	}
    }
    
    public void writePathInfo(FsPath path, FsEntryInfo entryInfo, FsEntryInfo prevEntryInfo) {
	byte[] key = path.toUri().getBytes();
	byte[] value = dbEntryInfoCodec.writeBytes(entryInfo);

	try {
	    doWriteKeyValue(key, value);
	} catch(Exception ex) {
	    log.error("Failed to write!", ex);
	    throw new RuntimeException("Failed to write!", ex);
	}

	fireEntryChange(path, entryInfo, prevEntryInfo);
    }

    protected abstract void doWriteKeyValue(byte[] key, byte[] value) throws Exception;

    public FsEntryInfo readPathInfo(FsPath path) {
	byte[] key = path.toUri().getBytes();
	byte[] valueBytes;
	try {
	    valueBytes = doReadKeyValue(key);
	} catch(Exception ex) {
	    log.error("Failed to read!", ex);
	    throw new RuntimeException("Failed to read!", ex);
	}

	if (valueBytes == null) {
	    return null;
	}
	return dbEntryInfoCodec.readBytes(valueBytes);
    }

    protected abstract byte[] doReadKeyValue(byte[] key) throws Exception;

    public void deletePathInfo(FsPath path, FsEntryInfo prevEntryInfo) {
	byte[] key = path.toUri().getBytes();
	try {
	    doDeleteKey(key);
	} catch(Exception ex) {
	    throw new RuntimeException("Failed to delete!", ex);
	}
	fireEntryChange(path, null, prevEntryInfo);
    }

    protected abstract void doDeleteKey(byte[] key)  throws Exception;
    
    public abstract Iterator<FsEntry> entryIterator();
    public abstract Iterator<FsEntry> entryIterator(FsPath seekFirstKey);

}
