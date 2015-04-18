package com.cron_manager.manager;

import com.cron_manager.mapper.JobMapper;
import com.cron_manager.mapper.JobOpenTaskMapper;
import com.cron_manager.mapper.JobScheduleMapper;
import com.cron_manager.model.Job;
import com.cron_manager.model.JobGroup;
import com.cron_manager.model.JobOpenTask;
import com.cron_manager.model.JobSchedule;
import com.cron_manager.scheduler.ScheduleTime;
import com.cron_manager.scheduler.ScheduleTimeQuartz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by hongcheng on 4/12/15.
 */
@Service
public class JobScheduleManager {
    ScheduleTime scheduleTime = new ScheduleTimeQuartz();

    @Autowired
    JobScheduleMapper jobScheduleMapper;

    @Autowired
    JobMapper jobMapper;

    @Autowired
    JobOpenTaskMapper jobOpenTaskMapper;

    @Transactional(readOnly = true)
    public JobSchedule findById(long id) {
        return jobScheduleMapper.findById(id);
    }

    @Transactional(readOnly = true)
    public int getStatus(long id) {return jobScheduleMapper.getStatus(id);}

    @Transactional
    public void updateStatus(long id, int status) {
        jobScheduleMapper.updateStatus(id, status);
    }

    /**
     * Job should be locked before call this.
     * @param job
     * @return
     */
    @Transactional
    public JobSchedule createJobScheduleInternal(Job job) {
        JobSchedule jobSchedule = doCreateJobSchedule(job, new Date(System.currentTimeMillis()));
        if (jobSchedule != null) {
            jobMapper.updateSchedule(job.getId(), jobSchedule.getId());
        }
        return jobSchedule;
    }

    /**
     *  Should be idempotence.
     * @param scheduleId
     * @return
     */
    @Transactional
    public JobSchedule createNextSchedule(long scheduleId) {
        JobSchedule curJobSchedule = jobScheduleMapper.findById(scheduleId);
        if (curJobSchedule != null && curJobSchedule.getNext_job_schedule_id() != 0) {
            //already scheduled
            return jobScheduleMapper.findById(curJobSchedule.getNext_job_schedule_id());
        }

        Job job = jobMapper.findByIdForUpdate(curJobSchedule.getJob_id());
        if (job != null) {
            JobSchedule nextJobSchedule = doCreateJobSchedule(job, curJobSchedule.getSchedule_datetime());
            if (nextJobSchedule != null) {
                jobScheduleMapper.updateNextScheduleId(curJobSchedule.getId(), nextJobSchedule.getId());
                jobMapper.updateSchedule(curJobSchedule.getJob_id(), nextJobSchedule.getId());
                return nextJobSchedule;
            }
        }

        return null;
    }

    private JobSchedule doCreateJobSchedule(Job job, Date datetime) {
        if (job.getStatus() == Job.JOB_STATUS_INACTIVE) {
            return  null;
        }

        JobSchedule jobSchedule = new JobSchedule();
        jobSchedule.setCreated_datetime(new Timestamp(System.currentTimeMillis()));
        jobSchedule.setSchedule_datetime(scheduleTime.getNextScheduleTime(job, datetime));
        jobSchedule.setJob_id(job.getId());
        jobSchedule.setJob_group_name(job.getJob_group_name());
        jobSchedule.setRun_as(job.getRun_as());
        jobSchedule.setStatus(JobSchedule.JOB_SCHEDULE_STATUS_PENDING);
        jobScheduleMapper.insert(jobSchedule);

        JobOpenTask jobOpenTask = new JobOpenTask();
        jobOpenTask.setJob_id(job.getId());
        jobOpenTask.setReference_id(jobSchedule.getId());
        jobOpenTask.setType(JobOpenTask.JOB_OPEN_TASK_TYPE_CREATE_SCHEDULE);
        jobOpenTask.setCreated_datetime(new Timestamp(System.currentTimeMillis()));
        jobOpenTaskMapper.insert(jobOpenTask);

        return jobSchedule;
    }
}
