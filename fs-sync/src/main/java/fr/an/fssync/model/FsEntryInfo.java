package fr.an.fssync.model;

import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.permission.PermissionStatus;
import org.apache.hadoop.hdfs.inotify.Event.CreateEvent.INodeType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import lombok.val;

public class FsEntryInfo {

    public final INodeType iNodeType;
    public final long ctime;
    public final long mtime;
    // ignored.. public final long atime;
    
    public final String ownerName;
    public final String groupName;
    public final FsPermission perms;
    public final ImmutableList<AclEntry> aclEntries;
    
    public final int replication;
    public final String symlinkTarget;
    public final long defaultBlockSize;
    // public final Optional<Boolean> erasureCoded;

    public final long fileSize;
    
    public final String md5;
    public final int extraDataMarker;

    
    public FsEntryInfo(FsEntryInfoBuilder b) {
	this.iNodeType = b.iNodeType;
	this.ctime = b.ctime;
	this.mtime = b.mtime;
	this.ownerName = b.ownerName;
	this.groupName = b.groupName;
	this.perms = b.perms;
	this.aclEntries = b.aclEntries;
	this.replication = b.replication;
	this.symlinkTarget = b.symlinkTarget;
	this.defaultBlockSize = b.defaultBlockSize;
	this.fileSize = b.fileSize;
	this.md5 = b.md5;
	this.extraDataMarker = b.extraDataMarker;
    }

    public static FsEntryInfoBuilder builder() {
	return new FsEntryInfoBuilder();
    }
    
    public static class FsEntryInfoBuilder {
        public INodeType iNodeType;
        public long ctime;
        public long mtime;
        // ignored.. public long atime;
        
        public String ownerName;
        public String groupName;
        public FsPermission perms;
        public ImmutableList<AclEntry> aclEntries;
        
        public int replication;
        public String symlinkTarget;
        public long defaultBlockSize;
        // public Optional<Boolean> erasureCoded;

        public long fileSize;
        
        public String md5;
        public int extraDataMarker;
        
        public FsEntryInfo build() {
            return new FsEntryInfo(this);
        }
        
	public FsEntryInfoBuilder iNodeType(INodeType iNodeType) {
	    this.iNodeType = iNodeType;
	    switch(iNodeType) {
	    case FILE:
		symlinkTarget = null;
		break;
	    case DIRECTORY:
	        replication = 0;
	        symlinkTarget = null;
	        defaultBlockSize = 0;
		break;
	    case SYMLINK:
	        replication = 0;
	        defaultBlockSize = 0;
		break;
	    }
	    return this;
	}

	public FsEntryInfoBuilder ctime(long ctime) {
	    this.ctime = ctime;
	    return this;
	}
	public FsEntryInfoBuilder mtime(long mtime) {
	    this.mtime = mtime;
	    return this;
	}
	public FsEntryInfoBuilder ownerName(String ownerName) {
	    this.ownerName = ownerName;
	    return this;
	}
	public FsEntryInfoBuilder groupName(String groupName) {
	    this.groupName = groupName;
	    return this;
	}
	public FsEntryInfoBuilder perms(FsPermission perms) {
	    this.perms = perms;
	    return this;
	}
	public FsEntryInfoBuilder aclEntries(ImmutableList<AclEntry> aclEntries) {
	    this.aclEntries = aclEntries;
	    return this;
	}
	public FsEntryInfoBuilder replication(int replication) {
	    this.replication = replication;
	    return this;
	}
	public FsEntryInfoBuilder symlinkTarget(String symlinkTarget) {
	    this.symlinkTarget = symlinkTarget;
	    return this;
	}
	public FsEntryInfoBuilder defaultBlockSize(long defaultBlockSize) {
	    this.defaultBlockSize = defaultBlockSize;
	    return this;
	}
	public FsEntryInfoBuilder fileSize(long fileSize) {
	    this.fileSize = fileSize;
	    return this;
	}
	public FsEntryInfoBuilder md5(String md5) {
	    this.md5 = md5;
	    return this;
	}
	public FsEntryInfoBuilder extraDataMarker(int extraDataMarker) {
	    this.extraDataMarker = extraDataMarker;
	    return this;
	}
        
    }
    
    
    public FsEntryInfoBuilder builderCopy() {
	val b = builder();
	b.iNodeType = iNodeType;
	b.ctime = ctime;
	b.mtime = mtime;
	b.ownerName = ownerName;
	b.groupName = groupName;
	b.perms = perms;
	b.aclEntries = aclEntries;
	b.replication = replication;
	b.symlinkTarget = symlinkTarget;
	b.defaultBlockSize = defaultBlockSize;
	b.fileSize = fileSize;
	b.md5 = md5;
	b.extraDataMarker = extraDataMarker;
	return b;
    }

    public static class FsEntryInfoChgBuilder {
        FsEntryInfoBuilder info;
        FsEntryInfo prevInfo;
        boolean chg = false;
        
        public FsEntryInfoChgBuilder(FsEntryInfo prevInfo) {
            this.prevInfo = prevInfo;
            this.info = prevInfo.builderCopy();
        }

        public boolean isChg() {
            return chg;
        }

	public FsEntryInfo build() {
	    return info.build();
	}

        public void iNodeType(INodeType iNodeType) {
            if (iNodeType != prevInfo.iNodeType) {
        	info.iNodeType(iNodeType);
        	chg = true;
            }
        }

        public void ctime(long ctime) {
            if (ctime != prevInfo.ctime) {
        	info.ctime(ctime);
        	chg = true;
            }
        }

        public void mtime(long mtime) {
            if (mtime != prevInfo.mtime) {
        	info.ctime(mtime);
        	chg = true;
            }
        }

        public void perm(PermissionStatus perm) {
            String ownerName = perm.getUserName();
            if (!Objects.equal(ownerName, prevInfo.ownerName)) {
        	info.ownerName(ownerName);
        	chg = true;
            }
            String groupName = perm.getGroupName();
            if (!Objects.equal(groupName, prevInfo.groupName)) {
        	info.ownerName(groupName);
        	chg = true;
            }
            FsPermission fsperm = perm.getPermission();
            if (!fsperm.equals(prevInfo.perms)) {
        	info.perms(fsperm);
        	chg = true;
            }
        }

        public void aclEntries(ImmutableList<AclEntry> aclEntries) {
            // TODO
        }
        
        public void replication(int replication) {
            if (replication != prevInfo.replication) {
        	info.replication(replication);
        	chg = true;
            }
        }

        public void symlinkTarget(String symlinkTarget) {
            if (!Objects.equal(symlinkTarget, prevInfo.symlinkTarget)) {
        	info.symlinkTarget(symlinkTarget);
        	chg = true;
            }
        }

        public void defaultBlockSize(int defaultBlockSize) {
            if (defaultBlockSize != prevInfo.defaultBlockSize) {
        	info.defaultBlockSize(defaultBlockSize);
        	chg = true;
            }
        }

        public void fileSize(long fileSize) {
            if (fileSize != prevInfo.fileSize) {
        	info.fileSize(fileSize);
        	chg = true;
            }
        }

        public void md5(String md5) {
            if (!Objects.equal(md5, prevInfo.md5)) {
        	info.md5(md5);
        	chg = true;
            }
        }

        public void extraDataMarker(int extraDataMarker) {
            if (extraDataMarker != prevInfo.extraDataMarker) {
        	info.extraDataMarker(extraDataMarker);
        	chg = true;
            }
        }

    }


    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((aclEntries == null) ? 0 : aclEntries.hashCode());
	result = prime * result + (int) (ctime ^ (ctime >>> 32));
	result = prime * result + (int) (defaultBlockSize ^ (defaultBlockSize >>> 32));
	result = prime * result + extraDataMarker;
	result = prime * result + (int) (fileSize ^ (fileSize >>> 32));
	result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
	result = prime * result + ((iNodeType == null) ? 0 : iNodeType.hashCode());
	result = prime * result + ((md5 == null) ? 0 : md5.hashCode());
	result = prime * result + (int) (mtime ^ (mtime >>> 32));
	result = prime * result + ((ownerName == null) ? 0 : ownerName.hashCode());
	result = prime * result + ((perms == null) ? 0 : perms.hashCode());
	result = prime * result + replication;
	result = prime * result + ((symlinkTarget == null) ? 0 : symlinkTarget.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	FsEntryInfo other = (FsEntryInfo) obj;
	if (aclEntries == null) {
	    if (other.aclEntries != null)
		return false;
	} else if (!aclEntries.equals(other.aclEntries))
	    return false;
	if (ctime != other.ctime)
	    return false;
	if (defaultBlockSize != other.defaultBlockSize)
	    return false;
	if (extraDataMarker != other.extraDataMarker)
	    return false;
	if (fileSize != other.fileSize)
	    return false;
	if (groupName == null) {
	    if (other.groupName != null)
		return false;
	} else if (!groupName.equals(other.groupName))
	    return false;
	if (iNodeType != other.iNodeType)
	    return false;
	if (md5 == null) {
	    if (other.md5 != null)
		return false;
	} else if (!md5.equals(other.md5))
	    return false;
	if (mtime != other.mtime)
	    return false;
	if (ownerName == null) {
	    if (other.ownerName != null)
		return false;
	} else if (!ownerName.equals(other.ownerName))
	    return false;
	if (perms == null) {
	    if (other.perms != null)
		return false;
	} else if (!perms.equals(other.perms))
	    return false;
	if (replication != other.replication)
	    return false;
	if (symlinkTarget == null) {
	    if (other.symlinkTarget != null)
		return false;
	} else if (!symlinkTarget.equals(other.symlinkTarget))
	    return false;
	return true;
    }

    public boolean isFile() {
	return iNodeType == INodeType.FILE;
    }

    public char iNodeTypeToChar() {
	switch(iNodeType) {
	    case FILE: return 'f';
	    case DIRECTORY: return 'd';
	    case SYMLINK: return 'l';
	    default: return '?';
	}
    }
    public static INodeType charToiNodeType(char ch) {
	switch(ch) {
	    case 'f': return INodeType.FILE;
	    case 'd': return INodeType.DIRECTORY;
	    case 'l': return INodeType.SYMLINK;
	    default: return null;
	}
    }
    
}
