//package com.hta.ws.process;
//
//import com.htaviet.redis.data.RedisNotificationData;
//import com.htaviet.redis.impl.Subscriber;
//import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
//
//public class LostCmdFromProcessEvent extends Subscriber {
//
//    protected final Logger logger = Logger.getLogger(LostCmdFromProcessEvent.class);
//
//
//    public LostCmdFromProcessEvent() throws Exception {
//        super(Properties.getDeviceMgrKey());
//    }
//
//    @Override
//    public void onExpireEvent(RedisNotificationData eventData) {
//        try {
//            String key = eventData.getKey();
//            String apKey = CheckConnectionTask.convertExpireKeyToApKey(key);
//            logger.debug("Lost command of process with device " + apKey);
//            if (StringUtils.isNotEmpty(apKey)) {
//                Device device = DeviceManager.getInstance().getDevice(apKey);
//                Integer lastStatus = -1;
//                if (device != null) {
//                    lastStatus = device.getState();
//                    device.setState(Device.STATE_OFFLINE);
//                    if (lastStatus != Device.STATE_OFFLINE) {
//                        logger.warn("Lost connection to device " + apKey);
//                        device.setPortStatus("[0,0,0,0]");
//                        dbProcess.updatePortDevice(device);
//                        DeviceManager.getInstance().addDevice(device.getCode(), device);
//                        DeviceStateBroadcast.getInstance().put(device);
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            logger.error("ERROR onExpireEvent: ", ex);
//        }
//    }
//}
