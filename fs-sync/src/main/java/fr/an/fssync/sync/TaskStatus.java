package fr.an.fssync.sync;

public enum TaskStatus {
    ToSchedule, Running, RunningToCancel, Done, // maybe useless? unregister
    // Failed // => maybe useless? ToSchedule
}