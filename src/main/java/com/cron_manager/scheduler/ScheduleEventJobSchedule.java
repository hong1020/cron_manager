package com.cron_manager.scheduler;

import com.cron_manager.manager.JobScheduleManager;
import com.cron_manager.manager.SpringContextDelegate;
import com.cron_manager.model.JobSchedule;
import com.cron_manager.queue.JobScheduleQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by honcheng on 2015/4/20.
 */
public class ScheduleEventJobSchedule extends AbstractScheduleEvent{
    public static final Logger logger = LoggerFactory.getLogger(ScheduleEventJobSchedule.class);

    public ScheduleEventJobSchedule(
            JobSchedule jobSchedule,
            ScheduleEventJobScheduleEventType eventType,
            Date eventScheduleTime) {
        super(jobSchedule, eventType, eventScheduleTime);
    }

    @Override
    public boolean handle() throws Exception {
        JobScheduleManager jobScheduleManager = SpringContextDelegate.getBean(JobScheduleManager.class);
        JobScheduleQueue jobScheduleQueue = SpringContextDelegate.getBean(JobScheduleQueue.class);

        if (jobScheduleManager.getStatus(jobSchedule.getId()) == JobSchedule.JOB_SCHEDULE_STATUS_PENDING) {
            jobScheduleQueue.moveScheduleToExecute(jobSchedule.getJob_group_name(), jobSchedule);
            logger.info("move to execute queue: " + jobSchedule.getId());

            JobSchedule nextJobSchedule = jobScheduleManager.createNextSchedule(jobSchedule.getId());
            logger.info("add next schedule: " + nextJobSchedule.getId());

            nextScheduleEventList = new ArrayList<ScheduleEvent>();

            //add next schedule event
            ScheduleEvent nextScheduleEvent = new ScheduleEventJobSchedule(
                    nextJobSchedule,
                    ScheduleEventJobScheduleEventType.SCHEDULE,
                    new Date(nextJobSchedule.getSchedule_datetime().getTime() - 1000));
            nextScheduleEventList.add(nextScheduleEvent);

            //add check start run event
            ScheduleEvent checkScheduleStartEvent = new ScheduleEventJobCheckStart(
                    jobSchedule,
                    ScheduleEventJobScheduleEventType.CHECK_START_RUNNING,
                    new Date(System.currentTimeMillis() + getCheckStartInterval()));
            nextScheduleEventList.add(checkScheduleStartEvent);
        }

        return true;
    }

    public long getCheckStartInterval() {
        return 10000;
    }
}
