package com.cron_manager.execute;

import com.cron_manager.execute.JobScheduleStateType;
import com.cron_manager.model.JobSchedule;

/**
 * Created by honcheng on 2015/4/20.
 */
public class JobScheduleState {
    public final static int JOB_SCHEDULE_STATE_PENDINGSCHEDULE = 0;
    public final static int JOB_SCHEDULE_STATE_PENDINGRUN = 1;
    public final static int JOB_SCHEDULE_STATE_RUNNING = 2;
    public final static int JOB_SCHEDULE_STATE_FINISHED = 3;
    public final static int JOB_SCHEDULE_STATE_FAILED = 4;
    public final static int JOB_SCHEDULE_STATE_TIMEOUT = 5;
}
