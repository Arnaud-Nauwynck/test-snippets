package fr.an.tests.resumabledirscan.prev;

import java.util.ArrayList;
import java.util.List;

public class PartialScanDirResultDTO {

	public String dir;
	
	public DirStatDTO stat;
	
	public long scannedTime;
	public int scannedMillis;
	// public int slowestDir;
	
	public List<String> remainSubPaths = new ArrayList<>();

}
