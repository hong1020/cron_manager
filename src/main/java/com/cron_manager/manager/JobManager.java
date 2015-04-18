package com.cron_manager.manager;

import com.cron_manager.mapper.JobMapper;
import com.cron_manager.model.Job;
import com.cron_manager.model.JobSchedule;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by honcheng on 2015/4/15.
 */
@Service
public class JobManager {
    @Autowired
    JobMapper jobMapper;

    @Autowired
    JobScheduleManager jobScheduleManager;

    @Transactional(readOnly = true)
    public Job findById(long id) {
        return jobMapper.findById(id);
    }

    @Transactional
    public Job create(Job job) {
        jobMapper.insert(job);
        return job;
    }

    @Transactional
    public Job update(Job job, Date modifiedDate, String modifiedBy) {
        job.setLast_modified_by(modifiedBy);
        job.setLast_modified_date(modifiedDate);
        jobMapper.update(job);
        return job;
    }

    //TODO - reschedule

    @Transactional
    public void delete(long id) {
        deactivate(id);
        jobMapper.delete(id);
    }

    /**
     * This job only handles activating in the database. Job is not scheduled yet in the queue.
     *
     * @param id
     * @return
     */
    @Transactional
    public JobSchedule activateInternal(long id) {
        //TODO - cr is the select necessary?
        Job job = jobMapper.findByIdForUpdate(id);
        job.setStatus(Job.JOB_STATUS_ACTIVE);
        jobMapper.updateStatus(id, job.getStatus());
        JobSchedule schedule = jobScheduleManager.createJobScheduleInternal(job);
        return schedule;
    }

    @Transactional
    public void deactivate(long id) {
        //TODO - will update already lock the record?
        jobMapper.updateStatus(id, Job.JOB_STATUS_INACTIVE);
        Job job = jobMapper.findByIdForUpdate(id);
        long lastScheduleId = job.getLast_schedule_id();
        if (lastScheduleId != 0) {
            jobScheduleManager.updateStatus(lastScheduleId, JobSchedule.JOB_SCHEDULE_STATUS_CANCELLED);
        }
    }

    @Transactional
    public JobSchedule rescheduleInternal(long id, String cronExpression) {
        //TODO - is for update necessary?
        Job job = jobMapper.findByIdForUpdate(id);
        job.setCron_expression(cronExpression);
        jobMapper.updateCronExpression(id, cronExpression);
        long lastScheduleId = job.getLast_schedule_id();
        if (lastScheduleId != 0) {
            jobScheduleManager.updateStatus(lastScheduleId, JobSchedule.JOB_SCHEDULE_STATUS_CANCELLED);
        }
        JobSchedule schedule = jobScheduleManager.createJobScheduleInternal(job);
        return schedule;
    }
}
