package com.cron_manager.model;

import java.util.Date;

/**
 * Created by hongcheng on 4/11/15.
 */
public class Job {
    long id;
    String title;
    String description;
    String cron_expression;
    int timezone;
    int timeout;
    int retry;
    int retry_interval;
    int run_type;
    int fail_strategy;
    String job_group_name;
    int status;
    String run_as;
    long last_schedule_id;
    Date created_date;
    Date enable_date;
    String last_modified_by;
    String created_by;

    public static final int JOB_STATUS_INACTIVE = 0;
    public static final int JOB_STATUS_ACTIVE = 1;

    public static final int JOB_RUN_TYPE_ONEHOST = 0;

    public static final int JOB_FAIL_STRATEGY_CONTINUE = 0;
    public static final int JOB_FAIL_STRATEGY_DISABLE = 0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static int getJobFailStrategyDisable() {
        return JOB_FAIL_STRATEGY_DISABLE;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCron_expression() {
        return cron_expression;
    }

    public void setCron_expression(String cron_expression) {
        this.cron_expression = cron_expression;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public int getRetry_interval() {
        return retry_interval;
    }

    public void setRetry_interval(int retry_interval) {
        this.retry_interval = retry_interval;
    }

    public int getRun_type() {
        return run_type;
    }

    public void setRun_type(int run_type) {
        this.run_type = run_type;
    }

    public int getFail_strategy() {
        return fail_strategy;
    }

    public void setFail_strategy(int fail_strategy) {
        this.fail_strategy = fail_strategy;
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

    public String getRun_as() {
        return run_as;
    }

    public void setRun_as(String run_as) {
        this.run_as = run_as;
    }

    public long getLast_schedule_id() {
        return last_schedule_id;
    }

    public void setLast_schedule_id(long last_schedule_id) {
        this.last_schedule_id = last_schedule_id;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public Date getEnable_date() {
        return enable_date;
    }

    public void setEnable_date(Date enable_date) {
        this.enable_date = enable_date;
    }

    public String getLast_modified_by() {
        return last_modified_by;
    }

    public void setLast_modified_by(String last_modified_by) {
        this.last_modified_by = last_modified_by;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }
}
