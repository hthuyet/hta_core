/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hta.ws.mqtt.process;

import com.hta.ws.command.Command;
import com.hta.ws.command.CommandRequestFactory;
import com.hta.ws.common.Ultils;
import com.hta.ws.process.ProcessDevice;
import com.hta.ws.process.ProcessHisCmd;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

/**
 *
 * @author thuyetlv
 */
public class HTAProcess implements MgsProcess {

    public static final String TYPE_INFO = "1";
    public static final String TYPE_CONTROL = "2";
    public static final String TYPE_GET_INFO = "6";

    protected static final Logger logger = Logger.getLogger(HTAProcess.class);

    @Override
    public void processMessage(String topic, MqttMessage mqttMessage) {
        try {
            String message = new String(mqttMessage.getPayload());
            JSONObject jsonObj = new JSONObject(message);
            if (!StringUtils.isBlank(jsonObj.getString("uid"))) {
                String typeCmd = jsonObj.getString("cmd");
                if (!StringUtils.isBlank(typeCmd) && (!TYPE_INFO.equalsIgnoreCase(typeCmd) && !TYPE_GET_INFO.equalsIgnoreCase(typeCmd))) {
                    ProcessDevice.getInstance().addAll(jsonObj.getString("uid"));
                }
                Command cmd = CommandRequestFactory.getCommand(jsonObj.getString("uid"), Integer.parseInt(jsonObj.getString("cmd")));
                if (cmd != null) {
                    logger.debug("-----found cmd---");
                    //Insert history
                    Document history = cmd.getHistory();
                    if (history != null) {
                        ProcessHisCmd.getInstance().add(history);
                    }
                    cmd.receiveResult(message);
                } else {
                    //MQTT tu process chu ko phai ws
                    if (!StringUtils.isBlank(typeCmd) && (!TYPE_CONTROL.equalsIgnoreCase(typeCmd))) {

                    }
                }
            }
        } catch (Exception ex) {
            logger.error("ERROR processMessage: " + topic, ex);
        }
    }
}
