package com.cron_manager.scheduler;

import com.cron_manager.jobgroup.JobGroupManager;
import com.cron_manager.manager.JobScheduleManager;
import com.cron_manager.mapper.JobScheduleMapper;
import com.cron_manager.model.JobSchedule;
import com.cron_manager.queue.JobQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;

/**
 * Created by hongcheng on 4/12/15.
 */
public class SimpleScheduler implements Scheduler,Runnable {
    Logger logger = LoggerFactory.getLogger(SimpleScheduler.class);

    ScheduleTime scheduleTime = new ScheduleTimeQuartz();

    @Autowired
    JobScheduleManager jobScheduleManager;

    @Autowired
    JobGroupManager jobGroupManager;

    @Autowired
    JobQueue jobQueue;

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            String jobGroupName = jobGroupManager.takeJobGroupToken();
            if (jobGroupName == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                schedule(jobQueue, jobGroupName);
            }
        }
    }


    @Override
    public void schedule(JobQueue jobQueue, String group) {
        int retried = 3;
        while (retried > 0) {
            try {
                JobSchedule jobSchedule = jobQueue.offerSchedule(group);
                if (isNeedSchedule(jobSchedule)) {
                    logger.info("start schedule for " + jobSchedule.getId());
                    try {
                        JobSchedule nextJobSchedule = jobScheduleManager.createNextSchedule(jobSchedule);

                        jobQueue.addSchedule(group, nextJobSchedule);
                        jobQueue.moveScheduleToExecute(group, jobSchedule);
                        logger.info("successful schedule for " + jobSchedule.getId());
                    } catch (Exception e) {
                        logger.info("failed schedule for " + jobSchedule.getId() + " " + e.getMessage());
                        throw e;
                    }
                } else {
                    break;
                }
            } catch (Exception e) {
                retried--;
                logger.info("schedule error of group: " + group + " " + e.getMessage());
            }
        }
    }

    private boolean isNeedSchedule(JobSchedule jobSchedule) {
        return jobSchedule.getStart_datetime().getTime() - System.currentTimeMillis() < 2 * 1000;
    }

}
