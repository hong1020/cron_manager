package com.cron_manager.opentask;

import com.cron_manager.manager.JobOpenTaskManager;
import com.cron_manager.manager.JobScheduleChangeManager;
import com.cron_manager.model.JobOpenTask;

/**
 * Created by hongcheng on 4/15/15.
 */
public class JobOpenTaskWorkerCreateSchedule extends AbstractJobOpenTaskWorker {
    JobScheduleChangeManager jobScheduleChangeManager;


    public JobOpenTaskWorkerCreateSchedule(JobOpenTaskManager jobOpenTaskManager, JobOpenTask jobOpenTask, JobScheduleChangeManager jobScheduleChangeManager) {
        super(jobOpenTaskManager, jobOpenTask);
        this.jobScheduleChangeManager = jobScheduleChangeManager;
    }

    @Override
    protected void doTheTask() throws Exception{
        jobScheduleChangeManager.reAddSchedule(jobOpenTask.getReference_id());
    }
}
