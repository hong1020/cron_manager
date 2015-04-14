package com.cron_manager.scheduler;

import com.cron_manager.model.Job;
import com.cron_manager.model.JobSchedule;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by hongcheng on 4/12/15.
 */
public interface ScheduleTime {
    public Timestamp getNextScheduleTime(Job job, Date afterDateTime);
}
