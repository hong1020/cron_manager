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
    public void updateScheduleEvent(final String scheduleGroup, final List<ScheduleEvent> oldScheduleEventList, final List<ScheduleEvent> newScheduleEventList) throws Exception;


    public void executeJobSchedule(String scheduleGroup, ScheduleEvent event, JobSchedule jobSchedule, List<ScheduleEvent> nexScheduleEventList) throws Exception;
    public void updateJobScheduleState(final long jobScheduleId, final int state, final int expire) throws Exception;

    public List<String> topExecuteJobScheduleKeys(final String jobGroup) throws Exception;
    public boolean tryLockExecuteJobSchedule(String key, String code) throws Exception;
    public boolean releaseLockExecuteJobSchedule(String key, String code) throws Exception;
    public void removeExecuteJobSchedule(String jobGroup, String key) throws Exception;

    public int getJobScheduleState(long joScheduleId) throws Exception;
}
