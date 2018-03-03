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
public class IrriHisObj {

    public static final int TYPE_CTRL = 1;
    public static final int TYPE_SCHE_SV = 2;
    public static final int TYPE_IRR = 3;
    
    public static final String DEVICE_ID = "device_id";
    public static final String AREA_ID = "area_id";
    public static final String PORT = "port";
    public static final String DAU_TUOI = "dau_tuoi";
    public static final String GOC = "goc";
    public static final String LUONG_NUOC = "luong_nuoc";
    public static final String PORT_NAME = "port_name";
    public static final String TONG = "tong";
    public static final String TIME = "time";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String TYPE = "type";

    Long areaId;
    Long deviceId;
    Long devicePortId;
    String code;
    int port;
    int dautuoi;
    int goc;
    float luongnuoc;
    String portName;
    Date startSime;
    Date endTime;
    int time;
    int type;

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getDevicePortId() {
        return devicePortId;
    }

    public void setDevicePortId(Long devicePortId) {
        this.devicePortId = devicePortId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getDautuoi() {
        return dautuoi;
    }

    public void setDautuoi(int dautuoi) {
        this.dautuoi = dautuoi;
    }

    public int getGoc() {
        return goc;
    }

    public void setGoc(int goc) {
        this.goc = goc;
    }

    public float getLuongnuoc() {
        return luongnuoc;
    }

    public void setLuongnuoc(float luongnuoc) {
        this.luongnuoc = luongnuoc;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public Date getStartSime() {
        return startSime;
    }

    public void setStartSime(Date startSime) {
        this.startSime = startSime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
