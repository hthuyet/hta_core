/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.htaviet.redis.impl;

import com.htaviet.redis.data.RedisNotificationData;
import org.nustaq.serialization.FSTConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Tuple;
import java.util.*;

/**
 *
 * @author HungNT
 */
public class Subscriber extends JedisPubSub {

    private JedisPool pool;
    private FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    public Subscriber(String key) throws Exception {
        RedisClient rc = RedisFactory.getInstance().getRedisInstance(key);
        if (rc == null) {
            throw new Exception("RedisFactory cannot getRedisInstance key: " + key);
        }
        this.pool = rc.getRedisPool();
    }

    @Override
    public void onMessage(String channel, String message) {
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        Jedis jedis = null;
        RedisNotificationData redisData = new RedisNotificationData(message);
        try {
            jedis = pool.getResource();
            String typeKey = jedis.type(message);
            if (typeKey.equals("string")) {
                try {
                    Object obj = null;
                    byte[] data = jedis.get(message.getBytes());
                    obj = this.conf.asObject(data);
                    redisData.setData(obj);
                } catch (Exception ex) {
                    String value = jedis.get(message);
                    redisData.setData(value);
                }
            } else if (typeKey.equals("hash")) {
                Map<String, String> value = jedis.hgetAll(message);
                redisData.setData(value);
            } else if (typeKey.equals("list")) {
                List<String> value = jedis.lrange(message, 0, -1);
                redisData.setData(value);
            } else if (typeKey.equals("set")) {
                Set<String> value = jedis.smembers(message);
                redisData.setData(value);
            } else if (typeKey.equals("zset")) {
                Set<Tuple> value = jedis.zrangeWithScores(message, 0, -1);
                redisData.setData(value);
            }
        } catch (Exception ex) {
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            onExpireEvent(redisData);
        }
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
    }

    public void onExpireEvent(RedisNotificationData eventData) {
    }
}
