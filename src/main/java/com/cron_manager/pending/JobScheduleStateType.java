package com.cron_manager.pending;

/**
 * Created by honcheng on 2015/4/20.
 */
public enum JobScheduleStateType {
    PendingSchedule,
    PendingRun,
    Running,
    Finished,
    Failed,
    TimeOut
}
