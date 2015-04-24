package com.cron_manager.scheduler;

import com.cron_manager.manager.JobScheduleManager;
import com.cron_manager.manager.SpringContextDelegate;
import com.cron_manager.model.JobSchedule;
import com.cron_manager.queue.JobScheduleQueue;
import com.cron_manager.queue.model.JobScheduleQueueModel;
import com.cron_manager.queue.model.ScheduleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by honcheng on 2015/4/20.
 */
public class ScheduleEventHandlerJobSchedule extends ScheduleEventHandler{
    public static final Logger logger = LoggerFactory.getLogger(ScheduleEventHandlerJobSchedule.class);

    public ScheduleEventHandlerJobSchedule (String scheduleGroup, ScheduleEvent scheduleEvent) {
        super(scheduleGroup, scheduleEvent);
    }

    @Override
    public boolean handle() throws Exception {
        JobScheduleManager jobScheduleManager = SpringContextDelegate.getBean(JobScheduleManager.class);
        JobScheduleQueue jobScheduleQueue = SpringContextDelegate.getBean(JobScheduleQueue.class);

        if (jobScheduleManager.getStatus(scheduleEvent.getJobScheduleId()) == JobSchedule.JOB_SCHEDULE_STATUS_PENDING) {
            //persistence handling
            JobSchedule jobSchedule = jobScheduleManager.findById(scheduleEvent.getJobScheduleId());
            JobSchedule nextJobSchedule = jobScheduleManager.createNextSchedule(scheduleEvent.getJobScheduleId());
            logger.info("add next schedule: " + nextJobSchedule.getId());

            //queue handling
            List<ScheduleEvent> nextScheduleEventList = new ArrayList<ScheduleEvent>();

            //add next schedule event
            ScheduleEvent nextScheduleEvent = new ScheduleEvent(
                    nextJobSchedule.getId(),
                    ScheduleEventJobScheduleEventType.SCHEDULE,
                    new Date(nextJobSchedule.getSchedule_datetime().getTime() - 1000));
            nextScheduleEventList.add(nextScheduleEvent);

            //add check start run event
            ScheduleEvent checkScheduleRunEvent = new ScheduleEvent(
                    scheduleEvent.getJobScheduleId(),
                    ScheduleEventJobScheduleEventType.CHECK_RUNNING,
                    new Date(System.currentTimeMillis() + JobScheduleQueueModel.getCheckInterval(scheduleEvent)));
            nextScheduleEventList.add(checkScheduleRunEvent);

            jobScheduleQueue.executeJobSchedule(scheduleGroup, scheduleEvent, jobSchedule, nextScheduleEventList);
            logger.info("schedule job schedule: " + scheduleEvent.toString());
        } else {
            //skip scheduling
            jobScheduleQueue.removeScheduleEvent(scheduleGroup,scheduleEvent);
            logger.info("skip job schedule: " + scheduleEvent.toString());
        }

        return true;
    }
}
