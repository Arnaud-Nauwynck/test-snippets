package fr.an.hadoop.fs.dirserver.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class FileStatusDTO implements Serializable {

  private static final long serialVersionUID = 1;

  private String path;
  private long len;
  private boolean dir;
  private short repl; // block_replication
  private long block; // blockSize
  private long mtime; // modification_time
  private long atime; // access_time
  private short perm; // => FsPermission
  private String owner;
  private String group;
  private String link; // symlink
  private short attr; // => Set<AttrFlags>
 
  /**
   * Flags for entity attributes.
   */
  public enum AttrDTOFlags {
	  /** ACL information available for this entity. */
	  HAS_ACL(1),
	  /** Entity is encrypted. */
	  HAS_CRYPT(2),
	  /** Entity is stored erasure-coded. */
	  HAS_EC(4),
	  /** Snapshot capability enabled. */
	  SNAPSHOT_ENABLED(8);

	  private final int mask;

	  private AttrDTOFlags(int mask) {
		this.mask = (short) mask;
	  }

	  public int getMask() {
		  return mask;
	  }
	  
  }

}
