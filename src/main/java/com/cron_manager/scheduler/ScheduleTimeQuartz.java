package com.cron_manager.scheduler;

import com.cron_manager.model.JobSchedule;

import java.sql.Timestamp;

/**
 * Created by hongcheng on 4/12/15.
 */
public class ScheduleTimeQuartz implements ScheduleTime{
    @Override
    public Timestamp getNextScheduleTime(JobSchedule jobSchedule) {
        return null;
    }
}
