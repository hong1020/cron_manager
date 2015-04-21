package com.cron_manager.client.queue;

import com.cron_manager.model.JobSchedule;
import com.cron_manager.queue.JobScheduleQueueRedis;
import com.cron_manager.redis.RedisCommand;
import com.cron_manager.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * Created by honcheng on 2015/4/20.
 */
@Service
public class JobExecuteQueueRedis implements JobExecuteQueue{
    public static final String EXECUTE_LOCK_KEY = "execute:lock";

    public static String getExecuteLockKey(String id) {
        return EXECUTE_LOCK_KEY + ":" + id;
    }

    @Autowired
    RedisService redisService;

    @Override
    public JobSchedule offerExecute(final String jobGroup) {
        //TODO
        return null;
    }

    @Override
    public JobSchedule takeExecute(String jobGroup) {
        return null;
    }
}
