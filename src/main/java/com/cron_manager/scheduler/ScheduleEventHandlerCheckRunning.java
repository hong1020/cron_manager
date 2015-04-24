package com.cron_manager.scheduler;

import com.cron_manager.manager.JobScheduleManager;
import com.cron_manager.manager.SpringContextDelegate;
import com.cron_manager.model.JobSchedule;
import com.cron_manager.queue.JobScheduleQueue;
import com.cron_manager.queue.model.JobScheduleQueueModel;
import com.cron_manager.queue.model.ScheduleEvent;
import com.google.common.collect.Lists;
import org.apache.log4j.spi.LoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by honcheng on 2015/4/21.
 */
public class ScheduleEventHandlerCheckRunning extends ScheduleEventHandler {
    public final static Logger logger = org.slf4j.LoggerFactory.getLogger(ScheduleEventHandlerCheckRunning.class);
    public ScheduleEventHandlerCheckRunning (String scheduleGroup, ScheduleEvent scheduleEvent) {
        super(scheduleGroup, scheduleEvent);
    }

    @Override
    public boolean handle() throws Exception {
        JobScheduleQueue jobScheduleQueue = SpringContextDelegate.getBean(JobScheduleQueue.class);
        JobScheduleManager jobScheduleManager = SpringContextDelegate.getBean(JobScheduleManager.class);

        int executeState = jobScheduleQueue.getJobScheduleState(scheduleEvent.getJobScheduleId());

        if (executeState < 0) {
            //heart beat is lost, take it as dead
            if (jobScheduleManager.getStatus(scheduleEvent.getJobScheduleId()) == JobSchedule.JOB_SCHEDULE_STATUS_RUNNING) {
                jobScheduleManager.updateStatus(scheduleEvent.getJobScheduleId(), JobSchedule.JOB_SCHEDULE_STATUS_FAILED);
                //TODO - retry
            }
            logger.info("take schedule as failed:" + scheduleEvent.getJobScheduleId());
        } else if (executeState == JobSchedule.JOB_SCHEDULE_STATUS_PENDINGEXECUTE) {
            //still in the queue, just check again
            ScheduleEvent checkScheduleRunEvent = new ScheduleEvent(
                    scheduleEvent.getJobScheduleId(),
                    ScheduleEventJobScheduleEventType.CHECK_RUNNING,
                    new Date(System.currentTimeMillis() + JobScheduleQueueModel.getCheckInterval(scheduleEvent)));
            jobScheduleQueue.updateScheduleEvent(scheduleGroup, Lists.newArrayList(scheduleEvent), Lists.newArrayList(checkScheduleRunEvent));
            logger.info("check schedule again after interval:" + scheduleEvent.getJobScheduleId());
        } else if (executeState == JobSchedule.JOB_SCHEDULE_STATUS_SUCCESS ||
                executeState == JobSchedule.JOB_SCHEDULE_STATUS_FAILED) {
            //skip since db should be updated
            jobScheduleQueue.removeScheduleEvent(scheduleGroup, scheduleEvent);
            logger.info("job has finished:" + scheduleEvent.getJobScheduleId());
        }

        logger.info("finished check running:" + scheduleEvent.toString());
        return false;
    }
}
