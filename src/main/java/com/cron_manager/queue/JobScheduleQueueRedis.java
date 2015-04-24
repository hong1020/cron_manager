package com.cron_manager.queue;

import com.cron_manager.model.Job;
import com.cron_manager.model.JobSchedule;
import com.cron_manager.queue.model.JobScheduleQueueModel;
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
        return tryLock(JobScheduleQueueModel.getScheduleEventLockKey(event), code, getLockTimeout(event));
    }

    @Override
    public boolean releaseLockScheduleEvent(ScheduleEvent event, String code) throws Exception {
        return releaseLock(JobScheduleQueueModel.getScheduleEventLockKey(event), code);
    }

    public int getExecuteLockTimeout(String key) {return 5;}
    @Override
    public boolean tryLockExecuteJobSchedule(String key, String code) throws Exception {
        return tryLock(JobScheduleQueueModel.getJobScheduleExecuteLockKey(key), code, getExecuteLockTimeout(key));
    }

    @Override
    public boolean releaseLockExecuteJobSchedule(String key, String code) throws Exception {
        return releaseLock(JobScheduleQueueModel.getJobScheduleExecuteLockKey(key), code);
    }

    @Override
    public void removeExecuteJobSchedule(final String jobGroup, final String key) throws Exception {
        RedisTransactionCommand command = new RedisTransactionCommand() {
            @Override
            public void call(Transaction transaction) throws Exception {
                transaction.zrem(JobScheduleQueueModel.getExecuteGroupKey(jobGroup), key);
                transaction.del(key);
            }
        };
        redisService.executeTransactionCommand(command);
    }

    private boolean tryLock(final String key, final String code, final int timeout) throws Exception {
        RedisCommand command = new RedisCommand() {
            @Override
            public Object call(Jedis jedis) throws Exception {
                return jedis.set(key, code, "NX", "EX", timeout);
            }
        };
        if (redisService.executeCommand(command) != null) {
            return true;
        }
        return false;
    }

    private boolean releaseLock(final String key , final String code) throws Exception {
        RedisCommand command = getCommand(key);
        String currentCode = (String)redisService.executeCommand(command);
        if (currentCode.equals(code)) {
            //still very few possibility to del other's lock
            //TODO - check watch perf impact
            redisService.executeCommand(delCommand(key));
            return true;
        }
        return false;
    }


    //TODO
    /*
    public boolean isSchedulePendingExecute(final JobSchedule jobSchedule) throws Exception {
        RedisCommand command = new RedisCommand() {
            @Override
            public Object call(Jedis jedis) throws Exception {
                return jedis.zrank(JobScheduleQueueModel.getExecuteGroupKey(jobSchedule.getJob_group_name()), JobScheduleQueueModel.getScheduleValueKey(jobSchedule.getId()));
            }
        };
        return redisService.executeCommand(command) != null;
    }
    */

    @Override
    public void updateJobScheduleState(final long jobScheduleId, final int state, final int expire) throws Exception {
        RedisCommand command = new RedisCommand() {
            @Override
            public Object call(Jedis jedis) throws Exception {
                return jedis.setex(JobScheduleQueueModel.getJobScheduleExecuteStateKey(jobScheduleId), expire, String.valueOf(state));
            }
        };
        redisService.executeCommand(command);
    }

    @Override
    public int getJobScheduleState(final long joScheduleId) throws Exception {
        String value = (String) redisService.executeCommand(getCommand(JobScheduleQueueModel.getJobScheduleExecuteStateKey(joScheduleId)));
        if (value != null) {
            return Integer.valueOf(value);
        }
        return -1;
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

    @Override
    public void updateScheduleEvent(final String scheduleGroup, final List<ScheduleEvent> oldScheduleEventList, final List<ScheduleEvent> newScheduleEventList) throws Exception {
        RedisTransactionCommand transactionCommand = new RedisTransactionCommand() {
            @Override
            public void call(Transaction transaction) throws Exception {
                for (ScheduleEvent event : oldScheduleEventList) {
                    deleteScheduleEvent(scheduleGroup, transaction, event);
                }
                addEventsToTransaction(scheduleGroup, transaction, newScheduleEventList);
            }
        };
        redisService.executeTransactionCommand(transactionCommand);
    }


    public int getTopScheduleEventRange() {return 5;}

    @Override
    public List<ScheduleEvent> topScheduleEvents(final String scheduleGroup) throws Exception {
        RedisCommand command = new RedisCommand() {
            @Override
            public Object call(Jedis jedis) throws Exception {
                return jedis.zrange(JobScheduleQueueModel.getScheduleGroupKey(scheduleGroup), 0, getTopScheduleEventRange());
            }
        };
        //this set is linked hash set which keeps sequence.
        Set<String> set = (Set<String>) (redisService.executeCommand(command));
        return getJobScheduleEventList(Lists.newArrayList(set));
    }

    public int getTopExecuteRange() {return 10;}
    @Override
    public List<String> topExecuteJobScheduleKeys(final String jobgroup) throws Exception {
        RedisCommand command = new RedisCommand() {
            @Override
            public Object call(Jedis jedis) throws Exception {
                return jedis.zrange(JobScheduleQueueModel.getExecuteGroupKey(jobgroup), 0, getTopExecuteRange());
            }
        };
        Set<String> set = (Set<String>) (redisService.executeCommand(command));
        return Lists.newArrayList(set);
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
                return jedis.sadd(JobScheduleQueueModel.getScheduleGroupSetKey(), scheduleGroup);
            }
        };
        redisService.executeCommand(command);
    }

    @Override
    public List<String> getScheduleGroupList() throws Exception{
        RedisCommand command = new RedisCommand() {
            @Override
            public Object call(Jedis jedis) throws Exception {
                return jedis.smembers(JobScheduleQueueModel.getScheduleGroupSetKey());
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
            transaction.zadd(JobScheduleQueueModel.getScheduleGroupKey(scheduleGroup), score, JobScheduleQueueModel.getScheduleEventKey(e));
            transaction.set(JobScheduleQueueModel.getScheduleEventKey(e), scheduleEventToString(e));
        }
    }

    private void deleteScheduleEvent(String scheduleGroup, Transaction transaction, ScheduleEvent event) {
        transaction.zrem(JobScheduleQueueModel.getScheduleGroupKey(scheduleGroup), JobScheduleQueueModel.getScheduleEventKey(event));
        transaction.del(JobScheduleQueueModel.getScheduleEventKey(event));
    }

    private void executeJobSchedule(Transaction transaction, JobSchedule jobSchedule) throws Exception{
        double timeScore = jobSchedule.getSchedule_datetime().getTime();
        transaction.zadd(JobScheduleQueueModel.getExecuteGroupKey(jobSchedule.getJob_group_name()), timeScore, JobScheduleQueueModel.getScheduleValueKey(jobSchedule.getId()));
        transaction.set(JobScheduleQueueModel.getScheduleValueKey(jobSchedule.getId()), scheduleToString(jobSchedule));
        //set executing state
        transaction.set(JobScheduleQueueModel.getJobScheduleExecuteStateKey(jobSchedule.getId()), String.valueOf(JobSchedule.JOB_SCHEDULE_STATUS_PENDINGEXECUTE));
    }
}
