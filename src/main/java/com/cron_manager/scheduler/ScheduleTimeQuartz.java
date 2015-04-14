package com.cron_manager.scheduler;

import com.cron_manager.model.Job;
import com.cron_manager.model.JobSchedule;
import org.quartz.CronExpression;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by hongcheng on 4/12/15.
 */
public class ScheduleTimeQuartz implements ScheduleTime{
    @Override
    public Timestamp getNextScheduleTime(JobSchedule jobSchedule) {
        Job job = null;
        //TODO
        try {
            CronExpression cronExpression = new CronExpression(job.getCron_expression());
            TimeZone timeZone = TimeZone.getTimeZone(TimeZone.getAvailableIDs(job.getTimezone())[0]);
            cronExpression.setTimeZone(timeZone);

            Date nextDate = cronExpression.getTimeAfter(jobSchedule.getStart_datetime());
            Timestamp nextTimeStamp = new Timestamp(nextDate.getTime());
            return  nextTimeStamp;
        } catch (ParseException e) {
            //
        }
        return null;
    }
}
