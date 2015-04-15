package com.cron_manager.model;

import java.sql.Timestamp;

/**
 * Created by honcheng on 2015/4/15.
 */
public class JobOpenTask {
    long id;
    long job_id;
    long reference_id;
    int type;
    Timestamp created_datetime;

    public final static int JOB_OPEN_TASK_TYPE_CREATE_SCHEDULE = 101;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getJob_id() {
        return job_id;
    }

    public void setJob_id(long job_id) {
        this.job_id = job_id;
    }

    public long getReference_id() {
        return reference_id;
    }

    public void setReference_id(long reference_id) {
        this.reference_id = reference_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Timestamp getCreated_datetime() {
        return created_datetime;
    }

    public void setCreated_datetime(Timestamp created_datetime) {
        this.created_datetime = created_datetime;
    }
}
