package com.cron_manager.mapper;

import com.cron_manager.model.JobOpenTask;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * Created by honcheng on 2015/4/15.
 */
public interface JobOpenTaskMapper {

    @Insert("insert into job_open_task (job_id, reference_id, type) values (#{job_id},#{reference_id}, #{type})")
    public void insert(JobOpenTask jobOpenTask);

    @Delete("delete from job_open_task where reference_id = #{referenceId}")
    public void delete(@Param("reference_id")long referenceId);
}
