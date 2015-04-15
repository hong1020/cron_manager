package com.cron_manager.manager;

import com.cron_manager.mapper.JobMapper;
import com.cron_manager.model.Job;
import com.cron_manager.model.JobSchedule;
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
    public void delete(Job job) {
        jobMapper.delete(job.getId());
    }

    /**
     * This job only handles activating in the database. Job is not scheduled yet in the queue.
     * @param job
     * @return
     */
    @Transactional
    public JobSchedule activate(Job job) {
        jobMapper.updateStatus(job.getId(), Job.JOB_STATUS_ACTIVE);
        JobSchedule schedule = jobScheduleManager.createJobSchedule(job);
        return schedule;
    }

    @Transactional
    public void deactivate(Job job) {
        jobMapper.updateStatus(job.getId(), Job.JOB_STATUS_INACTIVE);
    }
}
