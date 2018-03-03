package com.hta.ws.common;

import com.google.gson.Gson;
import com.hta.ws.obj.Device;
import com.hta.ws.process.CheckConnectionTask;
import com.htaviet.redis.data.RedisResponse;
import com.htaviet.redis.impl.RedisClient;
import com.htaviet.redis.impl.RedisFactory;
import org.apache.log4j.Logger;

public class DeviceManager {

    private static DeviceManager instance = null;
    protected final Logger logger = Logger.getLogger(DeviceManager.class);

    public static final String REDIS_DEVICE_MANAGER_DOMAIN = "";
    public static final String REDIS_DEVICE_EXPIRE_DOMAIN = "EX_";
    public static final String REDIS_CMD_EXPIRE_DOMAIN = "CMD_EX_";

    public int echoTimeInterval;
    public int maxPendingRequest;

    public static RedisClient redisClient;

    private CheckConnectionTask checkConnectionTask;    // Thread process check ap state

    private DeviceManager() throws Exception {
        logger.info("DeviceManager started");
        echoTimeInterval = 10000;
        maxPendingRequest = 3;
        redisClient = RedisFactory.getInstance().getRedisInstance(Properties.getDeviceMgrKey());
        checkConnectionTask = new CheckConnectionTask(this);
        checkConnectionTask.start();
    }

    public static synchronized DeviceManager getInstance() throws Exception {
        if (instance == null) {
            instance = new DeviceManager();
        }
        return instance;
    }

    public Device getDevice(String key) {
        Gson gson = new Gson();
        Object o = redisClient.getObject(formatKey(REDIS_DEVICE_MANAGER_DOMAIN, key));
        if (o != null && o instanceof String) {
            return gson.fromJson((String) o, Device.class);
        }
        return null;
    }

    public boolean addDevice(String key, Device device) {
        return redisClient.insertOrUpdate(formatKey(REDIS_DEVICE_MANAGER_DOMAIN, key), device.toString()).getResponse().compareTo(RedisResponse.SUCCESS) == 0;
    }

    public boolean removeDevice(String key) {
        return redisClient.delete(formatKey(REDIS_DEVICE_MANAGER_DOMAIN, key)).getResponse().compareTo(RedisResponse.SUCCESS) == 0;
    }

    public static String formatKey(String domain, String key) {
        return (domain + key);
    }

    public CheckConnectionTask getCheckConnectionTask() {
        return checkConnectionTask;
    }

    public void stop() {
        if (checkConnectionTask != null) {
            checkConnectionTask.stop();
        }
    }
}
