package com.cron_manager.opentask;

import com.cron_manager.manager.JobOpenTaskManager;
import com.cron_manager.manager.JobScheduleManager;
import com.cron_manager.model.JobOpenTask;

/**
 * Created by honcheng on 2015/4/15.
 */
public abstract class AbstractJobOpenTaskWorker implements JobOpenTaskWorker {
    JobOpenTask jobOpenTask;
    JobOpenTaskManager jobOpenTaskManager;

    public AbstractJobOpenTaskWorker(JobOpenTaskManager jobOpenTaskManager, JobOpenTask jobOpenTask) {
        this.jobOpenTaskManager = jobOpenTaskManager;
        this.jobOpenTask = jobOpenTask;
    }

    @Override
    public void doTask() {
        doTheTask();
        jobOpenTaskManager.deleteTask(jobOpenTask.getReference_id());
    }

    abstract protected void doTheTask();
}
