package fr.an.hadoop.fs.dirserver.api.converter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileStatus.AttrFlags;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;

import fr.an.hadoop.fs.dirserver.dto.FileStatusDTO;
import fr.an.hadoop.fs.dirserver.dto.FileStatusDTO.AttrDTOFlags;
import lombok.val;

public class HadoopFsDTOConverter {

	public static FileStatusDTO toFileStatusDTO(FileStatus src) {
		FileStatusDTO res = new FileStatusDTO();
		res.setPath(src.getPath().toString());
		res.setLen(src.getLen());
		res.setDir(src.isDirectory());
		res.setRepl(src.getReplication());
		res.setBlock(src.getBlockSize());
		res.setMtime(src.getModificationTime());
		res.setAtime(src.getAccessTime());
		res.setPerm(src.getPermission().toShort());
		res.setOwner(src.getOwner());
		res.setGroup(src.getGroup());
		Path symlink = null;
		if (src.isSymlink()) {
			try {
				symlink = src.getSymlink();
				res.setLink((symlink != null)? symlink.toString() : null);
			} catch (IOException e) {
				// should not occur
			}
		}
		int attr = 0;
		if (src.hasAcl()) {
			attr |= AttrDTOFlags.HAS_ACL.getMask();
		}
		if (src.isEncrypted()) {
			attr |= AttrDTOFlags.HAS_CRYPT.getMask();	    
		}
		if (src.isErasureCoded()) {
			attr |= AttrDTOFlags.HAS_EC.getMask();	    
		}
		if (src.isSnapshotEnabled()) {
			attr |= AttrDTOFlags.SNAPSHOT_ENABLED.getMask();	    
		}
		res.setAttr((short) attr);

		return res;
	}

	public static FileStatus toFileStatus(FileStatusDTO src) {
		long length = src.getLen();
		boolean isdir = src.isDir();
		int block_replication = src.getRepl();
	    long blocksize = src.getBlock();
	    long modification_time = src.getMtime();
	    long access_time = src.getAtime();
	    FsPermission permission = FsPermission.createImmutable(src.getPerm());
	    String owner = src.getOwner();
	    String group = src.getGroup();
	    String link = src.getLink();
		Path symlink = (null != link)? new Path(link) : null;
		Path path = new Path(src.getPath());
		Set<AttrFlags> attr = toFileStatusAttributes(src.getAttr());
		
		return new FileStatus(length, isdir, block_replication,
			      blocksize, modification_time, access_time,
			      permission, owner, group, symlink,
			      path, attr);
	}

	public static Set<AttrFlags> toFileStatusAttributes(short attr) {
		boolean acl = 0 != (attr & AttrDTOFlags.HAS_ACL.getMask());
		boolean crypt = 0 != (attr & AttrDTOFlags.HAS_CRYPT.getMask());
		boolean ec = 0 != (attr & AttrDTOFlags.HAS_EC.getMask());
		boolean sn = 0 != (attr & AttrDTOFlags.SNAPSHOT_ENABLED.getMask());
		return FileStatus.attributes(acl, crypt, ec, sn);
	}

	public static FileStatus[] toFileStatuses(FileStatusDTO[] src) {
		int len = src.length;
		FileStatus[] res = new FileStatus[len];
		for(int i = 0; i < len; i++) {
			res[i] = toFileStatus(src[i]);
		}
		return res;
	}

	public static FileStatusDTO[] toFileStatusesDTO(FileStatus[] src) {
		int len = src.length;
		FileStatusDTO[] res = new FileStatusDTO[len];
		for(int i = 0; i < len; i++) {
			res[i] = toFileStatusDTO(src[i]);
		}
		return res;
	}

	public static FileStatusDTO[] toFileStatusesDTO(List<FileStatus> src) {
		int len = src.size();
		FileStatusDTO[] res = new FileStatusDTO[len];
		int i = 0;
		for(val srcElt: src) {
			res[i] = toFileStatusDTO(srcElt);
			i++;
		}
		return res;
	}

}
