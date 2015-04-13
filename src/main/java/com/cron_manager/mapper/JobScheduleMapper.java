package com.cron_manager.mapper;

import com.cron_manager.model.JobSchedule;
import org.apache.ibatis.annotations.*;

/**
 * Created by hongcheng on 4/11/15.
 */
public interface JobScheduleMapper {
    @Select("select * from job_schedule where id = #{id}")
    public JobSchedule findById(@Param("id") long id);

    @Insert("insert into job_schedule (start_datetime, job_id, job_group_name, run_as) " +
            "values(#{start_datetime}, #{job_id}, #{job_group_name}, #{run_as})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public long insert(JobSchedule jobSchedule);

    @Update("udpate job_schedule set status = #{status} where id = #{id}")
    public void updateStatus(@Param("id") long id, @Param("status")int status);

    @Update("udpate job_schedule set status = " + JobSchedule.JOB_SCHEDULE_STATUS_PENDING + ", retried = retried + 1 where id = #{id}")
    public void retry(@Param("id") long id);
}
