package com.cron_manager.queue;

import com.cron_manager.model.JobSchedule;

import java.util.List;

/**
 * Created by honcheng on 2015/4/13.
 */
public interface JobScheduleQueue {
    /**
     * Idempotence. Atomic.
     * @param scheduleGroup
     * @param jobSchedule
     */
    public void addSchedule(String scheduleGroup, JobSchedule jobSchedule) throws Exception;

    public JobSchedule offerSchedule(String scheduleGroup) throws Exception;;
    public JobSchedule takeSchedule(String scheduleGroup) throws Exception;;

    public void addScheduleGroup(String scheduleGroup);
    public List<String> getScheduleGroupList(String scheduleGroup);

    /**
     * Idempotence. Atomic.
     * @param scheduleGroup
     * @param jobSchedule
     */
    public void moveScheduleToExecute(String scheduleGroup, JobSchedule jobSchedule) throws Exception;
}
