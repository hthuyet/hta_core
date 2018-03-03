/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hta.ws.command;

import com.hta.ws.application.Publisher;

/**
 *
 * @author thuyetlv
 */
public class CmdReqState extends Command {

    public CmdReqState(String device, String topic, String data) {
        this.type = Command.CMD_REQ_STATE;
        this.serialNumber = device;
        this.topic = topic;
        this.data = data;
    }

    public CmdReqState(String device, String topic) {
        this.type = Command.CMD_REQ_STATE;
        this.serialNumber = device;
        this.topic = topic;

    }

    public static String createCommand(String serialNumber) {
        return "{\"uid\": \"" + serialNumber + "\",\"cmd\": \"" + Command.CMD_REQ_STATE + "\",\"data\": \"\"}";
    }

    @Override
    public void sendCommand() throws Exception {
        logger.info("---------sendCommand------");
        Publisher.getInstace().publish(serialNumber, topic, data, typeHis, description);
    }

}
