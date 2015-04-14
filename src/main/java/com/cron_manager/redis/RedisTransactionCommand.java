package com.cron_manager.redis;

import redis.clients.jedis.Transaction;

/**
 * Created by honcheng on 2015/4/14.
 */
public interface RedisTransactionCommand {
    public void call(Transaction transaction);
}
