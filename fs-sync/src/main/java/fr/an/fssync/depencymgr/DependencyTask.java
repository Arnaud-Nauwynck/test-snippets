package fr.an.fssync.depencymgr;

import fr.an.fssync.depencymgr.AbstractFsStoreDependencyTaskManager.FsDependency;
import fr.an.fssync.model.FsPath;

class DependencyTask extends LockTask {

    public final FsDependency fsDependency;
    
    long submitTime;
    long startTime;
    long failedAttemptsCount;
    long initialSubmit;
    long pollableAfterTime;

    public DependencyTask(FsDependency fsDependency) {
        super(path);
        this.srcEntryInfo = srcEntryInfo;
        this.destEntryInfo = destEntryInfo;
        this.prevSrcEntryInfo = prevSrcEntryInfo;
        this.prevDestEntryInfo = prevDestEntryInfo;
    }

    public FsPath getPath() {
        return path;
    }

    public String getPathString() {
        return path.toUri();
    }

    public boolean isMetadataTask() {
        long srcLen = (srcEntryInfo != null) ? srcEntryInfo.fileSize : 0;
        long destLen = (destEntryInfo != null) ? destEntryInfo.fileSize : 0;
        return srcLen == destLen;
    }

    public long getTaskBytes() {
        long srcLen = (srcEntryInfo != null) ? srcEntryInfo.fileSize : 0;
        long destLen = (destEntryInfo != null) ? destEntryInfo.fileSize : 0;
        return (srcLen > destLen) ? srcLen - destLen : 0;
    }

    @Override
    public String toString() {
        return "Task [path=" + path + "]";
    }

}
