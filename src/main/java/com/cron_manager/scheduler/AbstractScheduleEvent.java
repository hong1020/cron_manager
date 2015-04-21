package com.cron_manager.scheduler;

import com.cron_manager.model.JobSchedule;

import java.util.Date;
import java.util.List;

/**
 * Created by honcheng on 2015/4/21.
 */
public abstract class AbstractScheduleEvent implements ScheduleEvent {
    JobSchedule jobSchedule;
    ScheduleEventJobScheduleEventType eventType;
    Date eventScheduleTime;

    List<ScheduleEvent> nextScheduleEventList = null;

    public AbstractScheduleEvent (JobSchedule jobSchedule, ScheduleEventJobScheduleEventType eventType, Date eventScheduleTime) {
        this.jobSchedule = jobSchedule;
        this.eventType = eventType;
        this.eventScheduleTime = eventScheduleTime;
    }
    @Override
    public Date getScheduleTime() {
        return eventScheduleTime;
    }

    @Override
    public List<ScheduleEvent> getNexScheduleEvent() {
        return nextScheduleEventList;
    }

    @Override
    abstract public boolean handle() throws Exception;

    @Override
    public int compareTo(Object o) {
        ScheduleEvent event = (ScheduleEvent)o;
        return this.getScheduleTime().compareTo(((ScheduleEvent) o).getScheduleTime());
    }

    @Override
    public String toString() {
        return jobSchedule.getId() + "," + getScheduleTime() + "," + eventType;
    }
}
