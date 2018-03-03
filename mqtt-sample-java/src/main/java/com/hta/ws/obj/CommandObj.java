/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hta.ws.obj;

import com.hta.ws.common.Ultils;
import org.json.JSONObject;

/**
 *
 * @author thuyetlv
 */
public class CommandObj {

    public static final String CTRL_ON = "1";
    public static final int CMD_CONTROL = 2;

    public static final int TYPE_CONTROL = 1;
    public static final int TYPE_SCHE_SV = 2;
    public static final int TYPE_SCHE_DEVICE = 3;
    public static final int TYPE_IRR = 4;

    private long deviceId;
    private String device;
    private String topic;
    private String data;
    private int type;
    private String description;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CommandObj(Long deviceId, String device, String topic, String data, int type, String description) {
        this.deviceId = deviceId;
        this.device = device;
        this.topic = topic;
        this.data = data;
        this.type = type;
        this.description = description;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public static void main(String[] args) {
        String s = "{\n"
                + "	\"uid\": \"123456\",\n"
                + "	\"cmd\": \"2\",\n"
                + "	\"data\": \"2,2,2,2\",\n"
                + "	\"time\": [\"5\", \"10\", \"15\", \"20\"]\n"
                + "}";
        JSONObject jsonObj = new JSONObject(s);
        int cmd = (jsonObj.getString("cmd") == null) ? 0 : jsonObj.getInt("cmd");
        System.out.println("cmd: " + cmd);
        System.out.println("data: " + jsonObj.getString("data"));
        System.out.println("time: " + jsonObj.getJSONArray("time"));
        System.out.println("time 0: " + jsonObj.getJSONArray("time").get(0));
        System.out.println("time 1: " + jsonObj.getJSONArray("time").get(1));
        System.out.println("time 2: " + jsonObj.getJSONArray("time").get(2));
        System.out.println("time 3: " + jsonObj.getJSONArray("time").get(3));
    }

    public Boolean insertListIrriHis() {
        return Ultils.getInstance().insertListIrriHis(deviceId, this.data, this.type);
    }

}
