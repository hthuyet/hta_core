/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.htaviet.websocket.mqtt.process;

import com.htaviet.websocket.broadcasts.DeviceStateBroadcast;
import com.htaviet.websocket.common.DeviceManager;
import static com.htaviet.websocket.mqtt.DeviceStateMqttCB.TYPE_GET_INFO;
import static com.htaviet.websocket.mqtt.DeviceStateMqttCB.TYPE_INFO;
import com.htaviet.websocket.obj.Device;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

/**
 *
 * @author thuyetlv
 */
public class HTAProcess implements MgsProcess {

    public static final int TYPE_HTA = 2;
    public static final long ADMIN_ID = 1L;

    protected static final Logger logger = Logger.getLogger(HTAProcess.class);

    @Override
    public void processMessage(String topic, MqttMessage mqttMessage) {
        try {
            String message = new String(mqttMessage.getPayload());
            JSONObject jsonObj = new JSONObject(message);
            String cmd = jsonObj.getString("cmd");

            if (!StringUtils.isBlank(cmd) && (TYPE_INFO.equalsIgnoreCase(cmd) || TYPE_GET_INFO.equalsIgnoreCase(cmd))) {
                String data = jsonObj.getString("data");
                Device device = new Device();
                device.setCode(jsonObj.getString("uid"));
                device.setState(Device.STATE_ONLINE);
                device.setType(TYPE_HTA);
                device.setUserId(ADMIN_ID);
                if (!StringUtils.isBlank(data)) {
                    device.setPortStatus("[" + data + "]");
                }
                DeviceStateBroadcast.getInstance().put(device);
            }
        } catch (Exception ex) {
            logger.error("ERROR processMessage: " + topic, ex);
        }
    }
}
