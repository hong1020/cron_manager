package com.cron_manager.queue;

import com.cron_manager.model.Job;
import com.cron_manager.model.JobSchedule;
import com.cron_manager.redis.RedisCommand;
import com.cron_manager.redis.RedisException;
import com.cron_manager.redis.RedisService;
import com.cron_manager.redis.RedisTransactionCommand;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by honcheng on 2015/4/14.
 */
@Service
public class JobScheduleQueueRedis implements  JobScheduleQueue {
    public static final String KEY_SCHEDULE = "schedule";
    public static final String KEY_EXECUTE = "execute";
    public static final String KEY_SCHEDULE_GROUP = "schedule_group";

    public static String getScheduleGroupKey(String group) {
        return KEY_SCHEDULE + ":" + group;
    }

    public static String getExecuteGroupKey(String group) {
        return KEY_EXECUTE + ":" + group;
    }

    public static String getScheduleGroupSetKey() {return KEY_SCHEDULE_GROUP;}

    @Autowired
    RedisService redisService;

    public String scheduleToString(JobSchedule jobSchedule) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(jobSchedule);
    }

    public JobSchedule stringToSchedule(String value) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JobSchedule jobSchedule = objectMapper.readValue(value, JobSchedule.class);
        return jobSchedule;
    }

    @Override
    public void addSchedule(final String scheduleGroup, final JobSchedule jobSchedule) throws Exception{
        final double timeScore = jobSchedule.getSchedule_datetime().getTime();
        final String jobScheduleJson = scheduleToString(jobSchedule);

        RedisCommand command = new RedisCommand() {
            @Override
            public Object call(Jedis jedis) {
                return jedis.zadd(getScheduleGroupKey(scheduleGroup), timeScore, jobScheduleJson) > 0;
            }
        };

        redisService.executeCommand(command);
    }

    @Override
    public JobSchedule offerSchedule(final String scheduleGroup) throws RedisException{
        RedisCommand command = new RedisCommand() {
            @Override
            public Object call(Jedis jedis) throws Exception{
                Set<String> jsonSet = jedis.zrange(getScheduleGroupKey(scheduleGroup), 0, 0);
                if (jsonSet != null && jsonSet.size() > 0) {
                    JobSchedule jobSchedule = stringToSchedule((String) (jsonSet.toArray()[0]));
                    return jobSchedule;
                }
                return null;
            };
        };
        return (JobSchedule) redisService.executeCommand(command);
    }

    //TODO - no take right?
    @Override
    public JobSchedule takeSchedule(String scheduleGroup) {
        return null;
    }

    @Override
    public void addScheduleGroup(final String scheduleGroup) throws Exception{
        RedisCommand command = new RedisCommand() {
            @Override
            public Object call(Jedis jedis) throws Exception {
                return jedis.sadd(getScheduleGroupSetKey(), scheduleGroup);
            }
        };
        redisService.executeCommand(command);
    }

    @Override
    public List<String> getScheduleGroupList() throws Exception{
        RedisCommand command = new RedisCommand() {
            @Override
            public Object call(Jedis jedis) throws Exception {
                return jedis.smembers(getScheduleGroupSetKey());
            }
        };
        Set<String> set = (Set<String>) redisService.executeCommand(command);
        List<String> list = new ArrayList<String>(set);
        Collections.sort(list);
        return list;
    }

    @Override
    public void moveScheduleToExecute(final String scheduleGroup, final JobSchedule jobSchedule) throws Exception {
        final String jobScheduleJson = scheduleToString(jobSchedule);
        RedisTransactionCommand transactionCommand = new RedisTransactionCommand() {
            @Override
            public void call(Transaction transaction) {
                transaction.zrem(getScheduleGroupKey(scheduleGroup), jobScheduleJson);
                transaction.lpush(getExecuteGroupKey(jobSchedule.getJob_group_name()), jobScheduleJson);
            };
        };
        redisService.executeTransactionCommand(transactionCommand);
    }
}
