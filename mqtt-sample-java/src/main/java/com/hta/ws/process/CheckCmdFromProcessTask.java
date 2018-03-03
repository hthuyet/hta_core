package com.hta.ws.process;

import com.hta.ws.common.DeviceManager;
import com.hta.ws.common.Properties;
import com.htaviet.redis.data.RedisResult;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class CheckCmdFromProcessTask extends Thread {

    protected final Logger logger = Logger.getLogger(CheckCmdFromProcessTask.class);

    int timeout = 0;

    public CheckCmdFromProcessTask(DeviceManager apManager) throws Exception {
        this.timeout = Properties.getKeyTimeOut();
    }

    @Override
    public void run() {
        try {
            logger.info("Start subscriber notification key expire command from server from redis");
        } catch (Exception ex) {
            logger.error("ERROR run: ", ex);
        }
    }

    public void resetExpireTime(String key) {
        key = DeviceManager.formatKey(DeviceManager.REDIS_CMD_EXPIRE_DOMAIN, key);
        logger.debug("------resetExpireTime: " + key + " - timeout: " + timeout + "s");
        RedisResult result = DeviceManager.redisClient.insertOrUpdateWithExpire(key, "", timeout);
        logger.debug("------resetExpireTime: result " + result.getMessage());
    }

    public static String convertExpireKeyToApKey(String expireKey) {
        if (StringUtils.isEmpty(expireKey) || expireKey.length() < DeviceManager.REDIS_CMD_EXPIRE_DOMAIN.length() + 1) {
            return null;
        }
        return expireKey.replaceAll(DeviceManager.REDIS_CMD_EXPIRE_DOMAIN, "");
    }
}
