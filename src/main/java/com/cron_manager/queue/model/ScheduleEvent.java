package com.cron_manager.queue.model;

import com.cron_manager.model.JobSchedule;
import com.cron_manager.scheduler.ScheduleEventJobScheduleEventType;

import java.util.Date;

/**
 * Created by honcheng on 2015/4/22.
 */
public class ScheduleEvent {
    long jobScheduleId;
    ScheduleEventJobScheduleEventType eventType;
    Date eventScheduleTime;

    public ScheduleEvent() {}

    public ScheduleEvent(long jobScheduleId, ScheduleEventJobScheduleEventType eventType, Date eventScheduleTime) {
        this.jobScheduleId = jobScheduleId;
        this.eventType = eventType;
        this.eventScheduleTime = eventScheduleTime;
    }

    public long getJobScheduleId() {
        return jobScheduleId;
    }

    public void setJobScheduleId(long jobScheduleId) {
        this.jobScheduleId = jobScheduleId;
    }

    public ScheduleEventJobScheduleEventType getEventType() {
        return eventType;
    }

    public void setEventType(ScheduleEventJobScheduleEventType eventType) {
        this.eventType = eventType;
    }

    public Date getEventScheduleTime() {
        return eventScheduleTime;
    }

    public void setEventScheduleTime(Date eventScheduleTime) {
        this.eventScheduleTime = eventScheduleTime;
    }

    @Override
    public String toString() {
        return this.jobScheduleId + "," + this.eventType + "," + this.eventScheduleTime;
    }
}
