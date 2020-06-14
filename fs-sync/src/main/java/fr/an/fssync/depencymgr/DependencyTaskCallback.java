package fr.an.fssync.depencymgr;

import java.util.function.Predicate;

public abstract class DependencyTaskCallback {

    public abstract DependencyTask pollTask(Predicate<DependencyTask> pred);

    public abstract void taskDone(DependencyTask task);

    public abstract void taskFailed(DependencyTask task);

}
