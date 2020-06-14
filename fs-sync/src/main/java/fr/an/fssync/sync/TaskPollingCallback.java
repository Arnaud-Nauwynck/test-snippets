package fr.an.fssync.sync;

import java.util.function.Predicate;

public abstract class TaskPollingCallback {

    public abstract ITask pollTask(Predicate<ITask> pred);

    public abstract void taskDone(ITask task);

    public abstract void taskFailed(ITask task);

}
