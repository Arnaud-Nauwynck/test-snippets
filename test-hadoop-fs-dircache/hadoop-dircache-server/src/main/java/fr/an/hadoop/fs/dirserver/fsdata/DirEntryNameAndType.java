package fr.an.hadoop.fs.dirserver.fsdata;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DirEntryNameAndType {
	public final String name;
	public final FsNodeType type;
}