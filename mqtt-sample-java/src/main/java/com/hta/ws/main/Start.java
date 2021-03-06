/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hta.ws.main;

import com.hta.ws.application.Publisher;
import com.hta.ws.application.WebServicePublisher;
import com.hta.ws.application.Subscriber;
import com.hta.ws.common.DeviceManager;
import com.hta.ws.database.ConnectionPool;
import com.hta.ws.mongo.ConnectToMongo;
import com.hta.ws.process.ProcessDevice;
import com.hta.ws.process.ProcessHisCmd;
import com.hta.ws.process.ProcessIrriStart;
import com.hta.ws.process.ProcessIrriStop;
import com.hta.ws.process.ProcessSchedule;
import com.hta.ws.process.ProcessSendCommandMgr;
import com.htaviet.redis.impl.RedisFactory;
import java.io.FileInputStream;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author ThuyetLV
 */
public class Start {

//    protected static final Logger logger = Logger.getLogger(Start.class);
    public static void main(String[] args) {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("../etc/log4j.conf"));
            PropertyConfigurator.configure(props);
//            PropertyConfigurator.configure("D:\\Project\\IOT\\smart\\mqtt-sample-java\\etc\\log4j.conf");

            ConnectionPool.getInstance();
//            testDb();

            //Start Redis
            RedisFactory.getInstance().autoLoadRedisInstance();

            DeviceManager.getInstance();

            ConnectToMongo.getInstace();
            //Process
            ProcessDevice.getInstance().start();
            ProcessHisCmd.getInstance().start();
            ProcessSendCommandMgr.getInstance().start();
            ProcessSchedule.getInstance().start();
            ProcessIrriStart.getInstance().start();
            ProcessIrriStop.getInstance().start();

            //Utils
            Publisher.getInstace();
            Subscriber.getInstace().subscriber();

            //WebService
            WebServicePublisher.publishWebservice();
        } catch (Exception ex) {
            ex.printStackTrace();
//            logger.error("ERROR Start: ", ex);
            System.exit(1);
        }
    }

//    public static void testDb() {
//        Connection connection = null;
//        PreparedStatement pstmt = null;
//        ResultSet resultSet = null;
//        try {
//            connection = ConnectionPool.getInstance().getConnection();
//            pstmt = connection.prepareStatement("SELECT * FROM config");
//
//            logger.info("The Connection Object is of Class: " + connection.getClass());
//
//            resultSet = pstmt.executeQuery();
//            while (resultSet.next()) {
//                logger.info(resultSet.getString("key") + "," + resultSet.getString("code") + "," + resultSet.getString("value"));
//            }
//
//        } catch (Exception e) {
//            try {
//                connection.rollback();
//            } catch (SQLException e1) {
//                logger.error("ERROR SQLException: ", e);
//            }
//            logger.error("ERROR Exception: ", e);
//        }
//    }
}
