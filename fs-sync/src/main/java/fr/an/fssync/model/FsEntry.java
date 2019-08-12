package fr.an.fssync.model;

import java.util.Comparator;

import org.apache.hadoop.hdfs.inotify.Event.CreateEvent.INodeType;

public class FsEntry {
    
    public FsPath path;
    public FsEntryInfo info;

    public FsEntry(FsPath path, FsEntryInfo info) {
	this.path = path;
	this.info = info;
    }

    public void set(FsPath path, FsEntryInfo info) {
        this.path = path;
        this.info = info;
    }

    public boolean isFile() {
	return info.iNodeType == INodeType.FILE;
    }
    
    public static final Comparator<FsEntry> PATH_COMPARATOR = new Comparator<FsEntry>() {
	public int compare(final FsEntry l, final FsEntry r) {
	    return l.path.compareTo(r.path);
	}
    };

}
