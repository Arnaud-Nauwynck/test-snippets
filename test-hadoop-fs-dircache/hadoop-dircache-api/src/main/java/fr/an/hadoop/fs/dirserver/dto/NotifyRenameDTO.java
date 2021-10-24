package fr.an.hadoop.fs.dirserver.dto;

import lombok.Data;

@Data
public class NotifyRenameDTO {

	private String src;
	private String dst;

}
