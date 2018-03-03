/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hta.ws.command;

import com.hta.ws.application.Publisher;
import com.hta.ws.obj.CommandObj;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author thuyetlv
 */
public class CmdConfigMode extends Command {
    
    public CmdConfigMode(String device, String topic, String data) {
        this.type = Command.CMD_CONFIG_MODE;
        this.typeHis = CommandObj.TYPE_CONTROL;
        this.serialNumber = device;
        this.topic = topic;
        this.data = data;
        try {
            Document history = Publisher.getInstace().createHistoryObj(serialNumber, data, typeHis, description);
            if (history != null) {
                setHistory(history);
            }
        } catch (MqttException ex) {
            logger.error("ERROR createHistoryObj: ", ex);
        }
    }
    
    @Override
    public void sendCommand() throws Exception {
//        logger.info("---------sendCommand------");
//        Document history = Publisher.getInstace().publish(serialNumber, topic, data, typeHis, description);
//        if (history != null) {
//            ProcessHisCmd.getInstance().add(history);
//        }
    }
    
}
