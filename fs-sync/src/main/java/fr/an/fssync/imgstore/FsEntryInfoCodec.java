package fr.an.fssync.imgstore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.inotify.Event.CreateEvent.INodeType;

import fr.an.fssync.model.FsEntryInfo;
import lombok.val;

public class FsEntryInfoCodec {
    public static final FsEntryInfoCodec DEFAULT = new FsEntryInfoCodec();

    public FsEntryInfo readBytes(byte[] in) {
	ByteArrayInputStream bin = new ByteArrayInputStream(in);
	DataInputStream din = new DataInputStream(bin);
	try {
	    return read(din);
	} catch (IOException ex) {
	    throw new RuntimeException("should not occur", ex);
	}
    }

    public byte[] writeBytes(FsEntryInfo src) {
	ByteArrayOutputStream bout = new ByteArrayOutputStream(512);
	DataOutputStream dout = new DataOutputStream(bout);
	try {
	    write(dout, src);
	} catch (IOException ex) {
	    throw new RuntimeException("should not occur", ex);
	}
	return bout.toByteArray();
    }

    public FsEntryInfo read(DataInputStream in) throws IOException {
	val res = FsEntryInfo.builder();
	byte typeB = in.readByte();
	INodeType iNodeType = typeB == 0 ? INodeType.FILE : typeB == 1 ? INodeType.DIRECTORY : INodeType.SYMLINK;
	res.iNodeType(iNodeType);
	res.ctime(in.readLong());
	res.mtime(in.readLong());
	res.ownerName(in.readUTF());
	res.groupName(in.readUTF());
	res.perms(new FsPermission(in.readShort()));
	// TODO aclEntries

	if (iNodeType == INodeType.FILE) {
	    res.replication(in.readInt());
	}
	if (iNodeType == INodeType.SYMLINK) {
	    res.symlinkTarget(in.readUTF());
	}
	res.defaultBlockSize(in.readLong());

	if (iNodeType == INodeType.FILE) {
	    res.fileSize(in.readLong());
	}

	return res.build();
    }

    public void write(DataOutputStream out, FsEntryInfo src) throws IOException {
	INodeType iNodeType = src.iNodeType;
	byte type = (byte) iNodeType.ordinal();
	out.writeByte(type);
	out.writeLong(src.ctime);
	out.writeLong(src.mtime);

	out.writeUTF(src.ownerName);
	out.writeUTF(src.groupName);
	out.writeShort(src.perms.toShort());
	// TODO aclEntries

	if (iNodeType == INodeType.FILE) {
	    out.writeInt(src.replication);
	}
	if (iNodeType == INodeType.SYMLINK) {
	    out.writeUTF(src.symlinkTarget);
	}
	out.writeLong(src.defaultBlockSize);

	if (iNodeType == INodeType.FILE) {
	    out.writeLong(src.fileSize);
	}

    }
}