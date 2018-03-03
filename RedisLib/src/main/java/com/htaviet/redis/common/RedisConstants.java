/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.htaviet.redis.common;

/**
 * Created by HIEUDT on 5/26/2017.
 */
public interface RedisConstants {

    public static final String REDIS_CONFIG_FILE = "../etc/redis.ini";

    public static final String REDIS_SERVER_KEY = "REDIS_SERVER";
    public static final String REDIS_PORT_KEY = "REDIS_PORT";
    public static final String REDIS_PASSWORD_KEY = "REDIS_PASSWORD";
    public static final String REDIS_MAX_TOTAL_KEY = "REDIS_MAX_TOTAL";
    public static final String REDIS_MIN_IDLE_KEY = "REDIS_MIN_IDLE";
    public static final String REDIS_MAX_IDLE_KEY = "REDIS_MAX_IDLE";
    public static final String REDIS_MAX_WAIT_KEY = "REDIS_MAX_WAIT";
    public static final String REDIS_SERVER_NOTIFY_KEY = "REDIS_SERVER_NOTIFY";
    public static final String REDIS_PORT_NOTIFY_KEY = "REDIS_PORT_NOTIFY";
    public static final String REDIS_PASSWORD_NOTIFY_KEY = "REDIS_PASSWORD_NOTIFY";

    public static final int REDIS_MAX_TOTAL_DEFAULT = 30;
    public static final int REDIS_MIN_IDLE_DEFAULT = 0;
    public static final int REDIS_MAX_IDLE_DEFAULT = 10;
    public static final long REDIS_MAX_WAIT_DEFAULT = -1;

    public static final int REDIS_DEFAULT_TIMEOUT = 2000; // ms
    public static final int REDIS_MAX_BLOCK = 20000; //max item for 1 commit in pipeline

    public static final String EXPIRE_PATTERN = "__keyevent*__:expired";
    public static final String SET_PATTERN = "__keyevent@*__:set";
}
