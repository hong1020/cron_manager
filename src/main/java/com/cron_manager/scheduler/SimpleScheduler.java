package com.cron_manager.scheduler;

import com.cron_manager.manager.JobScheduleManager;
import com.cron_manager.model.JobSchedule;
import com.cron_manager.queue.JobExecuteQueue;

import com.cron_manager.queue.JobScheduleQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hongcheng on 4/12/15.
 */
public class SimpleScheduler implements Scheduler,Runnable {
    Logger logger = LoggerFactory.getLogger(SimpleScheduler.class);

    ScheduleTime scheduleTime = new ScheduleTimeQuartz();

    JobScheduleManager jobScheduleManager;
    JobScheduleQueue jobScheduleQueue;

    //mandatory
    String schedulerGroup;

    public SimpleScheduler(String schedulerGroup, JobScheduleManager jobScheduleManager, JobScheduleQueue jobScheduleQueue) {
        this.schedulerGroup = schedulerGroup;
        this.jobScheduleManager = jobScheduleManager;
        this.jobScheduleQueue = jobScheduleQueue;
    }

    @Override
    public void run() {
        //register the schedule group
        jobScheduleQueue.addScheduleGroup(schedulerGroup);

        while (!Thread.currentThread().isInterrupted()) {
            schedule(jobScheduleQueue, schedulerGroup);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }


    @Override
    public void schedule(JobScheduleQueue jobScheduleQueue, String schedulerGroup) {
        int retried = 3;
        while (retried > 0) {
            try {
                JobSchedule jobSchedule = jobScheduleQueue.offerSchedule(schedulerGroup);
                if (jobSchedule != null && isNeedSchedule(jobSchedule)) {
                    logger.info("start schedule for " + jobSchedule.getId());
                    try {
                        JobSchedule nextJobSchedule = jobScheduleManager.createNextSchedule(jobSchedule);
                        if (nextJobSchedule != null) {
                            jobScheduleQueue.addSchedule(schedulerGroup, nextJobSchedule);
                            jobScheduleQueue.moveScheduleToExecute(schedulerGroup, jobSchedule);
                            logger.info("successful schedule for " + jobSchedule.getId());
                        }
                    } catch (Exception e) {
                        logger.info("failed schedule for " + jobSchedule.getId() + " " + e.getMessage());
                        throw e;
                    }
                } else {
                    break;
                }
            } catch (Exception e) {
                retried--;
                logger.info("schedule error of group: " + schedulerGroup + " " + e.getMessage());
            }
        }
    }

    private boolean isNeedSchedule(JobSchedule jobSchedule) {
        return jobSchedule.getStart_datetime().getTime() - System.currentTimeMillis() < 2 * 1000;
    }

}
