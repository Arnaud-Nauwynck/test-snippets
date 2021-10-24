package fr.an.hadoop.fs.dirserver.dto;

import lombok.Data;

@Data
public class NotifyCreateDTO {

	private String path;
	private short perm; // => FsPermission permission;
	private boolean overwrite;
	private int bufferSize;
	private short repl; // block_replication
	private long block; // blockSize
	
}
