package fr.an.fssync.sync;

import java.util.ArrayList;
import java.util.List;

import fr.an.fssync.model.FsPath;
import lombok.val;

class LockTask {

    final FsPath path;
    DiffEntry entry;
    SyncTaskStatus status;
    private List<LockTask> waitTasks;
    private List<LockTask> waitedByTasks;

    public LockTask(FsPath path) {
	this.path = path;
    }

    void addWaitTask(LockTask waitTask) {
	if (waitTasks == null) {
	    waitTasks = new ArrayList<>();
	}
	waitTasks.add(waitTask);

	if (waitTask.waitedByTasks == null) {
	    waitTask.waitedByTasks = new ArrayList<>();
	}
	waitTask.waitedByTasks.add(this);
    }

    void removeWaitTask(LockTask waitTask) {
	if (waitTasks != null) {
	    waitTasks.remove(waitTask);
	    if (waitTasks.isEmpty()) {
		waitTasks = null;
	    }
	}

	if (waitTask.waitedByTasks != null) {
	    waitTask.waitedByTasks.remove(this);
	    if (waitTask.waitedByTasks.isEmpty()) {
		waitTask.waitedByTasks = null;
	    }
	}
    }

    void _inv_removeWaitTask(LockTask waitTask) {
	waitTasks.remove(waitTask);
	if (waitTasks.isEmpty()) {
	    waitTasks = null;
	}
    }

    void removeFromWaitingTasks() {
	if (waitedByTasks != null) {
	    for (val waitingTask : waitedByTasks) {
		waitingTask._inv_removeWaitTask(this);
	    }
	    waitedByTasks = null;
	}
    }

    public boolean isWaitListEmpty() {
	return waitTasks == null || waitTasks.isEmpty();
    }

}