package com.cron_manager.scheduler;

import com.cron_manager.model.JobSchedule;
import com.cron_manager.queue.model.ScheduleEvent;

import java.util.Date;
import java.util.List;

import static com.cron_manager.scheduler.ScheduleEventJobScheduleEventType.*;

/**
 * Created by honcheng on 2015/4/21.
 */
public abstract class ScheduleEventHandler {
    ScheduleEvent scheduleEvent;
    String scheduleGroup;

    public ScheduleEventHandler (String scheduleGroup, ScheduleEvent scheduleEvent) {
        this.scheduleGroup = scheduleGroup;
        this.scheduleEvent = scheduleEvent;
    }

    abstract public boolean handle() throws Exception;

    //TODO
    public static ScheduleEventHandler getScheduleEventHandler(String scheduleGroup, ScheduleEvent scheduleEvent) {
        switch (scheduleEvent.getEventType()) {
            case SCHEDULE:
                return new ScheduleEventHandlerJobSchedule(scheduleGroup, scheduleEvent);
            case CHECK_START_RUNNING:
                return new ScheduleEventHandlerCheckStart(scheduleGroup, scheduleEvent);

        }
        return null;
    }
}
