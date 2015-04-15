package com.cron_manager.mapper;

import com.cron_manager.model.JobSchedule;
import org.apache.ibatis.annotations.*;

/**
 * Created by hongcheng on 4/11/15.
 */
public interface JobScheduleMapper {
    @Select("select * from job_schedule where id = #{id}")
    public JobSchedule findById(@Param("id") long id);

    @Insert("insert into job_schedule (created_datetime, schedule_datetime, timezone, job_id, job_group_name, run_as) " +
            "values(#{created_datetime}, #{schedule_datetime}, #{timezone}, #{job_id}, #{job_group_name}, #{run_as})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public void insert(JobSchedule jobSchedule);

    @Update("update job_schedule set status = #{status} where id = #{id}")
    public void updateStatus(@Param("id") long id, @Param("status")int status);

    @Update("update job_schedule set status = " + JobSchedule.JOB_SCHEDULE_STATUS_PENDING + ", retried = retried + 1 where id = #{id}")
    public void retry(@Param("id") long id);

    @Update("update job_schedule set next_job_schedule_id = #{nextId} where id = #{thisId}")
    public void updateNextScheduleId(@Param("thisId")long thisId, @Param("nextId")long nextId);
}
