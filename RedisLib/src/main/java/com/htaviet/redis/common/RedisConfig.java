/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.htaviet.redis.common;

import java.io.FileReader;
import java.util.HashMap;
import com.htaviet.redis.data.RedisObject;
import org.apache.log4j.Logger;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

/**
 * Created by HIEUDT on 5/26/2017.
 */
public class RedisConfig {

    final static Logger logger = Logger.getLogger(RedisConfig.class);
    
    public static HashMap<String, RedisObject> REDIS_MAP_CONFIG = new HashMap<String, RedisObject>();
    
    public static void configure(String configFile) {
        try {
            Ini ini = new Ini(new FileReader(configFile));
            if (ini.values().size() > 0) {
                for (Section section : ini.values()) {
                    String key = section.getName().toUpperCase();
                    logger.debug("----------------------------RedisConfig FOR: " + key);
                    if (section.containsKey(RedisConstants.REDIS_SERVER_KEY)
                            && section.containsKey(RedisConstants.REDIS_PORT_KEY)
                            && section.containsKey(RedisConstants.REDIS_PASSWORD_KEY)) {
                        RedisObject obj = new RedisObject();
                        obj.setRedisServer(section.fetch(RedisConstants.REDIS_SERVER_KEY));
                        obj.setRedisPort(Integer.parseInt(section.fetch(RedisConstants.REDIS_PORT_KEY)));
                        obj.setRedisPasswd(section.fetch(RedisConstants.REDIS_PASSWORD_KEY));
                        obj.setMaxTotal(section.containsKey(RedisConstants.REDIS_MAX_TOTAL_KEY) ? Integer.parseInt(section.fetch(RedisConstants.REDIS_PORT_KEY)) : RedisConstants.REDIS_MAX_TOTAL_DEFAULT);
                        obj.setMinIdle(section.containsKey(RedisConstants.REDIS_MIN_IDLE_KEY) ? Integer.parseInt(section.fetch(RedisConstants.REDIS_MIN_IDLE_KEY)) : RedisConstants.REDIS_MIN_IDLE_DEFAULT);
                        obj.setMaxIdle(section.containsKey(RedisConstants.REDIS_MAX_IDLE_KEY) ? Integer.parseInt(section.fetch(RedisConstants.REDIS_MAX_IDLE_KEY)) : RedisConstants.REDIS_MAX_IDLE_DEFAULT);
                        obj.setMaxWaitTime(section.containsKey(RedisConstants.REDIS_MAX_WAIT_KEY) ? Long.parseLong(section.fetch(RedisConstants.REDIS_MAX_WAIT_KEY)) : RedisConstants.REDIS_MAX_WAIT_DEFAULT);
                        if (section.containsKey(RedisConstants.REDIS_SERVER_NOTIFY_KEY)) {
                            obj.setNotifyServer(section.fetch(RedisConstants.REDIS_SERVER_NOTIFY_KEY));
                        }
                        if (section.containsKey(RedisConstants.REDIS_PORT_NOTIFY_KEY)) {
                            obj.setNotifyPort(Integer.parseInt(section.fetch(RedisConstants.REDIS_PORT_NOTIFY_KEY)));
                        }
                        if (section.containsKey(RedisConstants.REDIS_PASSWORD_NOTIFY_KEY)) {
                            obj.setNotifyPasswd(section.fetch(RedisConstants.REDIS_PASSWORD_NOTIFY_KEY));
                        }
                        REDIS_MAP_CONFIG.put(key, obj);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("ERROR configure: ", ex);
        }
    }
}
