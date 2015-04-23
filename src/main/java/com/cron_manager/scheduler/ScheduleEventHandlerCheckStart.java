package com.cron_manager.scheduler;

import com.cron_manager.manager.JobScheduleManager;
import com.cron_manager.manager.SpringContextDelegate;
import com.cron_manager.model.JobSchedule;
import com.cron_manager.queue.JobScheduleQueue;
import com.cron_manager.queue.model.ScheduleEvent;
import org.apache.log4j.spi.LoggerFactory;
import org.slf4j.Logger;

import java.util.Date;

/**
 * Created by honcheng on 2015/4/21.
 */
public class ScheduleEventHandlerCheckStart extends ScheduleEventHandler {
    public final static Logger logger = org.slf4j.LoggerFactory.getLogger(ScheduleEventHandlerCheckStart.class);
    public ScheduleEventHandlerCheckStart (String scheduleGroup, ScheduleEvent scheduleEvent) {
        super(scheduleGroup, scheduleEvent);
    }

    @Override
    public boolean handle() throws Exception {
        JobScheduleQueue jobScheduleQueue = SpringContextDelegate.getBean(JobScheduleQueue.class);

        //TODO
//        //check if the schedule is still in queue
//        if (jobScheduleQueue.isSchedulePendingExecute(jobSchedule)) {
//            //check again after
//        }
        jobScheduleQueue.removeScheduleEvent(scheduleGroup, scheduleEvent);
        logger.info("finished check start:" + scheduleEvent.toString());

        return false;
    }
}
