package com.cron_manager.manager;

import com.cron_manager.mapper.JobMapper;
import com.cron_manager.mapper.JobOpenTaskMapper;
import com.cron_manager.model.Job;
import com.cron_manager.model.JobSchedule;
import com.cron_manager.queue.JobScheduleQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by honcheng on 2015/4/15.
 */
@Service
public class JobScheduleChangeManager {
    @Autowired
    JobManager jobManager;
    @Autowired
    JobOpenTaskManager jobOpenTaskManager;
    @Autowired
    JobScheduleQueue jobScheduleQueue;
    @Autowired
    JobScheduleManager jobScheduleManager;

    /**
     * This is the right job activate, which also create the schedule.
     * Not in a transaction since queue is also involved.
     * @param job
     */
    public void activateJob(Job job) {
        JobSchedule jobSchedule = jobManager.activate(job);

        int retry = 3;
        while (retry > 0) {
            try {
                jobScheduleQueue.addSchedule(getScheduleGroup(jobSchedule), jobSchedule);
                jobOpenTaskManager.deleteTask(jobSchedule.getId());
                break;
            } catch (Exception e) {
                retry--;
            }
        }
    }

    /**
     * used to re add the schedule in the queue.
     * @param jobScheduleId
     */
    public void reAddSchedule(long jobScheduleId) throws Exception{
        JobSchedule jobSchedule = jobScheduleManager.findById(jobScheduleId);
        jobScheduleQueue.addSchedule(getScheduleGroup(jobSchedule), jobSchedule);
    }

    private String getScheduleGroup(JobSchedule jobSchedule) {
        //TODO
        return null;
    }
}
