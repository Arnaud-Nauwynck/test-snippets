package fr.an.tests.resumabledirscan;

public class FileStats {

	int dirCount;
	long dirMinModTime = Long.MAX_VALUE;
	long dirMaxModTime = Long.MIN_VALUE;
	
	int fileCount;
	
	long fileTotalSize;
	
	long fileMinModTime = Long.MAX_VALUE;
	long fileMaxModTime = Long.MIN_VALUE;

	// ------------------------------------------------------------------------
	
	public void addDir(long lastModifTime) {
		this.dirCount++;
		this.dirMinModTime = Math.min(dirMinModTime, lastModifTime);
		this.dirMaxModTime = Math.max(dirMaxModTime, lastModifTime);
	}
	
	public void addFile(long lastModifTime, long len) {
		this.fileCount++;
		this.fileTotalSize += len;
		this.fileMinModTime = Math.min(fileMinModTime, lastModifTime);
		this.fileMaxModTime = Math.max(fileMaxModTime, lastModifTime);
	}

	public void addStats(FileStats src) {
		this.dirCount += src.dirCount;
		this.dirMinModTime = Math.min(dirMinModTime, src.dirMinModTime);
		this.dirMaxModTime = Math.max(dirMaxModTime, src.dirMaxModTime);
		this.fileCount += src.fileCount;
		this.fileTotalSize += src.fileTotalSize;
		this.fileMinModTime = Math.min(fileMinModTime, src.fileMinModTime);
		this.fileMaxModTime = Math.max(fileMaxModTime, src.fileMaxModTime);
	}

}
