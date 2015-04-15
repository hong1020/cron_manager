package com.cron_manager.opentask;

import com.cron_manager.manager.JobOpenTaskManager;
import com.cron_manager.manager.JobScheduleChangeManager;
import com.cron_manager.model.JobOpenTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by honcheng on 2015/4/15.
 */
@Component
public class JobOpenTaskWorkerFactory {
    @Autowired
    JobOpenTaskManager jobOpenTaskManager;
    @Autowired
    JobScheduleChangeManager jobScheduleChangeManager;

    public JobOpenTaskWorker getJobOpenTaskWorker(JobOpenTask jobOpenTask) {
        switch (jobOpenTask.getType()) {
            case JobOpenTask.JOB_OPEN_TASK_TYPE_CREATE_SCHEDULE:
                JobOpenTaskWorker worker = new JobOpenTaskWorkerCreateSchedule(jobOpenTaskManager, jobOpenTask, jobScheduleChangeManager);
                return worker;
            default:
                return null;
        }
    }
}
