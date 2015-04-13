package com.cron_manager.manager;

import com.cron_manager.mapper.JobMapper;
import com.cron_manager.mapper.JobScheduleMapper;
import com.cron_manager.model.Job;
import com.cron_manager.model.JobSchedule;
import com.cron_manager.scheduler.ScheduleTime;
import com.cron_manager.scheduler.ScheduleTimeQuartz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * Created by hongcheng on 4/12/15.
 */
public class JobScheduleManager {
    ScheduleTime scheduleTime = new ScheduleTimeQuartz();

    @Autowired
    JobScheduleMapper jobScheduleMapper;

    @Autowired
    JobMapper jobMapper;

    public JobSchedule createJobSchedule(Job job) {
        //TODO
        return null;
    }

    /**
     *  Should be idempotence.
     * @param jobSchedule
     * @return
     */
    @Transactional
    public JobSchedule createNextSchedule(JobSchedule jobSchedule) {
        JobSchedule curJobSchedule = jobScheduleMapper.findById(jobSchedule.getId());
        if (curJobSchedule.getNext_job_schedule_id() != 0) {
            //already scheduled
            return jobScheduleMapper.findById(curJobSchedule.getNext_job_schedule_id());
        }

        JobSchedule nextJobSchedule = curJobSchedule;
        nextJobSchedule.setCreated_datetime(new Timestamp(System.currentTimeMillis()));
        nextJobSchedule.setSchedule_datetime(scheduleTime.getNextScheduleTime(jobSchedule));
        nextJobSchedule.setId(jobScheduleMapper.insert(nextJobSchedule));
        jobScheduleMapper.updateNextScheduleId(jobSchedule.getId(), nextJobSchedule.getId());
        jobMapper.updateSchedule(jobSchedule.getJob_id(), jobSchedule.getId());
        return nextJobSchedule;
    }
}
