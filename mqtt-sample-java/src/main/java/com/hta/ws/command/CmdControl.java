/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hta.ws.command;

import com.hta.ws.application.Publisher;
import com.hta.ws.common.Ultils;
import com.hta.ws.obj.CommandObj;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author thuyetlv
 */
public class CmdControl extends Command {

    protected Long deviceId;

    public CmdControl(Long deviceId, String device, String topic, String data) {
        this.deviceId = deviceId;
        this.type = Command.CMD_CONTROL;
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
        Document history = getHistory();
        if (history != null) {
            logger.debug("------------insertListIrriHis: " + serialNumber);
            Ultils.getInstance().insertListIrriHis(deviceId, data, type);
        }
//        Document history = Publisher.getInstace().publish(serialNumber, topic, data, typeHis, description);
//        if (history != null) {
//            logger.debug("------------insertListIrriHis: " + serialNumber);
//            ProcessHisCmd.getInstance().add(history);
//            Ultils.getInstance().insertListIrriHis(deviceId, data, type);
//        }
    }

}
