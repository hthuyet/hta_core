/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hta.ws.obj;

/**
 *
 * @author thuyetlv
 */
public class DevicePortObj {

    public static final String ID = "id";
    public static final String DEVICE_ID = "device_id";
    public static final String PORT = "port";
    public static final String DAUTUOI = "dautuoi";
    public static final String GOC = "goc";
    public static final String LUONGNUOC = "luongnuoc";
    public static final String PORT_NAME = "port_name";
    public static final String AREA_ID = "area_id";

    private long id;
    private long deviceId;
    private long areaId;
    private int port;
    private int dautuoi;
    private int goc;
    private Float luongnuoc;
    private String portName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public long getAreaId() {
        return areaId;
    }

    public void setAreaId(long areaId) {
        this.areaId = areaId;
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

    public Float getLuongnuoc() {
        return luongnuoc;
    }

    public void setLuongnuoc(Float luongnuoc) {
        this.luongnuoc = luongnuoc;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }
    
    

}
