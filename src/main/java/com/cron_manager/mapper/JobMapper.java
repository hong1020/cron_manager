package com.cron_manager.mapper;

import com.cron_manager.model.Job;
import org.apache.ibatis.annotations.*;

/**
 * Created by hongcheng on 4/11/15.
 */
public interface JobMapper {
    @Select("select * from job where id = #{id}")
    public Job findById(@Param("id") long id);

    @Select("select * from job where id = #{id} for update")
    public Job findByIdForUpdate(@Param("id") long id);

    @Insert("insert into job (title,description,cron_expression,timezone,timeout," +
            "retry,retry_interval,run_type,fail_strategy, job_group_name, status, run_as, created_date, created_by)" +
            "values(#{title},#{description},#{cron_expression},#{timezone},#{timeout}," +
            "#{retry},#{retry_interval},#{run_type},#{fail_strategy}, #{job_group_name}," +
            "#{status}, #{run_as}, #{created_date}, #{created_by})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public void insert(Job job);

    @Update("update job set title = #{title}, description = #{description}, timeout = #{timeout}, retry = #{retry}," +
            "retry_interval = #{retry_interval}, fail_strategy = #{fail_strategy}, run_as = #{run_as}," +
            "last_modified_by = #{last_modified_by}, last_modified_date = #{last_modified_date} " +
            "where id = #{id}")
    public void update(Job job);

    @Update("update job set cron_expression = #{expression} where id = #{id}")
    public void updateCronExpression(@Param("id")long id, @Param("expression") String expression);

    @Update("update job set last_schedule_id = #{scheduleId} where id = #{id}")
    public void updateSchedule(@Param("id")long id, @Param("scheduleId")long scheduleId);

    @Update("update job set status = #{status} where id = #{id}")
    public void updateStatus(@Param("id")long id, @Param("status") int status);

    @Select("select last_schedule_id from job where id = #{id}")
    public long getLastScheduleId(@Param("id")long id);

    @Select("select status from job where id = #{id}")
    public int getStatus(@Param("id")long id);

    @Delete("delete from job where id = #{id}")
    public void delete(@Param("id")long id);
}
