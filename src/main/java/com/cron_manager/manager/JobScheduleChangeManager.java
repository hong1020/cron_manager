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
    JobOpenTaskMapper jobOpenTaskMapper;

    public void activateJob(Job job) {
        JobSchedule jobSchedule = jobManager.activate(job);

        int retry = 3;
        while (retry > 0) {
            try {
                jobScheduleQueue.addSchedule(getScheduleGroup(jobSchedule), jobSchedule);
                jobOpenTaskManager.deleteTask(jobSchedule.getId());
                break;
            } catch (Exception e) {
                retry --;
            }
        }
    }

    private String getScheduleGroup(JobSchedule jobSchedule) {
        //TODO
        return null;
    }
}
