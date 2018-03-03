package com.htaviet.websocket.mqtt;

import com.htaviet.websocket.common.Properties;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public final class Subscriber {

    private final Logger logger = Logger.getLogger(Subscriber.class);

    static Subscriber _instance;

    public synchronized static Subscriber getInstace() throws MqttException {
        if (_instance == null) {
            _instance = new Subscriber();
        }
        return _instance;
    }

    private String user;
    private String pass;
    private String url;
    private String topic;
    private String clientId;
    private MemoryPersistence memoryPersistence;

    MqttConnectOptions conOpt;

    MqttClient client;

    Subscriber() throws MqttException {
        url = Properties.getMQTTUrl();
        user = Properties.getMQTTUser();
        pass = Properties.getMQTTPass();
        topic = Properties.getMQTTSub();

        conOpt = new MqttConnectOptions();
        if (pass != null) {
            conOpt.setPassword(pass.toCharArray());
        }
        if (user != null) {
            conOpt.setUserName(user);
        }

        memoryPersistence = new MemoryPersistence();
        clientId = MqttAsyncClient.generateClientId();

        conOpt.setCleanSession(Properties.getCleanSession());

        logger.info("----------MQTTUrl: " + url);
        logger.info("----------MQTTUser: " + user);
        logger.info("----------MQTTPass: " + pass);
        logger.info("----------MQTTSub: " + topic);
        logger.info("----------clientId: " + clientId);
        connect();
    }

    public MqttClient connect() throws MqttException {
        if (client == null || !client.isConnected()) {
            logger.info("----------connect is not Connected------");
//            client = new MqttClient(url, MqttClient.generateClientId());
            client = new MqttClient(url, clientId, memoryPersistence);
            client.setCallback(new DeviceStateMqttCB());
            client.connect(conOpt);
        } else {
            logger.info("----------connect isConnected------");
        }
        return client;
    }

    public void subscriber() throws MqttException {
        logger.debug("== START SUBSCRIBER == " + topic);
        connect().subscribe(topic);
    }

    public void unsubscriber() throws MqttException {
        if (client != null && client.isConnected()) {
            client.unsubscribe(topic);
        }
    }
}
