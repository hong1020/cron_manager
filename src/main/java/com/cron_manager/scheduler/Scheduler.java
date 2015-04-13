package com.cron_manager.scheduler;

import com.cron_manager.queue.JobQueue;

/**
 * Created by hongcheng on 4/12/15.
 */
public interface Scheduler {
    public void schedule(JobQueue jobQueue, String group);
}
