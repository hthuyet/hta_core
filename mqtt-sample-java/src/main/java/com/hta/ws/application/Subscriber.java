package com.hta.ws.application;

import com.hta.ws.common.Properties;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Subscriber {

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
        conOpt.setCleanSession(Properties.getCleanSession());

        memoryPersistence = new MemoryPersistence();
        clientId = MqttAsyncClient.generateClientId();

        logger.info("----------MQTTUrl: " + url);
        logger.info("----------MQTTUser: " + user);
        logger.info("----------MQTTPass: " + pass);
        logger.info("----------MQTTSub: " + topic);
        logger.info("----------clientId: " + clientId);
        init();
    }

    private MqttClient init() throws MqttException {
        if (client == null || !client.isConnected()) {
            logger.info("-------MqttClient init----");
            client = new MqttClient(url, clientId, memoryPersistence);
            client.setCallback(new CmdMqttCallBack());
            client.connect(conOpt);
        }
        return client;
    }

    public void subscriber() throws MqttException {
        logger.debug("== START SUBSCRIBER == " + topic);
        init().subscribe(topic);
    }

    public void stop() throws MqttException {
        init().unsubscribe(topic);
    }
}
