package com.cron_manager.queue;

import com.cron_manager.model.Job;
import com.cron_manager.model.JobSchedule;

/**
 * Created by hongcheng on 4/12/15.
 */
public interface JobQueue {

    public void addSchedulePendingChange(JobSchedule jobSchedule);
    public void removeSchedulePendingChange(JobSchedule jobSchedule);

    /**
     * Idempotence. Atomic.
     * @param group
     * @param jobSchedule
     */
    public void addSchedule(String group, JobSchedule jobSchedule);

    public JobSchedule offerSchedule(String group);
    public JobSchedule takeSchedule(String group);

    /**
     * Idempotence. Atomic.
     * @param group
     * @param jobSchedule
     */
    public void moveScheduleToExecute(String group, JobSchedule jobSchedule);

    public JobSchedule offerExecute(String group);
    public JobSchedule takeExecute(String group);
}
