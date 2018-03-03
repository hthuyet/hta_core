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
public class ScheduleObj {

    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_DONE = 2;
    public static final int STATUS_STOP = 3;

    public static final int TYPE_CONST = 1;
    public static final int TYPE_SCHEDULE = 2;

    public static final String ID = "ID";
    public static final String TYPE = "type";
    public static final String COMMAND = "command";
    public static final String COUNT = "count";
    public static final String IS_START = "IS_START";
    public static final String START_TIME = "start_time";
    public static final String TOPIC = "topic";
    public static final String SERIAL = "serial";
    public static final String DEVICE_ID = "device_id";
    public static final String DESCRIPTION = "description";

    private long id;
    private String topic;
    private String serial;
    private int type;
    private String command;
    private int isStart;
    private int count;
    private Date startTime;
    private long deviceId;
    private String description;

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
