/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.htaviet.websocket.main;

import com.htaviet.redis.impl.RedisFactory;
import com.htaviet.websocket.broadcasts.DeviceStateBroadcast;
import com.htaviet.websocket.common.DeviceManager;
import com.htaviet.websocket.common.RedisDeviceSync;
import com.htaviet.websocket.database.ConnectionPool;
import com.htaviet.websocket.database.DbProcess;
import com.htaviet.websocket.mqtt.Subscriber;
import com.htaviet.websocket.process.ProcessDevice;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author ThuyetLV
 */
public class Start {

    protected static Logger logger = Logger.getLogger(Start.class);

    public static void main(String[] args) throws IOException, Exception {
        Properties props = new Properties();
        props.load(new FileInputStream("../etc/log4j.conf"));
        PropertyConfigurator.configure(props);

        //Start Redis
        RedisFactory.getInstance().autoLoadRedisInstance();

        ConnectionPool.getInstance();
        
        //Reset trang thai tbi ve offline
        updateDeviceOff();
//        ConnectionPool.getInstance().getConnection();
        
        //Khoi tao Device manager quan ly thiet bi tren redis
        DeviceManager.getInstance();

        //Dong bo du lieu bang device len redis lan dau tien
        RedisDeviceSync rds = new RedisDeviceSync();
        rds.syncData();

        //Process start here
        ProcessDevice.getInstance().start();
        DeviceStateBroadcast.getInstance().start();

        //MQTT Subscriber here
        Subscriber.getInstace().subscriber();
        
        //Websocket start here
        WebsocketStart.run();
    }
    
    public static void updateDeviceOff(){
        DbProcess dbProcess = new DbProcess();
        dbProcess.updateDeviceOff();
    }
}
