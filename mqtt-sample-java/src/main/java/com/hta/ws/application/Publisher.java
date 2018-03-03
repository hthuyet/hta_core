package com.hta.ws.application;

import com.hta.ws.common.Properties;
import java.util.Date;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Publisher {

    private final Logger logger = Logger.getLogger(Publisher.class);

    static Publisher _instance;

    public synchronized static Publisher getInstace() throws MqttException {
        if (_instance == null) {
            _instance = new Publisher();
        }
        return _instance;
    }

    private String user;
    private String pass;
    private String url;

    MqttConnectOptions conOpt;
    MqttClient client;

    Publisher() throws MqttException {
        url = Properties.getMQTTUrl();
        user = Properties.getMQTTUser();
        pass = Properties.getMQTTPass();

        conOpt = new MqttConnectOptions();
        if (pass != null) {
            conOpt.setPassword(pass.toCharArray());
        }
        if (user != null) {
            conOpt.setUserName(user);
        }
        conOpt.setCleanSession(Properties.getCleanSession());

        logger.info("----------MQTTUrl: " + url);
        logger.info("----------MQTTUser: " + user);
        logger.info("----------MQTTPass: " + pass);

        init();
    }

    private void init() throws MqttException {
        if (client == null || !client.isConnected()) {
            client = new MqttClient(url, MqttClient.generateClientId());
            client.connect(conOpt);
        }
    }

    public synchronized void disconnect() throws MqttException {
        if (client == null || !client.isConnected()) {
            client.disconnect();
            client = null;
        }
    }

    public void publish2(String topic, String messageString) throws MqttException, Exception {
        logger.debug("== START publish2 ==");
        init();
        MqttMessage message = new MqttMessage();
        message.setPayload(messageString.getBytes());
        if (client == null) {
            throw new Exception("Client is null.");
        }
        logger.info("--topic: " + topic);
        logger.info("--message: " + message);
        client.publish(topic, message);
    }

    public Document publish(String device, String topic, String messageString, int type, String description) throws MqttException, Exception {
        logger.debug("== START PUBLISHER ==");
        init();
        MqttMessage message = new MqttMessage();
        message.setPayload(messageString.getBytes());
        if (client == null) {
            throw new Exception("Client is null.");
        }
        logger.info("--topic: " + topic);
        logger.info("--message: " + message);
        client.publish(topic, message);

        logger.debug("\tMessage '" + messageString + "' to " + topic);
        logger.debug("== END PUBLISHER ==");
        return createHistoryObj(device, messageString, type, description);
    }

    public static void main(String[] args) {
        Date now = new Date();
        now.setHours(now.getHours() + 9);
        System.out.println("" + now);
    }

    public Document createHistoryObj(String code, String command, int type, String description) {
        if (type > 0) {
            Document document = new Document();
            document.put("code", code);
            document.put("command", command);
            document.put("type", type);
            Date now = new Date();
            now.setHours(now.getHours() + 7);
            document.put("created_at", now);
            document.put("description", description);
            return document;
        }
        return null;
    }

    public Document createIrrHistory(Long area_id, Long device_id, Long device_port_id,
            String code, String port, int dautuoi, int goc, float luongnuoc, String port_name, Date start_time, Date end_time, int time, int type) {
        if (type > 0) {
            Document document = new Document();
            document.put("area_id", area_id);
            document.put("device_id", device_id);
            document.put("code", code);
            document.put("device_port_id", device_port_id);
            document.put("port", port);
            document.put("dautuoi", dautuoi);
            document.put("goc", goc);
            document.put("luongnuoc", luongnuoc);
            document.put("port_name", port_name);
            document.put("start_time", start_time);
            document.put("end_time", end_time);
            document.put("time", time);
            document.put("type", type);
            document.put("created_at", new Date());
            return document;
        }
        return null;
    }
}
