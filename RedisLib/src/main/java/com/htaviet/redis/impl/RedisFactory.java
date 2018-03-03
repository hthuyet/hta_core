/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.htaviet.redis.impl;

import java.util.HashMap;
import com.htaviet.redis.common.RedisConfig;
import com.htaviet.redis.common.RedisConstants;
import com.htaviet.redis.data.RedisObject;
import org.apache.log4j.Logger;

/**
 *
 * @author kiendt hold redisinstances
 */
public class RedisFactory {

    final static Logger logger = Logger.getLogger(RedisFactory.class);
    private static RedisFactory instance;
    private static HashMap<String, RedisClient> MAP_REDIS;

    private RedisFactory() {
        MAP_REDIS = new HashMap<String, RedisClient>();
        RedisConfig.configure(RedisConstants.REDIS_CONFIG_FILE);
    }

    public static synchronized RedisFactory getInstance() {
        if (instance == null) {
            instance = new RedisFactory();
        }
        return instance;
    }

    private void register(String redisName, RedisObject obj) {
        try {
            if (MAP_REDIS.containsKey(redisName)) {
                logger.info("Redis instance name " + redisName + " is resgistered");
                return;
            }
            MAP_REDIS.put(redisName, new RedisClient(obj));
            logger.info("Register instance " + redisName + " success!");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.info("Register instance " + redisName + " failed!");
        }

    }

    public RedisClient getRedisInstance(String redisName) throws Exception {
        if (MAP_REDIS.containsKey(redisName)) {
            RedisClient rtn = MAP_REDIS.get(redisName);
            if (rtn == null) {
                throw new Exception("Cannot getRedisInstance with key: " + redisName);
            }
            return rtn;
        }
        return null;
    }

    public void autoLoadRedisInstance() {
        if (RedisConfig.REDIS_MAP_CONFIG.size() > 0) {
            for (String key : RedisConfig.REDIS_MAP_CONFIG.keySet()) {
                register(key, RedisConfig.REDIS_MAP_CONFIG.get(key));
            }
        }
    }
}
