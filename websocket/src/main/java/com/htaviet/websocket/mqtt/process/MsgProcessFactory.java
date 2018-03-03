/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.htaviet.websocket.mqtt.process;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author thuyetlv
 */
public class MsgProcessFactory {

    public static final String HTA_TOPIC = "HTAE1";
    public static final String EWI_TOPIC = "eWi2";

    public MgsProcess getProcessor(String topic) {
        if (topic == null) {
            return null;
        }
        topic = topic.toUpperCase();
        if (StringUtils.startsWith(topic, HTA_TOPIC)) {
            return new HTAProcess();
        } else if (topic.equalsIgnoreCase(EWI_TOPIC)) {
            return new EWiProcess();

        }

        return null;
    }
}
