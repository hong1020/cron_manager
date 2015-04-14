package com.cron_manager.redis;

import redis.clients.jedis.Jedis;

/**
 * Created by honcheng on 2015/4/14.
 */
public interface RedisCommand {
    public Object call(Jedis jedis) throws Exception;
}
