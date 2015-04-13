package com.cron_manager.jobgroup;

/**
 * Created by hongcheng on 4/12/15.
 */
public interface JobGroupManager {
    public String takeJobGroupToken();
    public void addJobGroupToken(String jobGroupName);
}
