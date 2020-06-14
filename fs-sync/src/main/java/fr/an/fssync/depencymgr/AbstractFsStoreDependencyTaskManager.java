package fr.an.fssync.depencymgr;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

import fr.an.fssync.depencymgr.AbstractFsStoreDependencyTaskManager.FsDependency.FsDependencyInput;
import fr.an.fssync.depencymgr.AbstractFsStoreDependencyTaskManager.FsDependency.FsDependencyOutput;
import fr.an.fssync.imgstore.FsImageKeyStore;
import fr.an.fssync.model.FsEntry;
import fr.an.fssync.model.FsEntryInfo;
import fr.an.fssync.model.FsEntryInfoChangeListener;
import fr.an.fssync.model.FsPath;
import fr.an.fssync.utils.treefilter.SubPathesTreeMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * Manager of dependency task between dirs of FsImageKeyStore
 */
public class AbstractFsStoreDependencyTaskManager implements Closeable {

    protected class FsStoreEntry {
        // public final FsImageDependencyTaskManager ownerDependencyMgr;
        
        private final FsStoreName fsName;
        
        private final FsImageKeyStore imageStore;

        private final SubPathesTreeMap<FsDependencyOutput> outputDependencyTreeMap = new SubPathesTreeMap<>();

        private final SubPathesTreeMap<FsDependencyInput> inputDependencyTreeMap = new SubPathesTreeMap<>();
        
        private final FsEntryInfoChangeListener listener  = new FsEntryInfoChangeListener() {
            @Override
            public void onChange(FsPath path, FsEntryInfo srcEntryInfo, FsEntryInfo prevSrcEntryInfo) {
                lock.lock();
                try {
                    onFsChange(FsStoreEntry.this, path, srcEntryInfo, prevSrcEntryInfo);
                } finally {
                    lock.unlock();
                }
            }
        };

        // TODO add suspend() / resume() or start() / stop()
        // TODO add memento for listenerSinceTxId ?

        public FsStoreEntry(FsStoreName fsName, FsImageKeyStore imageStore) {
            this.fsName = fsName;
            this.imageStore = imageStore;
        }

        @Override
        public String toString() {
            return "FsStoreEntry[" + fsName + "]";
        }
        
    }


    /**
     * Dependency "formula", to fill an output tree depending of some input trees
     * <PRE>
     *   output = Fx( input1, input2, ... )
     * </PRE>
     * where output is the base filesystem+path
     */
    protected static class FsDependency {
        public final AbstractFsStoreDependencyTaskManager ownerDependencyMgr;
        public final String dependencyFuncName;
        public final Object dependencyFuncData;
        public final FsDependencyOutput output;
        public final FsDependencyInput[] inputs;

        @RequiredArgsConstructor
        public static class FsDependencyOutput {
            public final FsDependency parentDependency;
            public final FsStoreEntry fsStore;
            public final FsPath fsPath;
        }

        @RequiredArgsConstructor
        public static class FsDependencyInput {
            public final FsDependency parentDependency;
            public final FsStoreEntry fsStore;
            public final FsPath fsPath;
            
        }

        // TODO current status, locks ..
        
        public FsDependency(
                AbstractFsStoreDependencyTaskManager ownerDependencyMgr,
                String dependencyFuncName,
                Object dependencyFuncData,
                FsStoreAndPath outputParam,
                List<FsStoreAndPath> inputParams) {
            this.ownerDependencyMgr = ownerDependencyMgr;
            this.dependencyFuncName = dependencyFuncName;
            this.dependencyFuncData = dependencyFuncData;
            // resolve fs + init child inputs / output entries
            FsStoreEntry outputFs = ownerDependencyMgr.fsByName(outputParam.fsName);
            this.output = new FsDependencyOutput(this, outputFs, outputParam.path);
            int inputsLen = inputParams.size();
            this.inputs = new FsDependencyInput[inputsLen];
            for(int i = 0; i < inputsLen; i++) {
                FsStoreAndPath input = inputParams.get(i);
                FsStoreEntry inFs = ownerDependencyMgr.fsByName(input.fsName);
                inputs[i] = new FsDependencyInput(this, inFs, input.path);
            }
        }
    }


    // ------------------------------------------------------------------------
    
    /**
     * 
     */
    private final Map<FsStoreName,FsStoreEntry> fsStores = new HashMap<>();

    private final ReentrantLock lock = new ReentrantLock();

    @Getter
    private final DependencyTaskCallback dependencyTaskCallback = new InnerTaskPollingCallback();

    private final LinkedList<DependencyTask> taskQueue = new LinkedList<>();

    private final List<DependencyTask> runningTasks = new LinkedList<>();

    @Getter
    private final TaskStatistics taskStatistics = new TaskStatistics();

    // ------------------------------------------------------------------------

    public AbstractFsStoreDependencyTaskManager() {
    }

    public void init() {
        for(val e : fsStores.values()) {
            e.imageStore.addListener(e.listener);
        }

        reevalFullScanStoreDiff(); // TOCHECK .. ??
    }

    @Override
    public void close() {
        for(val e : fsStores.values()) {
            e.imageStore.removeListener(e.listener);
        }
    }

    // Manage fileSystems Store by name involved in dependency tasks
    // ------------------------------------------------------------------------

    public void addFsImageStore(FsStoreName name, FsImageKeyStore store) {
        if (null != fsStores.get(name)) {
            throw new IllegalArgumentException();
        }
        FsStoreEntry entry = new FsStoreEntry(name, store);
        fsStores.put(name, entry);
    }

    protected FsStoreEntry fsByName(FsStoreName name) {
        val res = this.fsStores.get(name);
        if (res == null) {
            throw new IllegalArgumentException();
        }
        return res;
    }
    
    protected void onFsChange(
            FsStoreEntry fsStoreEntry,
            FsPath path, FsEntryInfo entryInfo, FsEntryInfo prevEntryInfo) {
        // TODO
    }

    // Manage dependencies 
    // ------------------------------------------------------------------------
        
    public void addDependency(String functionName, Object functionData,
            FsStoreAndPath outputParam,
            List<FsStoreAndPath> inputParams
            ) {
        // create internal entry
        FsDependency fsDependency = new FsDependency(this, functionName, functionData, outputParam, inputParams);
        // register input/output in stores TreeMap
        FsDependencyOutput outputEntry = fsDependency.output;
        outputEntry.fsStore.outputDependencyTreeMap.put(outputEntry.fsPath, outputEntry);
        for(FsDependencyInput inputEntry : fsDependency.inputs) {
            inputEntry.fsStore.inputDependencyTreeMap.put(inputEntry.fsPath, inputEntry);
        }
        // TODO check no cyclic dependencies
        // TODO mark dirty on init..
    }
    
    
    // ------------------------------------------------------------------------
    
    protected void onSrcOrDestChange_UpdateDiffEntryTask(
            FsPath fsPath, FsEntryInfo srcEntryInfo,
            FsEntryInfo prevSrcEntryInfo) {
        DependencyTask task = new DependencyTask(fsPath, srcEntryInfo, destEntryInfo, prevSrcEntryInfo, prevDestEntryInfo);
        DiffEntry diffEntry = registerPathTaskWaitAncestor(fsPath, task);
        // TODO ... lookupDiffEntryIfExist(fsPath)
        DependencyTask prevSyncTask = (diffEntry != null) ? diffEntry.syncTask : null;

        diffEntry.syncTask = task;
        boolean ignoreTask = false;
        boolean waitPreviousTask = true;

        boolean alreadySameEntry = Objects.equals(srcEntryInfo, destEntryInfo); // both null (delete) or equals
                                                                                // (create/update)

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
                    if (prevSyncTask.status != TaskStatus.Running) {
                        // TODO remove previous task
                    }
                } else {
                    // conflicting pending/running task
                    if (prevSyncTask.status == TaskStatus.ToSchedule) {
                        // TODO remove previous conflicting task, as it it not running
                        ignoreTask = true;
                    } else {
                        // => need to forcely add task .. wait after this previous conflicting task
                        prevSyncTask.status = TaskStatus.RunningToCancel;
                    }
                }
            }
        } else {
            // surely something to do .. or maybe a task is already pending/running for it
            if (prevSyncTask == null) {
                // do add task
            } else {
                boolean alreadyCompatiblePrevTask = Objects.equals(srcEntryInfo, prevSyncTask.srcEntryInfo);
                if (alreadyCompatiblePrevTask) {
                    // already a compatible pending/running task for it
                    if (prevSyncTask.status == TaskStatus.RunningToCancel) {
                        prevSyncTask.status = TaskStatus.Running;
                    }
                    ignoreTask = true;
                } else {
                    if (prevSyncTask.status == TaskStatus.Running) {
                        prevSyncTask.status = TaskStatus.RunningToCancel;
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

    // batch checkpoint stores
    // ------------------------------------------------------------------------

    /**
     * Perform full diff between stores (using "sorted join iterators") update
     * corresponding DiffEntries and Task accordingly
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
     * diff between stores (using "sorted join iterators") update corresponding
     * DiffEntries and Task accordingly
     */
    protected void onReevalScanStoreDiff(Iterator<FsEntry> srcStoreIter, Iterator<FsEntry> destStoreIter,
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
                    onSrcOrDestChange_UpdateDiffEntryTask(srcPath, srcEntryInfo, srcEntryInfo, destEntryInfo,
                            destEntryInfo);
                }
                // advance both iter
                srcEntry = (srcStoreIter.hasNext()) ? srcStoreIter.next() : null;
                destEntry = (destStoreIter.hasNext()) ? destStoreIter.next() : null;
            } else if (cmp < 0) {
                // src only => need create on dest
                onSrcOrDestChange_UpdateDiffEntryTask(srcPath, srcEntryInfo, srcEntryInfo, null, null);

                // advance src iter
                srcEntry = (srcStoreIter.hasNext()) ? srcStoreIter.next() : null;
            } else { // if (cmp > 0)
                // dest only => need delete on dest
                onSrcOrDestChange_UpdateDiffEntryTask(srcPath, null, null, destEntryInfo, destEntryInfo);

                // advance dest iter
                destEntry = (destStoreIter.hasNext()) ? destStoreIter.next() : null;
            }

        }

        while (srcEntry != null) {
            // src only => need create on dest
            onSrcOrDestChange_UpdateDiffEntryTask(srcEntry.path, srcEntry.info, srcEntry.info, null, null);

            // advance src iter
            srcEntry = (srcStoreIter.hasNext()) ? srcStoreIter.next() : null;
        }

        while (destEntry != null) {
            // src only => need create on dest
            onSrcOrDestChange_UpdateDiffEntryTask(destEntry.path, null, null, destEntry.info, destEntry.info);

            // advance src iter
            destEntry = (destStoreIter.hasNext()) ? destStoreIter.next() : null;
        }

    }

    // Task
    // ------------------------------------------------------------------------

    protected void addPollableTask(DependencyTask task) {
        taskQueue.add(task);

        // update statistics
        long currTime = System.currentTimeMillis();
        task.submitTime = currTime;
        boolean isMeta = task.isMetadataTask();
        long taskBytes = task.getTaskBytes();
        taskStatistics.incrPendingTasks(currTime, isMeta, taskBytes);
    }

    private class InnerTaskPollingCallback extends DependencyTaskCallback {
        public DependencyTask pollTask(Predicate<DependencyTask> pred) {
            lock.lock();
            try {
                return onPollTask(pred);
            } finally {
                lock.unlock();
            }
        }

        public void taskDone(DependencyTask task) {
            lock.lock();
            try {
                onTaskDone((DependencyTask) task);
            } finally {
                lock.unlock();
            }
        }

        public void taskFailed(DependencyTask task) {
            lock.lock();
            try {
                onTaskFailed((DependencyTask) task);
            } finally {
                lock.unlock();
            }
        }
    };

    protected DependencyTask onPollTask(Predicate<DependencyTask> pred) {
        DependencyTask res = null;
        long currTime = System.currentTimeMillis();
        ListIterator<DependencyTask> iter = taskQueue.listIterator();
        for (; iter.hasNext();) {
            DependencyTask task = iter.next();
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

    protected void onTaskDone(DependencyTask task) {
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

    protected void onTaskFailed(DependencyTask task) {
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

    protected DiffEntry registerPathTaskWaitAncestor(FsPath path, DependencyTask syncTask) {
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

}
