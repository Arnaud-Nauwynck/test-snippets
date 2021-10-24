package fr.an.hadoop.fs.dirserver.dto;

import lombok.Data;

@Data
public class NotifyMkdirsDTO {

	private String path; // => Path;
	private short perm; // => FsPermission permission

}
