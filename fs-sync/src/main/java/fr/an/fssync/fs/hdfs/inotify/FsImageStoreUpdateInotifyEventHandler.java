package fr.an.fssync.fs.hdfs.inotify;

import java.util.List;

import org.apache.hadoop.fs.XAttr;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.hdfs.inotify.Event.AppendEvent;
import org.apache.hadoop.hdfs.inotify.Event.CloseEvent;
import org.apache.hadoop.hdfs.inotify.Event.CreateEvent;
import org.apache.hadoop.hdfs.inotify.Event.CreateEvent.INodeType;
import org.apache.hadoop.hdfs.inotify.Event.MetadataUpdateEvent;
import org.apache.hadoop.hdfs.inotify.Event.MetadataUpdateEvent.MetadataType;

import fr.an.fssync.imgstore.LevelDbFsImageStore;
import fr.an.fssync.model.FsEntryInfo;
import fr.an.fssync.model.FsPath;

import org.apache.hadoop.hdfs.inotify.Event.RenameEvent;
import org.apache.hadoop.hdfs.inotify.Event.TruncateEvent;
import org.apache.hadoop.hdfs.inotify.Event.UnlinkEvent;

import lombok.val;

/**
 * 
 *
 */
public class FsImageStoreUpdateInotifyEventHandler extends InotifyEventHandler {

    private LevelDbFsImageStore imageStore;
    
    public FsImageStoreUpdateInotifyEventHandler(LevelDbFsImageStore imageStore) {
	this.imageStore = imageStore;
    }

    @Override
    public void handleCreateEvent(CreateEvent event) {
	FsPath path = FsPath.of(event.getPath());
	val entryInfoBuilder = FsEntryInfo.builder();
	INodeType iNodeType = event.getiNodeType();
	entryInfoBuilder.iNodeType(iNodeType);
	entryInfoBuilder.ctime(event.getCtime());
	entryInfoBuilder.replication(event.getReplication());
	entryInfoBuilder.ownerName(event.getOwnerName());
	entryInfoBuilder.groupName(event.getGroupName());
	entryInfoBuilder.perms(event.getPerms());
	if (iNodeType == INodeType.SYMLINK) {
	    entryInfoBuilder.symlinkTarget(event.getSymlinkTarget());
	}
	entryInfoBuilder.defaultBlockSize(event.getDefaultBlockSize());
	FsEntryInfo entryInfo = entryInfoBuilder.build();
	
	imageStore.writePathInfo(path, entryInfo, null);
    }

    @Override
    public void handleCloseEvent(CloseEvent event) {
	FsPath path = FsPath.of(event.getPath());

	FsEntryInfo prevEntryInfo = imageStore.readPathInfo(path);
	val b = prevEntryInfo.builderCopy();
	b.fileSize(event.getFileSize());
	b.mtime(event.getTimestamp());
	FsEntryInfo entryInfo = b.build();
	
	imageStore.writePathInfo(path, entryInfo, prevEntryInfo);
    }

    @Override
    public void handleAppendEvent(AppendEvent event) {
	FsPath path = FsPath.of(event.getPath());
	boolean newBlock = event.toNewBlock();
	// TODO ignore?
    }

    @Override
    public void handleRenameEvent(RenameEvent event) {
	FsPath srcPath = FsPath.of(event.getSrcPath());
	FsPath dstPath = FsPath.of(event.getDstPath());

	FsEntryInfo prevEntryInfo = imageStore.readPathInfo(srcPath);
	val b = prevEntryInfo.builderCopy();
	b.mtime(event.getTimestamp());
	FsEntryInfo entryInfo = b.build();
	
	imageStore.deletePathInfo(srcPath, prevEntryInfo);
	imageStore.writePathInfo(dstPath, entryInfo, null);
    }

    @Override
    public void handleMetadataUpdateEvent(MetadataUpdateEvent event) {
	FsPath path = FsPath.of(event.getPath());
	MetadataType metadataType = event.getMetadataType();

	FsEntryInfo prevEntryInfo = imageStore.readPathInfo(path);
	val b = prevEntryInfo.builderCopy();

	switch(metadataType) {
	case TIMES:
	    long mtime = event.getMtime();
	    if (mtime == prevEntryInfo.mtime) {
		return;
	    }
	    b.mtime(mtime);
	    // ignore.. long atime = event.getAtime();
	    break;
	case REPLICATION:
	    b.replication(event.getReplication());
	    break;
	case OWNER:
	    b.ownerName(event.getOwnerName());
	    b.groupName(event.getGroupName());
	    break;
	case PERMS:
	    b.perms(event.getPerms());
	    break;
	case ACLS:
	    List<AclEntry> acls = event.getAcls();
	    // TODO
	    break;
	case XATTRS:
	    List<XAttr> xAttrs= event.getxAttrs();
	    boolean xAttrsRemoved = event.isxAttrsRemoved();
	    // TODO
	    break;
	}

	FsEntryInfo entryInfo = b.build();
	imageStore.writePathInfo(path, entryInfo, prevEntryInfo);
    }

    @Override
    public void handleUnlinkEvent(UnlinkEvent event) {
	FsPath path = FsPath.of(event.getPath());
	// long timestamp = event.getTimestamp();
	FsEntryInfo prevEntryInfo = imageStore.readPathInfo(path);

	imageStore.deletePathInfo(path, prevEntryInfo);
    }

    @Override
    public void handleTruncateEvent(TruncateEvent event) {
	FsPath path = FsPath.of(event.getPath());
	
	FsEntryInfo prevEntryInfo = imageStore.readPathInfo(path);
	val b = prevEntryInfo.builderCopy();
	b.mtime(event.getTimestamp());
	b.fileSize(event.getFileSize());
	
	FsEntryInfo entryInfo = b.build();
	imageStore.writePathInfo(path, entryInfo, prevEntryInfo);
    }

}
