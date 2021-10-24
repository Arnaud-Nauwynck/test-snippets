package fr.an.hadoop.fs.dirserver.dto;

import lombok.Data;

@Data
public class NotifyDeleteDTO {

	private String path; // => Path;
	private boolean recursive;

}
