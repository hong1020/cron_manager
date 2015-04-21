package com.cron_manager.scheduler;

import com.cron_manager.manager.JobScheduleManager;
import com.cron_manager.model.JobSchedule;
import com.cron_manager.queue.JobScheduleQueue;

import java.util.Date;

/**
 * Created by honcheng on 2015/4/21.
 */
public class ScheduleEventJobCheckStart extends AbstractScheduleEvent {

    public ScheduleEventJobCheckStart(
            JobSchedule jobSchedule,
            ScheduleEventJobScheduleEventType eventType,
            Date eventScheduleTime) {
        super(jobSchedule, eventType, eventScheduleTime);
    }

    @Override
    public boolean handle() throws Exception {
        return false;
    }
}
