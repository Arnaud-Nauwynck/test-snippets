package fr.an.fssync.model;

public interface FsEntryInfoChangeListener {

    public void onChange(FsPath path, FsEntryInfo entryInfo, FsEntryInfo prevEntryInfo);
    
}
