package com.cron_manager.opentask;

import com.cron_manager.manager.JobOpenTaskManager;
import com.cron_manager.model.JobOpenTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by honcheng on 2015/4/15.
 */
public class JobOpenTaskRunner implements Runnable {
    public static final Logger logger = LoggerFactory.getLogger(JobOpenTaskRunner.class);

    JobOpenTaskManager jobOpenTaskManager;
    JobOpenTaskWorkerFactory jobOpenTaskWorkerFactory;

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                List<JobOpenTask> openTaskList = jobOpenTaskManager.getOpenTasks();
                for (JobOpenTask jobOpenTask : openTaskList) {
                    if (jobOpenTask.getCreated_datetime().getTime() - System.currentTimeMillis() > -10000) {
                        //less than 10 seconds
                        continue;
                    }

                    JobOpenTaskWorker worker = jobOpenTaskWorkerFactory.getJobOpenTaskWorker(jobOpenTask);
                    try {
                        worker.doTask();
                    } catch (Exception e) {
                        logger.error("open task failed:" + jobOpenTask.getId() + "," + e.getMessage());
                    }
                }
            } catch (Exception e) {
                logger.error("fail to query open task");
            }

            //do it one by one, eat the exception.
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
