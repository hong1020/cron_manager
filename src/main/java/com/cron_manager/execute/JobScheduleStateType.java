package com.cron_manager.execute;

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
