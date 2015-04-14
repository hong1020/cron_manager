package com.cron_manager.mapper;

import com.cron_manager.model.Job;
import org.apache.ibatis.annotations.*;

/**
 * Created by hongcheng on 4/11/15.
 */
public interface JobMapper {
    @Select("select * from job where id = #{id}")
    public Job findById(@Param("id") long id);

    @Insert("insert into job (title，description，cron_description，timezone，timeout," +
            "retry，retry_interval，run_type，fail_strategy, job_group_id, status, run_as, created_date, created_by)" +
            "values(#{title}，#{description}，#{cron_description}，#{timezone}，#{timeout}," +
            "#{retry}，#{retry_interval}，#{run_type}，#{fail_strategy}, #{job_group_id}," +
            "#{status}, #{run_as}, #{created_date}, #{created_by})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public long insert(Job job);

    @Update("update job set last_schedule_id = #{scheduleId} where id = #{id}")
    public void updateSchedule(@Param("id")long id, @Param("scheduleId")long scheduleId);

    @Update("update job set status = #{status} where id = #{id}")
    public void updateStatus(@Param("id")long id, @Param("status") int status);
}