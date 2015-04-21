package com.cron_manager.scheduler;

import java.util.Date;
import java.util.List;

/**
 * Created by honcheng on 2015/4/20.
 */
public interface ScheduleEvent extends Comparable {
    public Date getScheduleTime();
    public List<ScheduleEvent> getNexScheduleEvent();
    public boolean handle() throws Exception;
    public String toString();
}
