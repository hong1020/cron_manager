package com.cron_manager; /**
 * Created by honcheng on 2015/4/16.
 */

import com.cron_manager.model.Job;
import com.cron_manager.scheduler.ScheduleTime;
import org.junit.Test;
import org.junit.Assert;
import com.cron_manager.scheduler.ScheduleTimeQuartz;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ScheduleTimeTest {

    @Test
    public void testTime() throws Exception{
        ScheduleTime scheduleTime = new ScheduleTimeQuartz();

        TimeZone timeZone = TimeZone.getTimeZone("Europe/Berlin");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(timeZone);
        Date startDate = formatter.parse("2015-02-01 13:10:10");

        Job job = new Job();
        job.setCron_expression("0 0/5 14 * * ?");
        job.setTimezone("GMT+8");
        Timestamp timestamp = scheduleTime.getNextScheduleTime(job, startDate);
        Date resultDate = new Date(timestamp.getTime());

        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone(("GMT+08:00")));
        String result = formatter.format(resultDate);

        Assert.assertEquals("time scheduled is incorrect", "2015-02-02 14:00:00", result);
    }
}
