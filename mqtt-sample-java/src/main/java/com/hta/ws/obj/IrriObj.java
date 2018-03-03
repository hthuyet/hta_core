/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hta.ws.obj;

import java.util.Date;

/**
 *
 * @author thuyetlv
 */
public class IrriObj {

    public static final int STATUS_OFF = 0;
    public static final int STATUS_ON = 1;
    public static final int NOT_START = 0;
    public static final int START = 1;

    public static final String ID = "ID";
    public static final String STATUS = "status";
    public static final String IRR_ID = "IRR_ID";
    public static final String IRR_IS_START = "IRR_IS_START";
    public static final String COMMAND = "command";
    public static final String COMMAND_OFF = "command_off";
    public static final String COUNT = "count";
    public static final String START_TIME = "start_time";
    public static final String TOPIC = "topic";
    public static final String SERIAL = "serial";
    public static final String DEVICE_ID = "device_id";
    public static final String DESCRIPTION = "description";

    private long id;
    private long deviceId;
    private String topic;
    private String serial;
    private String command;
    private String commandOff;
    private int isStart;
    private int count;
    private String description;

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommandOff() {
        return commandOff;
    }

    public void setCommandOff(String commandOff) {
        this.commandOff = commandOff;
    }

    public int getIsStart() {
        return isStart;
    }

    public void setIsStart(int isStart) {
        this.isStart = isStart;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
