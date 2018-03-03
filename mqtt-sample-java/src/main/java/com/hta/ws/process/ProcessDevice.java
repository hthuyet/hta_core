/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hta.ws.process;

import com.hta.ws.common.DeviceManager;
import com.hta.ws.database.DbProcess;
import com.hta.ws.obj.Device;
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
    BlockingQueue<String> queue;
    int capacity = 100;

    ProcessDevice(String threadName) throws Exception {
        super(threadName);
        dbProcess = new DbProcess();
        queue = new LinkedBlockingQueue<String>();
    }

    @Override
    protected void process() {
        try {
            Thread.sleep(100);
            if (queue != null && queue.size() > 0) {
                List<String> listRecord = new ArrayList<String>(capacity);
                queue.drainTo(listRecord, capacity);

                List<String> listUpdate = new ArrayList<String>(capacity);
                Device device;
                for (String code : listRecord) {
                    DeviceManager.getInstance().getCheckConnectionTask().resetExpireTime(code);
                    device = DeviceManager.getInstance().getDevice(code);
                    if (device != null && device.getState() != Device.STATE_ONLINE) {
                        device.commitToRedis();
                        listUpdate.add(code);
                    }
                }
                if (!listUpdate.isEmpty()) {
                    dbProcess.updatePortDevice(listUpdate);
                }
            }
        } catch (Exception ex) {
            logger.error("ERROR process: ", ex);
        }
    }

    public void addAll(List listRecord) {
        queue.addAll(listRecord);
    }

    public void addAll(String device) {
        queue.add(device);
    }
}
