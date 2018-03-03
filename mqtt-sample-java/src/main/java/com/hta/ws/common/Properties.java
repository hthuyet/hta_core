package com.hta.ws.common;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.util.List;
import org.apache.log4j.Logger;

/**
 * doc thong tin tu file properties
 *
 * @author ThuyetLV
 */
public class Properties {

    protected static final Logger logger = Logger.getLogger(Properties.class);

    private static Configuration pr;

    static {
        try {
            pr = new PropertiesConfiguration("../etc/app.properties");
        } catch (ConfigurationException ex) {
            logger.error("ERROR Can not load configuration file", ex);
        }
    }

    public static int getInteger(String propertyKey, int defaultValue) {
        try {
            int prop = pr.getInt(propertyKey, defaultValue);
            return prop;
        } catch (ConversionException ce) {
            logger.warn("The value of " + propertyKey + " was not a int, instead using default value  " + defaultValue);
            return defaultValue;
        }
    }

    public static long getLong(String propertyKey, long defaultValue) {
        try {
            long prop = pr.getLong(propertyKey, defaultValue);
            return prop;
        } catch (ConversionException ce) {
            logger.warn("The value of " + propertyKey + " was not a long, instead using default value  " + defaultValue);
            return defaultValue;
        }
    }

    public static String getString(String propertyKey, String defaultValue) {
        try {
            String prop = pr.getString(propertyKey, defaultValue);
            return prop;
        } catch (ConversionException ce) {
            logger.warn("The value of " + propertyKey + " was not a string, instead using default value  " + defaultValue);
            return defaultValue;
        }
    }

    public static List getList(String propertyKey, List defaultValue) {
        try {
            List prop = pr.getList(propertyKey, defaultValue);
            return prop;
        } catch (ConversionException ce) {
            logger.warn("The value of " + propertyKey + " was not a list, instead using default value  " + defaultValue);
            return defaultValue;
        }
    }

    public static String getWsAddress() {
        return getString(AppConstant.WS_ADDRESS, AppConfig.WS_ADDRESS_DEFAULT);
    }

    public static int getCommandTimeout() {
        return getInteger(AppConstant.TIMEOUT_COMMAND, 10000);
    }

    //For Publisher
    public static String getMQTTUrl() {
        return getString(AppConstant.MQTT_URL, AppConfig.MQTT_URL_DEFAULT);
    }

    public static String getMQTTUser() {
        return getString(AppConstant.MQTT_USER, AppConfig.MQTT_USER_DEFAULT);
    }

    public static String getMQTTPass() {
        return getString(AppConstant.MQTT_PASS, AppConfig.MQTT_PASS_DEFAULT);
    }

    public static String getMQTTSub() {
        return getString(AppConstant.MQTT_SUBSCRIBER, AppConfig.MQTT_SUBSCRIBER_DEFAULT);
    }

    public static boolean getCleanSession() {
        return getString(AppConstant.MQTT_CLEAN_SESSION, "").equalsIgnoreCase("true");
    }

    //MONGODB
    public static String getMongoUrl() {
        return getString(AppConstant.MONGO_URL, "");
    }

    public static Integer getMongoPort() {
        return getInteger(AppConstant.MONGO_PORT, 27017);
    }

    public static String getMongoUser() {
        return getString(AppConstant.MONGO_USER, "");
    }

    public static String getMongoPass() {
        return getString(AppConstant.MONGO_PASS, "");
    }

    public static String getMongoDb() {
        return getString(AppConstant.MONGO_DB, "");
    }

    public static String getMongoCollectionHis() {
        return getString(AppConstant.MONGO_COLLECTION_HISTORY, "");
    }
    public static String getMongoCollectionHisIrr() {
        return getString(AppConstant.MONGO_COLLECTION_IRR, "");
    }

    //hikari
    public static String getHikariUrl() {
        return getString("hikari.url", "");
    }

    public static String getHikariUser() {
        return getString("hikari.user", "");
    }

    public static String getHikariPass() {
        return getString("hikari.pass", "");
    }

    public static Boolean getHikariCachePre() {
        return getString("hikari.cachePrepStmts", "").equalsIgnoreCase("true");
    }

    public static Integer getHikariPrepStmt() {
        return getInteger("hikari.prepStmtCacheSize", 250);
    }

    public static Integer getHikariPrepStmtSqlLimit() {
        return getInteger("hikari.prepStmtCacheSqlLimit", 2048);
    }

    public static Integer getHikariMinPool() {
        return getInteger("hikari.minPool", 1);
    }

    public static Integer getHikariMaxPool() {
        return getInteger("hikari.maxPool", 3);
    }

    //Process
    public static Integer getNumberThreadSendCmd() {
        return getInteger("NUMBER_THREAD_SEND_CMD", 3);
    }

    public static Integer getMinCmd() {
        return getInteger("MIN_CMD", 3);
    }

    public static Integer getMaxCmd() {
        return getInteger("MAX_CMD", 500);
    }

    public static String getListTypeSendOff() {
        return getString("SCHEDULE_SEND_OFF", "");
    }

    //Redis
    public static String getDeviceMgrKey() {
        return getString("REDIS_DEVICE_MGR_KEY", "REDIS_DEVICE_MGR_KEY");
    }

    public static Integer getKeyTimeOut() {
        return getInteger("REDIS_DEVICE_OFF", 30);  //Don vi giay
    }
}
