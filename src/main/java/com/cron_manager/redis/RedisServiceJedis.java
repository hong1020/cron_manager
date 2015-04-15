package com.cron_manager.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

/**
 * Created by hongcheng on 4/14/15.
 */
@Service
public class RedisServiceJedis implements RedisService {

    @Autowired
    JedisPool jedisPool;

    @Override
    public Object executeCommand(RedisCommand command) throws RedisException {
        Jedis jedis = jedisPool.getResource();
        try {
            return command.call(jedis);
        } catch (Exception e) {
            if (jedis != null) {
                jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
            throw new RedisException();
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
                jedis = null;
            }
        }
    }

    @Override
    public Object executeTransactionCommand(RedisTransactionCommand command) throws RedisException {
        Jedis jedis = jedisPool.getResource();
        Transaction transaction = jedis.multi();
        try {
            command.call(transaction);
            return transaction.exec();
        } catch (Exception e) {
            if (jedis != null) {
                jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
            throw new RedisException();
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
                jedis = null;
            }
        }
    }
}
