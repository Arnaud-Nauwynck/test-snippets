package fr.an.fssync.depencymgr;

public enum TaskStatus {
    ToSchedule, Running, RunningToCancel, Done, // maybe useless? unregister
    // Failed // => maybe useless? ToSchedule
}