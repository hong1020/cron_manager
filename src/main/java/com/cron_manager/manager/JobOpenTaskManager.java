package com.cron_manager.manager;

import com.cron_manager.mapper.JobOpenTaskMapper;
import com.cron_manager.model.JobOpenTask;
import com.cron_manager.model.JobSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by honcheng on 2015/4/15.
 */
@Service
public class JobOpenTaskManager {
    @Autowired
    JobOpenTaskMapper jobOpenTaskMapper;

    @Transactional
    public void deleteTask(long referenceId) {
        jobOpenTaskMapper.delete(referenceId);
    }

    @Transactional(readOnly = true)
    public List<JobOpenTask> getOpenTasks() {
        return jobOpenTaskMapper.selectOpenTasks();
    }

}
