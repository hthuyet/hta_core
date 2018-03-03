package com.hta.ws.application;

import com.hta.ws.mqtt.process.MsgProcessFactory;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class CmdMqttCallBack implements MqttCallback {

    protected static final Logger logger = Logger.getLogger(CmdMqttCallBack.class);

    MsgProcessFactory msgProcessFactory;

    public CmdMqttCallBack() {
        msgProcessFactory = new MsgProcessFactory();
    }

    @Override
    public void connectionLost(Throwable throwable) {
        logger.warn("Connection to MQTT broker lost!");
        try {
            Subscriber.getInstace().subscriber();
        } catch (MqttException ex) {
            logger.error("ERROR RECONECT: ", ex);
        }
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        logger.debug("Message received:\t" + s + " -- " + new String(mqttMessage.getPayload()));
        msgProcessFactory.getProcessor(s).processMessage(s, mqttMessage);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        logger.warn("----deliveryComplete----");
    }
}
