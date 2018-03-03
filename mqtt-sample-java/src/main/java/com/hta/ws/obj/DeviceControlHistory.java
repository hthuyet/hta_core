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
public class DeviceControlHistory {
    public static final int TYPE_CONTROL = 1;
    public static final int TYPE_SCHE_SV = 2;
    public static final int TYPE_SCHE_DEVICE = 3;
    public static final int TYPE_IRR = 4;
    
    private String code;
    private String command;
    private int type;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    
}
