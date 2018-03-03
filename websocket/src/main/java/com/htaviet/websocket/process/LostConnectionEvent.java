package com.htaviet.websocket.process;

import com.htaviet.redis.data.RedisNotificationData;
import com.htaviet.redis.impl.Subscriber;
import com.htaviet.websocket.broadcasts.DeviceStateBroadcast;
import com.htaviet.websocket.common.DeviceManager;
import com.htaviet.websocket.common.Properties;
import com.htaviet.websocket.database.DbProcess;
import com.htaviet.websocket.obj.Device;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class LostConnectionEvent extends Subscriber {

    protected final Logger logger = Logger.getLogger(LostConnectionEvent.class);

    DbProcess dbProcess;

    public LostConnectionEvent() throws Exception {
        super(Properties.getDeviceMgrKey());
        dbProcess = new DbProcess();
    }

    @Override
    public void onExpireEvent(RedisNotificationData eventData) {
        try {
//            Object data = eventData.getData();
//            logger.info("--------onExpireEvent: " + data);
//            logger.info("--------onExpireEvent getKey: " + eventData.getKey());
//            if (data instanceof String) {
//                logger.debug("-------onExpireEvent String: " + data);
//                logger.debug("-------onExpireEvent key: " + eventData.getKey());
//            } else {
//                key = eventData.getKey();
//                if (data == null) {
//                    logger.debug("------- onExpireEvent unknown data null " + key);
//                } else {
//                    logger.debug("------- onExpireEvent unknown: " + data.toString());
//                }
//            }
            String key = eventData.getKey();
            String apKey = CheckConnectionTask.convertExpireKeyToApKey(key);
            logger.debug("Lost connection with device " + apKey);
            if (StringUtils.isNotEmpty(apKey)) {
                Device device = DeviceManager.getInstance().getDevice(apKey);
                Integer lastStatus = -1;
                if (device != null) {
                    lastStatus = device.getState();
                    device.setState(Device.STATE_OFFLINE);
                    if (lastStatus != Device.STATE_OFFLINE) {
                        logger.warn("Lost connection to device " + apKey);
                        device.setPortStatus("[0,0,0,0]");
                        dbProcess.updatePortDevice(device);
                        DeviceManager.getInstance().addDevice(device.getCode(), device);
                        DeviceStateBroadcast.getInstance().put(device);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("ERROR onExpireEvent: ", ex);
        }
    }
}
