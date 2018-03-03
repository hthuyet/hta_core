package com.htaviet.websocket.common;

import com.htaviet.websocket.process.ProcessDevice;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 * doc thong tin tu file properties
 *
 * @author ThuyetLV
 */
public class Properties {

    private static final Logger logger = Logger.getLogger(Properties.class.getSimpleName());

    private static Configuration pr;

    static {
        try {
            pr = new PropertiesConfiguration("../etc/websocket.properties");
        } catch (Exception ex) {
            logger.error("ERROR can not load configuration file: ", ex);
        }
    }

    private static int getInteger(String propertyKey, int defaultValue) {
        try {
            int prop = pr.getInt(propertyKey, defaultValue);
            return prop;
        } catch (ConversionException ce) {
            logger.error("ERROR getInteger, default value: " + defaultValue, ce);
            return defaultValue;
        }
    }

    private static String getString(String propertyKey, String defaultValue) {
        try {
            String prop = pr.getString(propertyKey, defaultValue);
            return prop;
        } catch (ConversionException ce) {
            logger.error("ERROR getInteger, default value: " + defaultValue, ce);
            return defaultValue;
        }
    }

    public static int getWebSocketPort() {
        return getInteger("websocketPort", 6789);
    }

    public static String getListBroadCast() {
        return "." + getString("LIST_BROADCAST", "CRITICAL.MAJOR") + ".";
    }

    public static Long getSleepProcess() {
        return Long.parseLong(getString("SLEEP_PROCESS", "500"));
    }

    public static int getQueueDevice() {
        return getInteger("QUEUE_SIZE_DEVICE", 1000);
    }

    //For Publisher
    public static String getMQTTUrl() {
        return getString("MQTT_URL", "");
    }

    public static String getMQTTUser() {
        return getString("MQTT_USER", "");
    }

    public static String getMQTTPass() {
        return getString("MQTT_PASS", "");
    }

    public static String getMQTTSub() {
        return getString("MQTT_SUBSCRIBER", "");
    }

    public static boolean getCleanSession() {
        return getString("MQTT_CLEAN_SESSION", "").equalsIgnoreCase("true");
    }

    //hikari
    public static String getPoolType() {
        return getString("POOL_TYPE", "hikari");
    }

    public static String getHikariDatasouce() {
        return getString("hikari.dataSource", "com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource");
    }

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

    //Redis
    public static String getDeviceMgrKey() {
        return getString("REDIS_DEVICE_MGR_KEY", "REDIS_DEVICE_MGR_KEY");
    }

    public static Integer getKeyTimeOut() {
        return getInteger("REDIS_DEVICE_OFF", 30);  //Don vi giay
    }

    public static Integer getAutoAdd() {
        return getInteger("AUTO_ADD", 0);
    }
}
