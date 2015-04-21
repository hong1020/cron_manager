package com.cron_manager.client.queue;

import com.cron_manager.model.Job;
import com.cron_manager.model.JobSchedule;

/**
 * Created by hongcheng on 4/12/15.
 */
public interface JobExecuteQueue {
    public JobSchedule offerExecute(String jobGroup);
    public JobSchedule takeExecute(String jobGroup);
}
