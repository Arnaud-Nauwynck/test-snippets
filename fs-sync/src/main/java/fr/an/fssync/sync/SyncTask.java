package fr.an.fssync.sync;

import fr.an.fssync.model.FsEntryInfo;
import fr.an.fssync.model.FsPath;

class SyncTask extends LockTask implements StoreSyncTask {
    
    FsEntryInfo srcEntryInfo;
    FsEntryInfo destEntryInfo;
    private FsEntryInfo prevSrcEntryInfo;
    private FsEntryInfo prevDestEntryInfo;
    long submitTime;
    long startTime;
    long failedAttemptsCount;
    long initialSubmit;
    long pollableAfterTime;
    
    public SyncTask(FsPath path, // DiffEntry entry,
    	FsEntryInfo srcEntryInfo, FsEntryInfo destEntryInfo, 
    	FsEntryInfo prevSrcEntryInfo,
    	FsEntryInfo prevDestEntryInfo
    	) {
        super(path);
        this.srcEntryInfo = srcEntryInfo;
        this.destEntryInfo = destEntryInfo;
        this.prevSrcEntryInfo = prevSrcEntryInfo;
        this.prevDestEntryInfo = prevDestEntryInfo;
    }

    @Override
    public FsPath getPath() {
        return path;
    }

    public String getPathString() {
        return path.toUri();
    }

    public boolean isMetadataTask() {
	long srcLen = (srcEntryInfo != null)? srcEntryInfo.fileSize : 0;
	long destLen = (destEntryInfo != null)? destEntryInfo.fileSize : 0;
	return srcLen == destLen;
    }

    public long getTaskBytes() {
	long srcLen = (srcEntryInfo != null)? srcEntryInfo.fileSize : 0;
	long destLen = (destEntryInfo != null)? destEntryInfo.fileSize : 0;
	return (srcLen > destLen)? srcLen - destLen : 0;
    }

    @Override
    public String toString() {
	return "SyncTask [path=" + path + "]";
    }
    
    
}
