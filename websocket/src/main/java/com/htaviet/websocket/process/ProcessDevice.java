/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.htaviet.websocket.process;

import com.htaviet.websocket.common.DeviceManager;
import com.htaviet.websocket.common.Properties;
import com.htaviet.websocket.database.DbProcess;
import com.htaviet.websocket.obj.Device;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;

/**
 *
 * @author ThuyetLV
 */
public class ProcessDevice extends ProcessThreadMX {

    private static final Logger logger = Logger.getLogger(ProcessDevice.class.getSimpleName());
    long sleepTime = 500;

    static ProcessDevice _instance;

    public static synchronized ProcessDevice getInstance() throws Exception {
        if (_instance == null) {
            _instance = new ProcessDevice(ProcessDevice.class.getSimpleName());
        }
        return _instance;
    }

    DbProcess dbProcess;
    BlockingQueue<Device> queueInsert;
    BlockingQueue<Device> queueUpdate;
    int capacity = 100;

    ProcessDevice(String threadName) throws Exception {
        super(threadName);
        dbProcess = new DbProcess();
        queueInsert = new LinkedBlockingQueue<Device>();
        queueUpdate = new LinkedBlockingQueue<Device>();
        capacity = Properties.getQueueDevice();
    }

    @Override
    protected void process() {
        try {
            Thread.sleep(100);
            if (queueInsert != null && queueInsert.size() > 0) {
                List<Device> listInsert = new ArrayList<Device>(capacity);
                queueInsert.drainTo(listInsert, capacity);
                //Update DB
                dbProcess.insertDevice(listInsert);
            }
            if (queueUpdate != null && queueUpdate.size() > 0) {
                List<Device> listUpdate = new ArrayList<Device>(capacity);
                queueUpdate.drainTo(listUpdate, capacity);
                for (Device device : listUpdate) {
                    DeviceManager.getInstance().addDevice(device.getCode(), device);
                }
                //Update DB
                dbProcess.updatePortDevice(listUpdate);
            }
        } catch (Exception ex) {
            logger.error("ERROR process: ", ex);
        }
    }

    public void addInsert(List listRecord) {
        queueInsert.addAll(listRecord);
        logQueueSize();
    }

    public void addUpdate(List listRecord) {
        queueUpdate.addAll(listRecord);
        logQueueSize();
    }

    private void logQueueSize() {
        int sizeUpdate = queueUpdate.size();
        int sizeInsert = queueInsert.size();
        if (sizeUpdate > capacity || sizeInsert > capacity) {
            logger.warn("QUEUE IS OVER CAPACITY INSERT|UPDATE: " + sizeInsert + " - " + sizeUpdate);
        }
    }

    public void addInsert(Device device) {
        queueInsert.add(device);
        logQueueSize();
    }

    public void addUpdate(Device device) {
        queueUpdate.add(device);
        logQueueSize();
    }
}
