package fr.an.fssync.sortedimgfrag;

import java.io.Closeable;

import fr.an.fssync.model.FsEntry;

public abstract class FsEntryReader implements Closeable {
    
    @Override
    public abstract void close();

    public abstract FsEntry readEntry();
    
}
