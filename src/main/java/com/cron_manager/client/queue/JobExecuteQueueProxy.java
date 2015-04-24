package com.cron_manager.client.queue;

import com.cron_manager.model.JobSchedule;
import com.cron_manager.queue.JobScheduleQueue;
import com.cron_manager.queue.JobScheduleQueueRedis;
import com.cron_manager.queue.model.JobScheduleQueueModel;
import com.cron_manager.redis.RedisCommand;
import com.cron_manager.redis.RedisService;
import com.cron_manager.redis.RedisTransactionCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

/**
 * Created by honcheng on 2015/4/20.
 */
public class JobExecuteQueueProxy{
    JobScheduleQueue jobScheduleQueue;
    String code;

    public JobExecuteQueueProxy(JobScheduleQueue jobScheduleQueue, String code) {
        this.jobScheduleQueue = jobScheduleQueue;
        this.code = code;
    }

    public JobSchedule takeExecute(final String jobGroup) throws Exception{
        List<String> executeJobKeyList = jobScheduleQueue.topExecuteJobScheduleKeys(jobGroup);
        for (String key : executeJobKeyList) {
            if (jobScheduleQueue.tryLockExecuteJobSchedule(key, code)) {
                try {
                    JobSchedule jobSchedule = jobScheduleQueue.getJobSchedule(key);
                    jobScheduleQueue.removeExecuteJobSchedule(jobGroup, key);
                    return jobSchedule;
                } finally {
                    jobScheduleQueue.releaseLockExecuteJobSchedule(key, code);
                }
            }
        }
        return null;
    }

    public void updateJobScheduleState(long jobScheduleId, int state) throws Exception {
        jobScheduleQueue.updateJobScheduleState(jobScheduleId, state, 1000);
    }

    public void refreshJobScheduleRunning(long jobScheduleId)  throws Exception {
        jobScheduleQueue.updateJobScheduleState(jobScheduleId, JobSchedule.JOB_SCHEDULE_STATUS_RUNNING, 30);
    }
}
