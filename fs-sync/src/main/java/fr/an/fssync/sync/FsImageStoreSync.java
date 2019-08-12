package fr.an.fssync.sync;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

import fr.an.fssync.imgstore.FsImageKeyStore;
import fr.an.fssync.model.FsEntry;
import fr.an.fssync.model.FsEntryInfo;
import fr.an.fssync.model.FsEntryInfoChangeListener;
import fr.an.fssync.model.FsPath;
import lombok.Getter;
import lombok.val;

public class FsImageStoreSync implements Closeable {

    private FsImageKeyStore srcStore;
    private FsImageKeyStore destStore;

    private final ReentrantLock lock = new ReentrantLock();

    private FsEntryInfoChangeListener srcStoreListener = new FsEntryInfoChangeListener() {
	@Override
	public void onChange(FsPath path, FsEntryInfo srcEntryInfo, FsEntryInfo prevSrcEntryInfo) {
	    lock.lock();
	    try {
		onSrcChange(path, srcEntryInfo, prevSrcEntryInfo);
	    } finally {
		lock.unlock();
	    }
	}
    };

    private FsEntryInfoChangeListener destStoreListener = new FsEntryInfoChangeListener() {
	@Override
	public void onChange(FsPath path, FsEntryInfo srcEntryInfo, FsEntryInfo prevSrcEntryInfo) {
	    lock.lock();
	    try {
		onDestChange(path, srcEntryInfo, prevSrcEntryInfo);
	    } finally {
		lock.unlock();
	    }
	}
    };

    @Getter
    private final SyncTaskPollingCallback syncTaskPollingCallback = new SyncTaskPollingCallback() {
	public StoreSyncTask pollTask(Predicate<StoreSyncTask> pred) {
	    lock.lock();
	    try {
		return onPollTask(pred);
	    } finally {
		lock.unlock();
	    }
	}

	public void taskDone(StoreSyncTask task) {
	    lock.lock();
	    try {
		onTaskDone((SyncTask) task);
	    } finally {
		lock.unlock();
	    }
	}

	public void taskFailed(StoreSyncTask task) {
	    lock.lock();
	    try {
		onTaskFailed((SyncTask) task);
	    } finally {
		lock.unlock();
	    }
	}
    };

    private DiffEntry rootDiff = new DiffEntry(null, "");

    private final LinkedList<SyncTask> taskQueue = new LinkedList<>();

    private final List<SyncTask> runningTasks = new LinkedList<>();

    @Getter
    private SyncTaskStatistics taskStatistics = new SyncTaskStatistics();

    // ------------------------------------------------------------------------

    public FsImageStoreSync(FsImageKeyStore srcStore, FsImageKeyStore destStore) {
	this.srcStore = srcStore;
	this.destStore = destStore;
	srcStore.addListener(srcStoreListener);
	destStore.addListener(destStoreListener);
    }

    public void close() {
	srcStore.removeListener(srcStoreListener);
	destStore.removeListener(destStoreListener);
    }

    // ------------------------------------------------------------------------

    protected void onSrcChange(FsPath path, 
	    FsEntryInfo srcEntryInfo, FsEntryInfo prevSrcEntryInfo) {
	FsEntryInfo destEntryInfo = destStore.readPathInfo(path);
	FsEntryInfo prevDestEntryInfo = destEntryInfo;
	
	onSrcOrDestChange_UpdateDiffEntryTask(path, 
		    srcEntryInfo, prevSrcEntryInfo,
		    destEntryInfo, prevDestEntryInfo);
    }
    

    protected void onDestChange(FsPath path, FsEntryInfo destEntryInfo, FsEntryInfo prevDestEntryInfo) {
	FsEntryInfo srcEntryInfo = srcStore.readPathInfo(path);
	FsEntryInfo prevSrcEntryInfo = srcEntryInfo;
	
	onSrcOrDestChange_UpdateDiffEntryTask(path, 
		    srcEntryInfo, prevSrcEntryInfo,
		    destEntryInfo, prevDestEntryInfo);
    }

    protected void onSrcOrDestChange_UpdateDiffEntryTask(FsPath fsPath, 
	    FsEntryInfo srcEntryInfo, FsEntryInfo prevSrcEntryInfo,
	    FsEntryInfo destEntryInfo, FsEntryInfo prevDestEntryInfo
	    ) {
	SyncTask task = new SyncTask(fsPath, srcEntryInfo, destEntryInfo, prevSrcEntryInfo, prevDestEntryInfo);
	DiffEntry diffEntry = registerPathTaskWaitAncestor(fsPath, task);
		// TODO ... lookupDiffEntryIfExist(fsPath)
	SyncTask prevSyncTask = (diffEntry != null)? diffEntry.syncTask : null;
	
	diffEntry.syncTask = task;
	boolean ignoreTask = false;
	boolean waitPreviousTask = true;
	
	boolean alreadySameEntry = Objects.equals(srcEntryInfo, destEntryInfo); // both null (delete) or equals (create/update)

	if (alreadySameEntry) {
	    // maybe nothing to do ... but check if task is already pending/running
	    if (prevSyncTask == null) {
		// nothing to do: already same state (and no task pending/running)
		ignoreTask = true;
	    } else {
		boolean alreadyCompatiblePrevTask = Objects.equals(srcEntryInfo, prevSyncTask.srcEntryInfo);
		if (alreadyCompatiblePrevTask) {
		    // nothing to do: can even remove redundant pending task
		    ignoreTask = true;
		    if (prevSyncTask.status != SyncTaskStatus.Running) {
			// TODO remove previous task
		    }
		} else {
		    // conflicting pending/running task
		    if (prevSyncTask.status == SyncTaskStatus.ToSchedule) {
			// TODO remove previous conflicting task, as it it not running
			ignoreTask = true;
		    } else {
			// => need to forcely add task .. wait after this previous conflicting task
			prevSyncTask.status = SyncTaskStatus.RunningToCancel;
		    }
		}
	    }
	} else {
	    // surely something to do ..  or maybe a task is already pending/running for it
	    if (prevSyncTask == null) {
		// do add task
	    } else {
		boolean alreadyCompatiblePrevTask = Objects.equals(srcEntryInfo, prevSyncTask.srcEntryInfo);
		if (alreadyCompatiblePrevTask) {
		    // already a compatible pending/running task for it
		    if (prevSyncTask.status == SyncTaskStatus.RunningToCancel) {
			prevSyncTask.status = SyncTaskStatus.Running;
		    }
		    ignoreTask = true;
		} else {
		    if (prevSyncTask.status == SyncTaskStatus.Running) {
			prevSyncTask.status = SyncTaskStatus.RunningToCancel;
		    }
		    // do add task.. wait after this previous task
		}
	    }
	}
	

	if (!ignoreTask) {
	    if (prevSyncTask != null && waitPreviousTask) {
		task.addWaitTask(prevSyncTask);
	    }
	    addPollableTask(task);
	} else {
	    // TODO task and DiffEntry should not be created 
	    diffEntry.syncTask = null;
	    unregisterEntryFromAncestors(diffEntry);
	}
    }

    // batch checkpoint diff between stores
    // ------------------------------------------------------------------------
    
    /**
     * Perform full diff between stores (using "sorted join iterators")
     * update corresponding DiffEntries and Task accordingly
     */
    public void reevalFullScanStoreDiff() {
	lock.lock();
	try {
	    Iterator<FsEntry> srcStoreIter = srcStore.entryIterator();
	    Iterator<FsEntry> destStoreIter = destStore.entryIterator();
	    onReevalScanStoreDiff(srcStoreIter, destStoreIter, Optional.empty());
	} finally {
	    lock.unlock();
	}
    }

    public void reevalPartialScanStoreDiff(FsPath fromPath, FsPath toPath) {
	lock.lock();
	try {
	    Iterator<FsEntry> srcStoreIter = srcStore.entryIterator(fromPath);
	    Iterator<FsEntry> destStoreIter = destStore.entryIterator(fromPath);
	    onReevalScanStoreDiff(srcStoreIter, destStoreIter, Optional.of(toPath));
	} finally {
	    lock.unlock();
	}
    }

    /**
     * diff between stores (using "sorted join iterators")
     * update corresponding DiffEntries and Task accordingly
     */
    protected void onReevalScanStoreDiff(
	    Iterator<FsEntry> srcStoreIter, Iterator<FsEntry> destStoreIter, 
	    Optional<FsPath> scanEndPath) {
	FsEntry srcEntry = null, destEntry = null;
	if (srcStoreIter.hasNext() && destStoreIter.hasNext()) {
	    srcEntry = srcStoreIter.next();
	    destEntry = destStoreIter.next();
	}
	
	while (srcEntry != null && destEntry != null) {
	    val srcPath = srcEntry.path, destPath = destEntry.path;
	    val srcEntryInfo = srcEntry.info, destEntryInfo = destEntry.info; 

	    int cmp = srcPath.compareTo(destPath);
	    if (cmp == 0) {
		// same path .. update or exactly same entry
		if (srcEntry.info.equals(destEntry.info)) {
		    // exactly same - already in-sync
		} else {
		    // update entry
		    onSrcOrDestChange_UpdateDiffEntryTask(srcPath, 
			    srcEntryInfo, srcEntryInfo, 
			    destEntryInfo, destEntryInfo);
		}
		// advance both iter
		srcEntry = (srcStoreIter.hasNext())? srcStoreIter.next() : null;
		destEntry = (destStoreIter.hasNext())? destStoreIter.next() : null;
	    } else if (cmp < 0) {
		// src only => need create on dest
		onSrcOrDestChange_UpdateDiffEntryTask(srcPath, 
			srcEntryInfo, srcEntryInfo, 
			null, null);
		
		// advance src iter
		srcEntry = (srcStoreIter.hasNext())? srcStoreIter.next() : null;
	    } else { // if (cmp > 0)
		// dest only => need delete on dest
		onSrcOrDestChange_UpdateDiffEntryTask(srcPath, 
			null, null, 
			destEntryInfo, destEntryInfo);
		
		// advance dest iter
		destEntry = (destStoreIter.hasNext())? destStoreIter.next() : null;
	    }
	    
	}
	
	while (srcEntry != null) {
	    // src only => need create on dest
	    onSrcOrDestChange_UpdateDiffEntryTask(srcEntry.path, 
		    srcEntry.info, srcEntry.info, 
		    null, null);

	    // advance src iter
	    srcEntry = (srcStoreIter.hasNext())? srcStoreIter.next() : null;
	} 
	
	while (destEntry != null) {
	    // src only => need create on dest
	    onSrcOrDestChange_UpdateDiffEntryTask(destEntry.path, 
		    null, null, 
		    destEntry.info, destEntry.info);

	    // advance src iter
	    destEntry = (destStoreIter.hasNext())? destStoreIter.next() : null;
	} 
	
    }
    
    // Task
    // ------------------------------------------------------------------------

    protected void addPollableTask(SyncTask task) {
	taskQueue.add(task);

	// update statistics
	long currTime = System.currentTimeMillis();
	task.submitTime = currTime;
	boolean isMeta = task.isMetadataTask();
	long taskBytes = task.getTaskBytes();
	taskStatistics.incrPendingTasks(currTime, isMeta, taskBytes);
    }

    protected SyncTask onPollTask(Predicate<StoreSyncTask> pred) {
	SyncTask res = null;
	long currTime = System.currentTimeMillis();
	ListIterator<SyncTask> iter = taskQueue.listIterator();
	for (; iter.hasNext();) {
	    SyncTask task = iter.next();
	    if (!task.isWaitListEmpty()) {
		continue;
	    }
	    if (task.pollableAfterTime != 0 && task.pollableAfterTime < currTime) {
		continue;
	    }
	    if (pred != null && !pred.test(task)) {
		continue;
	    }
	    // try lock path
	    boolean locked = task.entry.tryWriteLock(task);
	    if (!locked) {
		continue;
	    }
	    // ok, got task
	    res = task;
	    iter.remove();
	    runningTasks.add(res);

	    // update statistics
	    task.startTime = currTime;
	    boolean isMeta = task.isMetadataTask();
	    long taskBytes = task.getTaskBytes();
	    taskStatistics.decrPendingIncrRunningTasks(task.submitTime, currTime, isMeta, taskBytes);
	    break;
	}

	return res;
    }

    protected void onTaskDone(SyncTask task) {
	DiffEntry entry = task.entry;
	runningTasks.remove(task);
	task.removeFromWaitingTasks();
	entry.writeUnlock(task);
	entry.syncTask = null;
	entry.unregisterIfEmpty();

	// update statistics
	long currTime = System.currentTimeMillis();
	long millis = task.startTime - currTime;
	boolean isMeta = task.isMetadataTask();
	long taskBytes = task.getTaskBytes();
	taskStatistics.decrRunningIncrDoneTasks(millis, currTime, isMeta, taskBytes);

    }

    protected void onTaskFailed(SyncTask task) {
	// try re-read image store to check
	FsPath path = task.path;
	FsEntryInfo srcEntryInfo = srcStore.readPathInfo(path);
	task.srcEntryInfo = srcEntryInfo;
	FsEntryInfo destEntryInfo = destStore.readPathInfo(path);
	task.destEntryInfo = destEntryInfo;
	if (Objects.equals(srcEntryInfo, destEntryInfo)) {
	    // failed task, resolved externally!
	    onTaskDone(task);
	    return;
	}

	runningTasks.remove(task);
	task.failedAttemptsCount++;
	task.initialSubmit = task.submitTime;
	task.pollableAfterTime = System.currentTimeMillis() + task.failedAttemptsCount * 1000L;

	// re-submit!
	addPollableTask(task);

	// update statistics
	// submitTime overwriten.. cf initialSubmit
	long currTime = System.currentTimeMillis();
	long millis = task.startTime - currTime;
	boolean isMeta = task.isMetadataTask();
	long taskBytes = task.getTaskBytes();
	taskStatistics.decrRunningIncrFailedTaskAttempts(millis, currTime, isMeta, taskBytes);
    }

    // private
    // ------------------------------------------------------------------------

    protected DiffEntry registerPathTaskWaitAncestor(FsPath path, SyncTask syncTask) {
	DiffEntry entry = rootDiff;
	for (String pathElement : path.elements) {
	    entry = entry.getOrRegisterChild(pathElement);
	    if (entry.syncTask != null) {
		syncTask.addWaitTask(entry.syncTask);
	    }
	}
	syncTask.entry = entry;
	return entry;
    }

    protected void unregisterPathTask(LockTask syncTask) {
	DiffEntry entry = syncTask.entry;
	entry.syncTask = null;
	syncTask.entry = null;
	unregisterEntryFromAncestors(entry);
    }

    protected void unregisterEntryFromAncestors(DiffEntry entry) {
	for (DiffEntry e = entry; e.syncTask == null && (e.childDiff == null || e.childDiff.isEmpty()); e = e.parent) {
	    DiffEntry parent = e.parent;
	    if (parent == null) {
		break;
	    }
	    parent.childDiff.remove(e.name);
	}
    }

    // package protected, for test
    // ------------------------------------------------------------------------

    /*pp*/ List<String> _getDiffEntryPathes() {
	List<String> res = new ArrayList<>();
	recursiveGetDiffEntryPathes(res, "", rootDiff);
	return res;
    }

    private void recursiveGetDiffEntryPathes(List<String> res, String path, DiffEntry e) {
	if (e.childDiff != null) {
	    for(val child : e.childDiff.values()) {
		String childPath = path + "/" + child.name;
		res.add(childPath);
		recursiveGetDiffEntryPathes(res, childPath, child);
	    }
	}
    }

}
