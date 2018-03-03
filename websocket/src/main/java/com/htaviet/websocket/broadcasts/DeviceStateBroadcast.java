/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.htaviet.websocket.broadcasts;

import com.google.gson.Gson;
import com.htaviet.websocket.common.DeviceManager;
import com.htaviet.websocket.common.Properties;
import com.htaviet.websocket.events.DeviceSocket;
import com.htaviet.websocket.obj.Device;
import com.htaviet.websocket.process.ProcessDevice;
import com.htaviet.websocket.process.ProcessThreadMX;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.websocket.Session;
import org.apache.log4j.Logger;

/**
 *
 * @author ThuyetLV
 */
public class DeviceStateBroadcast extends ProcessThreadMX {

    private static final Logger logger = Logger.getLogger(DeviceStateBroadcast.class.getSimpleName());
    static private BlockingQueue<Device> queue;
    long sleepTime = 500;
    private static final Gson gson = new Gson();

    static DeviceStateBroadcast _instance;

    public static synchronized DeviceStateBroadcast getInstance() throws Exception {
        if (_instance == null) {
            _instance = new DeviceStateBroadcast(DeviceStateBroadcast.class.getSimpleName());
        }
        return _instance;
    }

    DeviceStateBroadcast(String threadName) throws Exception {
        super(threadName);
        sleepTime = Properties.getSleepProcess();
        queue = new LinkedBlockingQueue<Device>();
    }

    @Override
    protected void process() {
        try {
            Thread.sleep(sleepTime);

            int size = getSize();
            if (size > 0) {
                List<Device> lstTmp = new ArrayList<Device>(100);
                List<Device> alarms = new ArrayList<Device>(100);
                queue.drainTo(lstTmp, 100);

                //
                Device deviceRedis;
                for (Device alarm : lstTmp) {
                    if (alarm.getState() == Device.STATE_OFFLINE) {
                        logger.debug("device : " + alarm.getCode() + " offfline.");
                        alarms.add(alarm);
                        continue;
                    }
                    DeviceManager.getInstance().getCheckConnectionTask().resetExpireTime(alarm.getCode());
                    deviceRedis = DeviceManager.getInstance().getDevice(alarm.getCode());
                    if (deviceRedis == null) {
                        if (Properties.getAutoAdd() > 0) {
                            logger.info("Add device : " + alarm.getCode() + " and redis.");
                            //Add truoc vi neu add sau de bi trung key
                            DeviceManager.getInstance().addDevice(alarm.getCode(), alarm);
                            ProcessDevice.getInstance().addInsert(alarm);
                        }
                    } else {
                        if ((deviceRedis.getPortStatus() == null && alarm.getPortStatus() != null)
                                || !deviceRedis.getPortStatus().equalsIgnoreCase(alarm.getPortStatus())
                                || deviceRedis.getState() != alarm.getState()) {
                            DeviceManager.getInstance().addDevice(alarm.getCode(), alarm);
                            alarms.add(alarm);
                        }
                        if (alarm.getState() != Device.STATE_OFFLINE) {
                            DeviceManager.getInstance().addDevice(alarm.getCode(), alarm);
                            ProcessDevice.getInstance().addUpdate(alarm);
                        }
                    }
                }

                //
                Set<Session> sessions = DeviceSocket.getSessions();
                if (sessions == null || sessions.isEmpty()) {
                    logger.debug("Device state clients is isEmpty...");
                    return;
                }

                logger.debug("Broadcast Device state to clients, Size: " + sessions.size());

                ArrayList<Device> sendMsgs = new ArrayList<>();
                String json;
                for (Session session : sessions) {
                    sendMsgs = new ArrayList<>();
                    //Tao list alarm cho tung client
                    for (Device alarm : alarms) {
                        //Check co su thay doi
                        if (DeviceSocket.checkToSend(session.getId(), alarm.getCode())) {
                            sendMsgs.add(alarm);
                        }
                    }

                    if (!sendMsgs.isEmpty()) {
                        json = gson.toJson(sendMsgs);
                        try {
                            session.getBasicRemote().sendText(json);
                            logger.debug("Broadcast to " + session.getId() + " sendMsgs: " + sendMsgs.size());
                        } catch (Exception ex) {
                            logger.error("ERROR sendText: " + session.getId(), ex);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("ERROR process: ", ex);
        }
    }

    public synchronized void putAll(List<Device> devices) {
        queue.addAll(devices);
        logger.debug("Device state is enqueuing ..., current size: " + getSize());
    }

    public synchronized void put(Device device) {
        queue.add(device);
        logger.debug("Device state is enqueuing ..., current size: " + getSize());
    }

    public synchronized int getSize() {
        return queue.size();
    }
}
