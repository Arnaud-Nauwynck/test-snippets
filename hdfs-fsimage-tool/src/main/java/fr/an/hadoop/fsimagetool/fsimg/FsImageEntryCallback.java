package fr.an.hadoop.fsimagetool.fsimg;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.PermissionStatus;
import org.apache.hadoop.hdfs.protocol.proto.HdfsProtos;
import org.apache.hadoop.hdfs.server.namenode.FsImageProto;
import org.apache.hadoop.hdfs.server.namenode.FsImageProto.INodeSection.INode;
import org.apache.hadoop.hdfs.server.namenode.FsImageProto.INodeSection.INodeDirectory;
import org.apache.hadoop.hdfs.server.namenode.FsImageProto.INodeSection.INodeFile;
import org.apache.hadoop.hdfs.server.namenode.FsImageProto.INodeSection.INodeSymlink;
import org.apache.hadoop.hdfs.tools.offlineImageViewer.FsImageDumpTool.EntryCallback;

public class FsImageEntryCallback extends EntryCallback {
		final String delimiter = "\t";
		private static final String DATE_FORMAT="yyyy-MM-dd HH:mm";
		private final SimpleDateFormat dateFormatter =
		      new SimpleDateFormat(DATE_FORMAT);

		@Override
		public void onEntry(String parent, INode inode) {
		    StringBuffer buffer = new StringBuffer();
		    String inodeName = inode.getName().toStringUtf8();
		    Path path = new Path(parent.isEmpty() ? "/" : parent,
		      inodeName.isEmpty() ? "/" : inodeName);
		    append(buffer, path.toString());
		    PermissionStatus p = null;
		    boolean isDir = false;
		    boolean hasAcl = false;

		    switch (inode.getType()) {
		    case FILE:
		      INodeFile file = inode.getFile();
		      // p = getPermission(file.getPermission());
		      hasAcl = file.hasAcl() && file.getAcl().getEntriesCount() > 0;
		      append(buffer, file.getReplication());
		      append(buffer, formatDate(file.getModificationTime()));
		      append(buffer, formatDate(file.getAccessTime()));
		      append(buffer, file.getPreferredBlockSize());
		      append(buffer, file.getBlocksCount());
		      append(buffer, getFileSize(file));
		      append(buffer, 0);  // NS_QUOTA
		      append(buffer, 0);  // DS_QUOTA
		      break;
		    case DIRECTORY:
		      INodeDirectory dir = inode.getDirectory();
		      // p = getPermission(dir.getPermission());
		      hasAcl = dir.hasAcl() && dir.getAcl().getEntriesCount() > 0;
		      append(buffer, 0);  // Replication
		      append(buffer, formatDate(dir.getModificationTime()));
		      append(buffer, formatDate(0));  // Access time.
		      append(buffer, 0);  // Block size.
		      append(buffer, 0);  // Num blocks.
		      append(buffer, 0);  // Num bytes.
		      append(buffer, dir.getNsQuota());
		      append(buffer, dir.getDsQuota());
		      isDir = true;
		      break;
		    case SYMLINK:
		      INodeSymlink s = inode.getSymlink();
		      // p = getPermission(s.getPermission());
		      append(buffer, 0);  // Replication
		      append(buffer, formatDate(s.getModificationTime()));
		      append(buffer, formatDate(s.getAccessTime()));
		      append(buffer, 0);  // Block size.
		      append(buffer, 0);  // Num blocks.
		      append(buffer, 0);  // Num bytes.
		      append(buffer, 0);  // NS_QUOTA
		      append(buffer, 0);  // DS_QUOTA
		      break;
		    default:
		      break;
		    }
		    assert p != null;
		    String dirString = isDir ? "d" : "-";
		    append(buffer, dirString);
		    // append(buffer, p.getPermission().toString());
		    // append(buffer, s.getPermission());
		    		
		    String aclString = hasAcl ? "+" : "";
		    append(buffer, aclString);
		    append(buffer, p.getUserName());
		    append(buffer, p.getGroupName());
		    // buffer.substring(1);
		    
		    System.out.println("** " + buffer);
		}

		private String formatDate(long date) {
		    return dateFormatter.format(new Date(date));
		  }

		private void append(StringBuffer buffer, int field) {
		    buffer.append(delimiter);
		    buffer.append(field);
		  }

		private void append(StringBuffer buffer, long field) {
		    buffer.append(delimiter);
		    buffer.append(field);
		  }

		private void append(StringBuffer buffer, String field) {
		    buffer.append(delimiter);

		    String escapedField = StringEscapeUtils.escapeCsv(field);
//			    if (escapedField.contains(CRLF)) {
//			      escapedField = escapedField.replace(CRLF, "%x0D%x0A");
//			    } else if (escapedField.contains(StringUtils.LF)) {
//			      escapedField = escapedField.replace(StringUtils.LF, "%x0A");
//			    }

		    buffer.append(escapedField);
		  }
//			  protected PermissionStatus getPermission(long perm) {
//				  return FSImageFormatPBINode.Loader.loadPermission(perm, stringTable);
//			  }

		// cf FSImageLoader
		  long getFileSize(FsImageProto.INodeSection.INodeFile f) {
			    long size = 0;
			    for (HdfsProtos.BlockProto p : f.getBlocksList()) {
			      size += p.getNumBytes();
			    }
			    return size;
			  }
	}