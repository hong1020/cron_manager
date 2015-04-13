package com.cron_manager.model;

import java.sql.Timestamp;

/**
 * Created by hongcheng on 4/11/15.
 */
public class JobSchedule {
    long id;
    Timestamp start_datetime;
    long job_id;
    String job_group_name;
    int status;
    int retried;
    String run_as;

    public static final int JOB_SCHEDULE_STATUS_PENDING = 0;
    public static final int JOB_SCHEDULE_STATUS_RUNNING = 1;
    public static final int JOB_SCHEDULE_STATUS_SUCCESS = 2;
    public static final int JOB_SCHEDULE_STATUS_FAILED = 3;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Timestamp getStart_datetime() {
        return start_datetime;
    }

    public void setStart_datetime(Timestamp start_datetime) {
        this.start_datetime = start_datetime;
    }

    public long getJob_id() {
        return job_id;
    }

    public void setJob_id(long job_id) {
        this.job_id = job_id;
    }

    public String getJob_group_name() {
        return job_group_name;
    }

    public void setJob_group_name(String job_group_name) {
        this.job_group_name = job_group_name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRetried() {
        return retried;
    }

    public void setRetried(int retried) {
        this.retried = retried;
    }

    public String getRun_as() {
        return run_as;
    }

    public void setRun_as(String run_as) {
        this.run_as = run_as;
    }
}
