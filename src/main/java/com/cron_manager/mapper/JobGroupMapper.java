package com.cron_manager.mapper;

import com.cron_manager.model.JobGroup;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by hongcheng on 4/14/15.
 */
public interface JobGroupMapper {
    @Select("select * from job_group where id = #{id}")
    public JobGroup findById(@Param("id") long id);
}
