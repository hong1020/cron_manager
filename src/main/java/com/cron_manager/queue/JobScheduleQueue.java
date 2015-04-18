package com.cron_manager.queue;

import com.cron_manager.model.JobSchedule;

import java.util.List;
import java.util.Set;

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
    public JobSchedule takeSchedule(String scheduleGroup) throws Exception;
    public void removeSchedule(String scheduleGroup, JobSchedule jobSchedule) throws Exception;

    public void addScheduleGroup(String scheduleGroup) throws Exception;
    public List<String> getScheduleGroupList() throws Exception;

    /**
     * Idempotence. Atomic.
     * @param scheduleGroup
     * @param jobSchedule
     */
    public void moveScheduleToExecute(String scheduleGroup, JobSchedule jobSchedule) throws Exception;
}
