/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.htaviet.websocket.common;

import com.htaviet.redis.impl.RedisClient;
import com.htaviet.redis.impl.RedisFactory;
import com.htaviet.websocket.database.DbProcess;
import com.htaviet.websocket.obj.Device;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 *
 * @author ThuyetLV
 */
public class RedisDeviceSync {

    private static final Logger logger = Logger.getLogger(RedisDeviceSync.class.getSimpleName());
    DbProcess dbProcess;

    public static final int LIMIT = 1000;
    public static long ID = -1L;

    public RedisDeviceSync() {
        dbProcess = new DbProcess();
    }

    //<editor-fold defaultstate="collapsed" desc="syncData">
    public void syncData() {
        if (!StringUtils.isEmpty(Properties.getDeviceMgrKey())) {
            //flushAll
            flushAll();
            //Fet DB
            List<Device> lstDevice = dbProcess.getDevice(ID, LIMIT);
            while (!lstDevice.isEmpty()) {
                cacheNodeTable(lstDevice);
                lstDevice = dbProcess.getDevice(ID, LIMIT);
            }
        }
        logger.info("-----syncData Done-----");
    }//</editor-fold>

    public void flushAll() {
        Jedis redisServer = null;
        try {
            redisServer = RedisFactory.getInstance()
                    .getRedisInstance(Properties.getDeviceMgrKey())
                    .getRedisPool().getResource();

            redisServer.flushAll();
            logger.info("Finish flushAll");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            try {
                if (redisServer != null) {
                    redisServer.close();
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

    }

    void cacheNodeTable(List<Device> listNodes) {
        Jedis redisServer = null;
        Pipeline pipeline = null;
        try {
            redisServer = RedisFactory.getInstance()
                    .getRedisInstance(Properties.getDeviceMgrKey())
                    .getRedisPool().getResource();

            pipeline = redisServer.pipelined();
            int i = 0;
            // cache adslTable with key = serial
            logger.info("Starting.... load by AP serial");
            for (Device device : listNodes) {
//                DeviceManager.getInstance().getCheckConnectionTask().resetExpireTime(device.getCode());
                ID = (device.getId() > ID) ? device.getId() : ID;
                ++i;
                if (!StringUtils.isBlank(device.getCode())) {
                    pipeline.set(device.getCode().getBytes(), RedisClient.conf.asByteArray(device.toString()));
                }
                if (i % LIMIT == 0) {
                    pipeline.sync();
                }
            }
            pipeline.sync();
            logger.info("Finish load by AP serial");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            try {
                if (pipeline != null) {
                    pipeline.close();
                }
                if (redisServer != null) {
                    redisServer.close();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

    }
}
