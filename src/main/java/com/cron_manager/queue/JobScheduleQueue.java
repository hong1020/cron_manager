package com.cron_manager.queue;

import com.cron_manager.model.JobSchedule;
import com.cron_manager.queue.model.ScheduleEvent;

import java.util.List;
import java.util.Set;

/**
 * Created by honcheng on 2015/4/13.
 */
public interface JobScheduleQueue {
    public void addScheduleGroup(String scheduleGroup) throws Exception;
    public List<String> getScheduleGroupList() throws Exception;

    public JobSchedule getJobSchedule(String key) throws Exception;

    public ScheduleEvent getJobScheduleEvent(String key) throws Exception;
    public List<ScheduleEvent> topScheduleEvents(final String scheduleGroup) throws Exception;
    public List<ScheduleEvent> getJobScheduleEventList(List<String> keyList) throws Exception;

    public void addScheduleEvent(String scheduleGroup, ScheduleEvent scheduleEvent) throws Exception;
    public void removeScheduleEvent(String scheduleGroup, ScheduleEvent scheduleEvent) throws Exception;
    public boolean tryLockScheduleEvent(ScheduleEvent event, String code) throws Exception;
    public boolean releaseLockScheduleEvent(ScheduleEvent event, String code) throws Exception;

    public void executeJobSchedule(String scheduleGroup, ScheduleEvent event, JobSchedule jobSchedule, List<ScheduleEvent> nexScheduleEventList) throws Exception;

    public boolean isSchedulePendingExecute(JobSchedule jobSchedule) throws Exception;
}
