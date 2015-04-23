package com.cron_manager.queue;

import com.cron_manager.model.Job;
import com.cron_manager.model.JobSchedule;
import com.cron_manager.queue.model.ScheduleEvent;
import com.cron_manager.redis.RedisCommand;
import com.cron_manager.redis.RedisException;
import com.cron_manager.redis.RedisService;
import com.cron_manager.redis.RedisTransactionCommand;
import com.google.common.collect.Lists;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.util.*;

/**
 * Created by honcheng on 2015/4/14.
 */
@Service
public class JobScheduleQueueRedis implements  JobScheduleQueue {
    public static final String KEY_SCHEDULE = "schedule";
    public static final String KEY_EXECUTE = "execute";
    public static final String KEY_SCHEDULE_GROUP = "schedule_group";
    public static final String KEY_SCHEDULE_VALUE = "schedule_value";
    public static final String KEY_SCHEDULE_EVENT_VALUE = "schedule_event_value";
    public static final String KEY_SCHEDULE_EVENT_LOCK = "schedule_event_lock";

    public static String getScheduleGroupKey(String group) {
        return KEY_SCHEDULE + ":" + group;
    }

    public static String getExecuteGroupKey(String group) {
        return KEY_EXECUTE + ":" + group;
    }

    public static String getScheduleGroupSetKey() {return KEY_SCHEDULE_GROUP;}

    public static String getScheduleValueKey(long id) {return KEY_SCHEDULE_VALUE + ":" + id;}

    public static String getScheduleEventKey(ScheduleEvent event) {
        return KEY_SCHEDULE_EVENT_VALUE + ":" + event.getJobScheduleId() + ":" + event.getEventType();
    }

    public static String getScheduleEventLockKey(ScheduleEvent event) {
        return KEY_SCHEDULE_EVENT_LOCK + ":" + event.getJobScheduleId() + ":" + event.getEventType();
    }

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

    public String scheduleEventToString(ScheduleEvent scheduleEvent) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(scheduleEvent);
    }

    public ScheduleEvent stringToScheduleEvent(String value) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ScheduleEvent scheduleEvent = objectMapper.readValue(value, ScheduleEvent.class);
        return scheduleEvent;
    }

    @Override
    public void addScheduleEvent(final String scheduleGroup, final ScheduleEvent scheduleEvent) throws Exception {
        final List<ScheduleEvent> list = Lists.newArrayList(scheduleEvent);
        RedisTransactionCommand command = new RedisTransactionCommand() {
            @Override
            public void call(Transaction transaction) throws Exception{
                addEventsToTransaction(scheduleGroup, transaction, list);
            }
        };
        redisService.executeTransactionCommand(command);
    }

    @Override
    public void removeScheduleEvent(final String scheduleGroup, final ScheduleEvent scheduleEvent) throws Exception {
        RedisTransactionCommand command = new RedisTransactionCommand() {
            @Override
            public void call(Transaction transaction) throws Exception {
                deleteScheduleEvent(scheduleGroup, transaction, scheduleEvent);
            }
        };
        redisService.executeTransactionCommand(command);
    }

    public int getLockTimeout(ScheduleEvent event) {return 5;}

    @Override
    public boolean tryLockScheduleEvent(final ScheduleEvent event, final String code) throws Exception {
        RedisCommand command = new RedisCommand() {
            @Override
            public Object call(Jedis jedis) throws Exception {
                return jedis.set(getScheduleEventLockKey(event), code, "NX", "EX", getLockTimeout(event));
            }
        };
        if (redisService.executeCommand(command) != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean releaseLockScheduleEvent(ScheduleEvent event, String code) throws Exception {
        RedisCommand command = getCommand(getScheduleEventLockKey(event));
        String currentCode = (String)redisService.executeCommand(command);
        if (currentCode.equals(code)) {
            //still very few possibility to del other's lock
            //TODO - check watch perf impact
            redisService.executeCommand(delCommand(getScheduleEventLockKey(event)));
            return true;
        }
        return false;
    }

    @Override
    public boolean isSchedulePendingExecute(final JobSchedule jobSchedule) throws Exception {
        RedisCommand command = new RedisCommand() {
            @Override
            public Object call(Jedis jedis) throws Exception {
                return jedis.zrank(getExecuteGroupKey(jobSchedule.getJob_group_name()), getScheduleValueKey(jobSchedule.getId()));
            }
        };
        return redisService.executeCommand(command) != null;
    }

    @Override
    public void executeJobSchedule(final String scheduleGroup, final ScheduleEvent event, final JobSchedule jobSchedule, final List<ScheduleEvent> nexScheduleEventList) throws Exception{
        RedisTransactionCommand command = new RedisTransactionCommand() {
            @Override
            public void call(Transaction transaction) throws Exception{
                executeJobSchedule(transaction, jobSchedule);
                deleteScheduleEvent(scheduleGroup, transaction, event);
                addEventsToTransaction(scheduleGroup, transaction, nexScheduleEventList);
            }
        };
        redisService.executeTransactionCommand(command);
    }


    public int getTopRange() {return 5;}

    @Override
    public List<ScheduleEvent> topScheduleEvents(final String scheduleGroup) throws Exception {
        RedisCommand command = new RedisCommand() {
            @Override
            public Object call(Jedis jedis) throws Exception {
                return jedis.zrange(getScheduleGroupKey(scheduleGroup), 0, getTopRange());
            }
        };
        //this set is linked hash set which keeps sequence.
        Set<String> set = (Set<String>) (redisService.executeCommand(command));
        return getJobScheduleEventList(Lists.newArrayList(set));
    }

    @Override
    public ScheduleEvent getJobScheduleEvent(final String key) throws Exception {
        String json = (String)redisService.executeCommand(getCommand(key));
        return stringToScheduleEvent(json);
    }

    @Override
    public List<ScheduleEvent> getJobScheduleEventList(List<String> keyList) throws Exception {
        List<String> valueList = (List<String>)redisService.executeCommand(getListCommand(keyList));
        List<ScheduleEvent> resultList = new ArrayList<ScheduleEvent>();
        for (String value : valueList) {
            resultList.add(stringToScheduleEvent(value));
        }
        return  resultList;
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
        List<String> list = Lists.newArrayList(set);
        Collections.sort(list);
        return list;
    }

    public JobSchedule getJobSchedule(String key) throws Exception {
        String json = (String) redisService.executeCommand(getCommand(key));
        if (json != null) {
            JobSchedule jobSchedule = stringToSchedule(json);
            return jobSchedule;
        }
        return null;
    }

    private RedisCommand delCommand(final String key) {
        RedisCommand command = new RedisCommand() {
            @Override
            public Object call(Jedis jedis) throws Exception {
                return jedis.del(key);
            }
        };
        return command;
    }

    private RedisCommand getCommand(final String key) {
        RedisCommand command = new RedisCommand() {
            @Override
            public Object call(Jedis jedis) throws Exception {
                return jedis.get(key);
            }
        };
        return command;
    }

    private RedisCommand getListCommand(final List<String> keyList) {
        RedisCommand command = new RedisCommand() {
            @Override
            public Object call(Jedis jedis) throws Exception {
                Pipeline pipeline = jedis.pipelined();
                for (String key: keyList) {
                    pipeline.get(key);
                }
                return pipeline.syncAndReturnAll();
            }
        };
        return command;
    }

    private void addEventsToTransaction(String scheduleGroup, Transaction transaction, List<ScheduleEvent> nexScheduleEventList) throws Exception {
        for (ScheduleEvent e : nexScheduleEventList) {
            double score = e.getEventScheduleTime().getTime();
            transaction.zadd(getScheduleGroupKey(scheduleGroup), score, getScheduleEventKey(e));
            transaction.set(getScheduleEventKey(e), scheduleEventToString(e));
        }
    }

    private void deleteScheduleEvent(String scheduleGroup, Transaction transaction, ScheduleEvent event) {
        transaction.zrem(getScheduleGroupKey(scheduleGroup), getScheduleEventKey(event));
        transaction.del(getScheduleEventKey(event));
    }

    private void executeJobSchedule(Transaction transaction, JobSchedule jobSchedule) throws Exception{
        double timeScore = jobSchedule.getSchedule_datetime().getTime();
        transaction.zadd(getExecuteGroupKey(jobSchedule.getJob_group_name()), timeScore, getScheduleValueKey(jobSchedule.getId()));
        transaction.set(getScheduleValueKey(jobSchedule.getId()), scheduleToString(jobSchedule));
    }

}
