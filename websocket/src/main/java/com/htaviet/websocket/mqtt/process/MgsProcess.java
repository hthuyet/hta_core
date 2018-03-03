/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.htaviet.websocket.mqtt.process;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author thuyetlv
 */
public interface MgsProcess {

    void processMessage(String topic, MqttMessage message);
}
