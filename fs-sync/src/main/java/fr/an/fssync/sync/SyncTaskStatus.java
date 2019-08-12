package fr.an.fssync.sync;

public enum SyncTaskStatus {
    ToSchedule,
    Running,
    RunningToCancel,
    Done, // maybe useless? unregister
    // Failed // => maybe useless? ToSchedule
}