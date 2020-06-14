package fr.an.fssync.depencymgr;

import java.util.Map;
import java.util.TreeMap;

import fr.an.fssync.model.FsPath;

@Deprecated // TODO
class DiffEntry {

    final DiffEntry parent;
    final String name;
    Map<String, DiffEntry> childDiff; // = new TreeMap<>();

    private int readLockCount;
    private DependencyTask writeLockBy;

    DependencyTask syncTask;

    public DiffEntry(DiffEntry parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    DiffEntry getOrRegisterChild(String name) {
        if (childDiff == null) {
            childDiff = new TreeMap<String, DiffEntry>();
        }
        DiffEntry child = childDiff.get(name);
        if (child == null) {
            child = new DiffEntry(this, name);
            childDiff.put(name, child);
        }
        return child;
    }

    void unregisterIfEmpty() {
        if (syncTask != null || (childDiff != null && !childDiff.isEmpty())) {
            return;
        }
        if (parent != null) {
            parent.childDiff.remove(name);
            parent.unregisterIfEmpty();
        }
    }

    public FsPath getFsPath() {
        int ancestorCount = 0;
        for (DiffEntry p = this; p.parent != null; p = p.parent) {
            ancestorCount++;
        }
        String[] elements = new String[ancestorCount];
        int i = ancestorCount - 1;
        for (DiffEntry p = this; p.parent != null; p = p.parent, i--) {
            elements[i] = p.name;
        }
        return new FsPath(elements);
    }

    public DiffEntry[] ancestors() {
        int ancestorCount = 0;
        for (DiffEntry p = this; p.parent != null; p = p.parent) {
            ancestorCount++;
        }
        DiffEntry[] ancestors = new DiffEntry[ancestorCount];
        int i = ancestorCount - 1;
        for (DiffEntry p = this; p.parent != null; p = p.parent, i--) {
            ancestors[i] = p;
        }
        return ancestors;
    }

    protected boolean tryWriteLock(DependencyTask task) {
//        DiffEntry[] ancestors = ancestors();
//        // step 1: check no write lock on root..entry
//        for(val a : ancestors) {
//            if (a.writeLockBy != null) {
//        	return false;
//            }
//        }
//	// check no read lock on entry
//        final int len = ancestors.length;
//        final int parentPathLen = len - 1;
//        if (ancestors[parentPathLen].readLockCount > 0) {
//            return false;
//        }
//        // step 2: put read lock on root..parent, and write lock on entry
//        for(int i = 0; i < len; i++) {
//            ancestors[i].readLockCount++;
//        }
//        ancestors[parentPathLen].writeLockBy = task;

        // step 1: check
        if (writeLockBy != null || readLockCount > 0) {
            return false;
        }
        // check no write lock on root..entry.parent
        for (DiffEntry p = parent; p.parent != null; p = p.parent) {
            if (p.writeLockBy != null) {
                return false;
            }
        }
        // step 2: put lock
        this.writeLockBy = task;
        for (DiffEntry p = this; p.parent != null; p = p.parent) {
            p.readLockCount++;
        }
        return true;
    }

    protected void writeUnlock(DependencyTask task) {
        assert writeLockBy == task;
        this.writeLockBy = null;
        for (DiffEntry p = this; p.parent != null; p = p.parent) {
            p.readLockCount--;
        }
    }

    @Override
    public String toString() {
        return "DiffEntry [" + getFsPath() + "]";
    }

}