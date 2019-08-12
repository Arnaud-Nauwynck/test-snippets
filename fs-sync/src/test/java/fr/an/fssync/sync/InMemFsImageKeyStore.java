package fr.an.fssync.sync;

import java.util.Iterator;
import java.util.Map.Entry;

import fr.an.fssync.imgstore.FsImageKeyStore;
import fr.an.fssync.model.FsEntry;
import fr.an.fssync.model.FsEntryInfo;
import fr.an.fssync.model.FsPath;

import java.util.TreeMap;

public class InMemFsImageKeyStore extends FsImageKeyStore {

    private TreeMap<FsPath,FsEntryInfo> data = new TreeMap<>();

    @Override
    public void writePathInfo(FsPath path, FsEntryInfo entryInfo, FsEntryInfo prevEntryInfo) {
	data.put(path, entryInfo);

	fireEntryChange(path, entryInfo, prevEntryInfo);
    }

    @Override
    protected void doWriteKeyValue(byte[] key, byte[] value) {
	throw new UnsupportedOperationException();
    }

    @Override
    public FsEntryInfo readPathInfo(FsPath path) {
	return data.get(path);
    }

    @Override
    protected byte[] doReadKeyValue(byte[] key) {
	throw new UnsupportedOperationException();
    }

    public void deletePathInfo(FsPath path, FsEntryInfo prevEntryInfo) {
	data.remove(path);
	fireEntryChange(path, null, prevEntryInfo);
    }

    @Override
    protected void doDeleteKey(byte[] key) {
	throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<FsEntry> entryIterator() {
	Iterator<Entry<FsPath, FsEntryInfo>> dataIter = data.entrySet().iterator();
	return new InnerFsEntryIterator(dataIter);
    }

    @Override
    public Iterator<FsEntry> entryIterator(FsPath fromPath) {
	Iterator<Entry<FsPath, FsEntryInfo>> dataIter = data.tailMap(fromPath).entrySet().iterator();
	return new InnerFsEntryIterator(dataIter);
    }

    protected class InnerFsEntryIterator implements Iterator<FsEntry> {
	Iterator<Entry<FsPath, FsEntryInfo>> dataIter;
	FsEntry curr = new FsEntry(null, null); // mutable.. could be immutable new but optim gc(?)
	
	public InnerFsEntryIterator(Iterator<Entry<FsPath, FsEntryInfo>> dataIter) {
	    this.dataIter = dataIter;
	}
    
	@Override
	public boolean hasNext() {
	    return dataIter.hasNext();
	}

	@Override
	public FsEntry next() {
	    Entry<FsPath, FsEntryInfo> e = dataIter.next();
	    curr.set(e.getKey(), e.getValue());
	    return curr;
	}
    }
    
}
