/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.htaviet.websocket.obj;

import com.google.gson.Gson;
import com.htaviet.redis.data.RedisResponse;
import com.htaviet.websocket.common.DeviceManager;
import java.io.Serializable;

/**
 *
 * @author thuyetlv
 */
public class Device implements Serializable {

    static Gson gson = new Gson();

    public static final int STATE_OFFLINE = 0;
    public static final int STATE_ONLINE = 1;

    public static final String ID = "id";
    public static final String CODE = "code";
    public static final String PORT_STATUS = "port_status";
    public static final String STATE = "state";

    private long id;
    private String code;
    private String portStatus;
    private int state; //Trang thai online, offline
    private int type;
    private long userId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static Device convert(String info) {
        return gson.fromJson(info, Device.class);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPortStatus() {
        return portStatus;
    }

    public void setPortStatus(String portStatus) {
        this.portStatus = portStatus;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public synchronized boolean commitToRedis() {
        return DeviceManager.redisClient.insertOrUpdate(DeviceManager.formatKey(DeviceManager.REDIS_DEVICE_MANAGER_DOMAIN, this.getCode()), this).getResponse().compareTo(RedisResponse.SUCCESS) == 0;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
