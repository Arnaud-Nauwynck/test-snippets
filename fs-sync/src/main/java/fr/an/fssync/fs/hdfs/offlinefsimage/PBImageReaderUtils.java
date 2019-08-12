package fr.an.fssync.fs.hdfs.offlinefsimage;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.PermissionStatus;
import org.apache.hadoop.hdfs.server.namenode.FSImageFormatPBINode;
import org.apache.hadoop.hdfs.server.namenode.FsImageProto.INodeSection.AclFeatureProto;
import org.apache.hadoop.hdfs.server.namenode.FsImageProto.INodeSection.INode;
import org.apache.hadoop.hdfs.server.namenode.FsImageProto.INodeSection.INodeDirectory;
import org.apache.hadoop.hdfs.server.namenode.FsImageProto.INodeSection.INodeFile;
import org.apache.hadoop.hdfs.server.namenode.FsImageProto.INodeSection.INodeSymlink;

import com.google.common.collect.ImmutableList;

import fr.an.fssync.fs.hdfs.inotify.PBImageINodeCallback;
import fr.an.fssync.model.FsPath;

public class PBImageReaderUtils {

    public static void readFsImage(File fsImage, PBImageINodeCallback callback) {
	String tempPath = "";

	try (ToCallbackPBImageEntryVisitor pbImgVisitor = new ToCallbackPBImageEntryVisitor(tempPath, callback)) {
	    pbImgVisitor.visit(new RandomAccessFile(fsImage, "r"));
	} catch(IOException ex) {
	    throw new RuntimeException("Failed to read fsImage", ex);
	}
    }

    /**
     * extends package protected class PBImageTextWriter !!! => override getEntry()
     * which then ouput to NullOutputStream, with side-effect to callback.onEntry()
     */
    static class ToCallbackPBImageEntryVisitor extends PBImageTextWriter {
	private PBImageINodeCallback callback;

	public ToCallbackPBImageEntryVisitor(String tempPath, PBImageINodeCallback callback) throws IOException {
	    super(new PrintStream(new NullOutputStream()), tempPath);
	    this.callback = callback;
	}

	@Override
	protected String getEntry(String parent, INode inode) {
	    String inodeName = inode.getName().toStringUtf8();
	    Path hdfsPath = new Path(parent.isEmpty() ? "/" : parent, inodeName.isEmpty() ? "/" : inodeName);
	    FsPath path = FsPath.of(hdfsPath.toString());

	    PermissionStatus perm;
	    boolean hasAcl = false;
	    ImmutableList<AclEntry> aclEntries = null;

	    switch (inode.getType()) {
	    case FILE: {
		INodeFile file = inode.getFile();
		perm = getPermission(file.getPermission());
		hasAcl = file.hasAcl() && file.getAcl().getEntriesCount() > 0;
		if (hasAcl) {
		    aclEntries = getAclEntries(file.getAcl());
		}
		int replication = file.getReplication();
		long mtime = file.getModificationTime();
		long accessTime = file.getAccessTime();
		long preferredBlockSize = file.getPreferredBlockSize();
		int blocksCount = file.getBlocksCount();
		long fileSize = FSImageLoader.getFileSize(file);

		callback.onFile(path, file, perm, aclEntries, 
			replication, mtime, accessTime, preferredBlockSize,
			blocksCount, fileSize);

	    }
		break;
	    case DIRECTORY: {
		INodeDirectory dir = inode.getDirectory();
		perm = getPermission(dir.getPermission());
		hasAcl = dir.hasAcl() && dir.getAcl().getEntriesCount() > 0;
		if (hasAcl) {
		    aclEntries = getAclEntries(dir.getAcl());
		}
		long mtime = dir.getModificationTime();

		callback.onDir(path, dir, perm, aclEntries, mtime
			// dir.getNsQuota(), dir.getDsQuota()
			);
	    }
		break;
	    case SYMLINK: {
		INodeSymlink s = inode.getSymlink();
		perm = getPermission(s.getPermission());
		long mtime = s.getModificationTime();
		long atime = s.getAccessTime();

		callback.onSymlink(path, s, perm, mtime, atime);

	    }
		break;
	    default:
		break;
	    }

	    return "";
	}

	protected ImmutableList<AclEntry> getAclEntries(AclFeatureProto aclFeatureProto) {
	    return FSImageFormatPBINode.Loader.loadAclEntries(aclFeatureProto, stringTable);
	}

	@Override
	protected String getHeader() {
	    return "";
	}

    }

}
