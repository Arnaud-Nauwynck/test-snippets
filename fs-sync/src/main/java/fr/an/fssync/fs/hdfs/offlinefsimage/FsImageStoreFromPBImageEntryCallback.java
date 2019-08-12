package fr.an.fssync.fs.hdfs.offlinefsimage;

import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.PermissionStatus;
import org.apache.hadoop.hdfs.inotify.Event.CreateEvent.INodeType;
import org.apache.hadoop.hdfs.server.namenode.FsImageProto.INodeSection.INodeDirectory;
import org.apache.hadoop.hdfs.server.namenode.FsImageProto.INodeSection.INodeFile;
import org.apache.hadoop.hdfs.server.namenode.FsImageProto.INodeSection.INodeSymlink;

import com.google.common.collect.ImmutableList;

import fr.an.fssync.fs.hdfs.inotify.PBImageINodeCallback;
import fr.an.fssync.imgstore.FsImageKeyStore;
import fr.an.fssync.model.FsEntryInfo;
import fr.an.fssync.model.FsPath;
import fr.an.fssync.model.FsEntryInfo.FsEntryInfoChgBuilder;

/**
 * implementation of PBImageINodeCallback to update imageStore
 */
public class FsImageStoreFromPBImageEntryCallback extends PBImageINodeCallback {

    private FsImageKeyStore imageStore;
    private FsPathSeen pathSeen;
    
    public static abstract class FsPathSeen {
	public abstract void put(FsPath path);
    }

    public FsImageStoreFromPBImageEntryCallback(FsImageKeyStore imageStore, FsPathSeen pathSeen) {
	this.imageStore = imageStore;
	this.pathSeen = pathSeen;
    }

    @Override
    public void onFile(FsPath path, INodeFile file, 
	    PermissionStatus perm, ImmutableList<AclEntry> aclEntries,
	    int replication, long mtime, long accessTime, 
	    long preferredBlockSize, int blocksCount, 
	    long fileSize) {
	FsEntryInfo prevEntryInfo = imageStore.readPathInfo(path);
	pathSeen.put(path);
	
	FsEntryInfoChgBuilder infoChg = new FsEntryInfoChgBuilder(prevEntryInfo);
	infoChg.iNodeType(INodeType.FILE);
	infoChg.perm(perm);
	infoChg.aclEntries(aclEntries);
	infoChg.replication(replication);
	infoChg.mtime(mtime);
	// ignore.. infoChg.atime(accessTime);
	// ignore.. long preferredBlockSize, 
	// ignore.. int blocksCount, 
	infoChg.fileSize(fileSize);
	
	if (infoChg.isChg()) {
	    imageStore.writePathInfo(path, infoChg.build(), prevEntryInfo);
	}
    }


    @Override
    public void onDir(FsPath path, INodeDirectory dir, PermissionStatus perm, ImmutableList<AclEntry> aclEntries,
	    long mtime
//	    long nsQuota, // dir.getNsQuota()
//	    long dsQuota // dir.getDsQuota()
    ) {
	FsEntryInfo prevEntryInfo = imageStore.readPathInfo(path);
	pathSeen.put(path);

	FsEntryInfoChgBuilder infoChg = new FsEntryInfoChgBuilder(prevEntryInfo);
	infoChg.iNodeType(INodeType.DIRECTORY);
	infoChg.perm(perm);
	infoChg.aclEntries(aclEntries);
	// infoChg.replication(0);
	infoChg.mtime(mtime);
	// ignore.. nsQuota, 
	// ignore.. dsQuota, 
	// infoChg.fileSize(0);
	
	if (infoChg.isChg()) {
	    imageStore.writePathInfo(path, infoChg.build(), prevEntryInfo);
	}
    }

    @Override
    public void onSymlink(FsPath path, INodeSymlink s, 
	    PermissionStatus perm, long mtime, long atime) {
	FsEntryInfo prevEntryInfo = imageStore.readPathInfo(path);
	pathSeen.put(path);

	FsEntryInfoChgBuilder infoChg = new FsEntryInfoChgBuilder(prevEntryInfo);
	infoChg.iNodeType(INodeType.SYMLINK);
	infoChg.perm(perm);
	infoChg.aclEntries(null);
	// infoChg.replication(0);
	infoChg.mtime(mtime);
	// infoChg.fileSize(0);
	
	if (infoChg.isChg()) {
	    imageStore.writePathInfo(path, infoChg.build(), prevEntryInfo);
	}
    }

}
