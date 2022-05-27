package fr.an.hadoop.fs.dirserver.fsdata.nio;

import fr.an.hadoop.fs.dirserver.fsdata.FsNodeDataEntryCallback;
import fr.an.hadoop.fs.dirserver.fsdata.NodeFsData;
import fr.an.hadoop.fs.dirserver.fsdata.NodeFsData.FileNodeFsData;
import lombok.Getter;

public class CountFsNodeDataEntryCallback extends FsNodeDataEntryCallback {
	@Getter
	int countFile;
	@Getter
	int countDir;
	
	@Override
	public void handle(String path, NodeFsData nodeData) {
		if (nodeData instanceof FileNodeFsData) {
			countFile++;
		} else {
			countDir++;
		}
	}
	
}