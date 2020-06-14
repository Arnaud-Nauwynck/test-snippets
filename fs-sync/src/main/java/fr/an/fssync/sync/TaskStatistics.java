package fr.an.fssync.sync;

import lombok.Getter;

public class TaskStatistics {

    @Getter
    private int pendingTasks;
    @Getter
    private int pendingMetadataTasks;
    @Getter
    private long pendingTaskBytes;

    @Getter
    private int runningTasks;
    @Getter
    private int runningMetadataTasks;
    @Getter
    private int runningTaskBytes;

    @Getter
    private long doneTasks;
    @Getter
    private long doneMetadataTasks;
    @Getter
    private long doneTaskBytes;
    @Getter
    private long doneTasksMillis;

    @Getter
    private long failedTaskAttempts;
    @Getter
    private long failedMetadataTasks;
    @Getter
    private long failedTaskBytes;
    @Getter
    private long failedTaskAttemptsMillis;

    void incrPendingTasks(long currTime, boolean isMeta, long bytes) {
        this.pendingTasks++;
        if (isMeta) {
            this.pendingMetadataTasks++;
        } else {
            this.pendingTaskBytes += bytes;
        }
    }

    protected void decrPendingTasks(long submitTime, long currTime, boolean isMeta, long bytes) {
        this.pendingTasks--;
        if (isMeta) {
            this.pendingMetadataTasks--;
        } else {
            this.pendingTaskBytes -= bytes;
        }
    }

    public void decrPendingIncrRunningTasks(long submitTime, long currTime, boolean isMeta, long bytes) {
        decrPendingTasks(submitTime, currTime, isMeta, bytes);
        this.runningTasks++;
        if (isMeta) {
            this.runningMetadataTasks++;
        } else {
            this.runningTaskBytes += bytes;
        }
    }

    protected void decrRunning(boolean isMeta, long bytes) {
        this.runningTasks--;
        if (isMeta) {
            this.runningMetadataTasks--;
        } else {
            this.runningTaskBytes -= bytes;
        }
    }

    public void decrRunningIncrDoneTasks(long millis, long currTime, boolean isMeta, long bytes) {
        decrRunning(isMeta, bytes);
        this.doneTasks++;
        if (isMeta) {
            this.doneMetadataTasks++;
        } else {
            this.doneTaskBytes += bytes;
        }
        this.doneTasksMillis += millis;
    }

    public void decrRunningIncrFailedTaskAttempts(long millis, long currTime, boolean isMeta, long bytes) {
        decrRunning(isMeta, bytes);
        this.failedTaskAttempts++;
        if (isMeta) {
            this.failedMetadataTasks++;
        } else {
            this.failedTaskBytes += bytes;
        }
        this.failedTaskAttemptsMillis += millis;
    }

}
