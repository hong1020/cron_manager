package com.cron_manager.scheduler;

import com.cron_manager.queue.JobScheduleQueue;

/**
 * Created by hongcheng on 4/12/15.
 */
public interface Scheduler {
    public void schedule(JobScheduleQueue jobQueue, String scheduleGroup);
}
