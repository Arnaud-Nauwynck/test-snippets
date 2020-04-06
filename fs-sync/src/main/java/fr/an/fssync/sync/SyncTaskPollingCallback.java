package fr.an.fssync.sync;

import java.util.function.Predicate;

public abstract class SyncTaskPollingCallback {

    public abstract StoreSyncTask pollTask(Predicate<StoreSyncTask> pred);
    
    public abstract void taskDone(StoreSyncTask task);
    
    public abstract void taskFailed(StoreSyncTask task);

}
