/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.htaviet.websocket.main;

import com.htaviet.websocket.broadcasts.DeviceStateBroadcast;
import com.htaviet.websocket.mqtt.Subscriber;
import com.htaviet.websocket.process.ProcessDevice;

/**
 *
 * @author ThuyetLV
 */
public class Stop {

    public static void main(String[] args) throws Exception {
        Subscriber.getInstace().unsubscriber();
        WebsocketStart.closeWebSocket();
        DeviceStateBroadcast.getInstance().stop();
        ProcessDevice.getInstance().stop();
    }

}
