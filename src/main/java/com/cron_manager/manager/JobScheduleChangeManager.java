package com.cron_manager.manager;

import com.cron_manager.mapper.JobMapper;
import com.cron_manager.mapper.JobOpenTaskMapper;
import com.cron_manager.model.Job;
import com.cron_manager.model.JobSchedule;
import com.cron_manager.queue.JobScheduleQueue;
import com.cron_manager.queue.model.ScheduleEvent;
import com.cron_manager.scheduler.ScheduleEventJobScheduleEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
        JobSchedule jobSchedule = jobManager.activateInternal(job.getId());
        tryAddSchedule(jobSchedule);
    }

    public void rescheduleJob(Job job, String cronExpression) {
        JobSchedule jobSchedule = jobManager.rescheduleInternal(job.getId(), cronExpression);
        tryAddSchedule(jobSchedule);
    }

    private void tryAddSchedule(JobSchedule jobSchedule) {
        int retry = 3;
        while (retry > 0) {
            try {
                jobScheduleQueue.addScheduleEvent(getScheduleGroup(jobSchedule), new ScheduleEvent(jobSchedule.getId(), ScheduleEventJobScheduleEventType.SCHEDULE, new Date(jobSchedule.getSchedule_datetime().getTime() - 1000)));
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
        jobScheduleQueue.addScheduleEvent(getScheduleGroup(jobSchedule), new ScheduleEvent(jobSchedule.getId(), ScheduleEventJobScheduleEventType.SCHEDULE, new Date(jobSchedule.getSchedule_datetime().getTime() - 1000)));
    }

    private String getScheduleGroup(JobSchedule jobSchedule) throws Exception {
        //TODO - how schedule group is HA?
        List<String> groupList = jobScheduleQueue.getScheduleGroupList();
        if (groupList == null || groupList.size() == 0) {
            throw new Exception("no schedule group available.");
        }

        return groupList.get((int)(jobSchedule.getId() % groupList.size()));
    }
}
