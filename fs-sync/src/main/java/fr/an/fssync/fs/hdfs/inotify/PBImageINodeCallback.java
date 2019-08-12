package fr.an.fssync.fs.hdfs.inotify;

import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.PermissionStatus;
import org.apache.hadoop.hdfs.server.namenode.FsImageProto.INodeSection.INodeDirectory;
import org.apache.hadoop.hdfs.server.namenode.FsImageProto.INodeSection.INodeFile;
import org.apache.hadoop.hdfs.server.namenode.FsImageProto.INodeSection.INodeSymlink;

import com.google.common.collect.ImmutableList;

import fr.an.fssync.model.FsPath;

public abstract class PBImageINodeCallback {

    public abstract void onFile(FsPath path, INodeFile file,
	    PermissionStatus perm,
	    ImmutableList<AclEntry> aclEntries,
	    int replication,
	    long mtime, long accessTime,
	    long preferredBlockSize,
	    int blocksCount,
	    long fileSize);

    public abstract void onDir(FsPath path, INodeDirectory dir,
	    PermissionStatus perm,
	    ImmutableList<AclEntry> aclEntries,
	    long mtime
//	    long nsQuota, // dir.getNsQuota()
//	    long dsQuota // dir.getDsQuota()
	    );

    public abstract void onSymlink(FsPath path, INodeSymlink s, 
	    PermissionStatus perm, 
	    long mtime, long atime);

}