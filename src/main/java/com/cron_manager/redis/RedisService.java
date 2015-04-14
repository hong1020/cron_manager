package com.cron_manager.redis;

/**
 * Created by honcheng on 2015/4/14.
 */
public interface RedisService {
    public Object executeCommand(RedisCommand command) throws RedisException;
    public Object executeTransactionCommand(RedisTransactionCommand command) throws RedisException;
}
