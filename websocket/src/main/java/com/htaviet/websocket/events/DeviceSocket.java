/*
 * Copyright 2014 VNPT-Technology. All rights reserved.
 * VNPT-Technology PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.htaviet.websocket.events;

import com.google.gson.Gson;
import com.htaviet.websocket.obj.SessionInfo;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.apache.log4j.Logger;

/**
 *
 * @author ThuyetLV
 */
@ClientEndpoint
@ServerEndpoint(value = "/deviceState/")
public class DeviceSocket {

    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());
    protected static final Logger logger = Logger.getLogger(DeviceSocket.class);
    static HashMap<String, String> hsmSession = new HashMap<String, String>();

    public static Set<Session> getSessions() {
        return sessions;
    }

    @OnOpen
    public void onWebSocketConnect(Session session) {
        session.setMaxIdleTimeout(Long.MAX_VALUE); //A value that is 0 or negative indicates the session will never timeout due to inactivity.
        sessions.add(session);
        Set<Session> s = session.getOpenSessions();
        for (Session s1 : s) {
            logger.debug("------getOpenSessions: " + s1.getId());
        }
        logger.info("Add to device state queue: " + session.getId());
        logger.info("Sessions device state size: " + getSessions().size());
        try {
            session.getBasicRemote().sendText(md5Id(session.getId()));
            hsmSession.put(md5Id(session.getId()), "");
        } catch (IOException ex) {
            logger.error("ERROR onWebSocketConnect sendText: ", ex);
        }
    }

    @OnMessage
    public void onWebSocketText(String message) {
        logger.info("Received TEXT message: " + message);
        logger.info("Sessions size: " + getSessions().size());
        if (message.contains("id") && message.contains("lstDevice")) {
            Gson gson = new Gson();
            SessionInfo sessionInfo = gson.fromJson(message, SessionInfo.class);
            hsmSession.put(sessionInfo.getId(), "," + sessionInfo.getLstDevice()+ ",");
        }
    }

    @OnClose
    public void onWebSocketClose(Session session) {
        hsmSession.remove(md5Id(session.getId()));
        sessions.remove(session);
        logger.info("Socket device state Closed: " + session);
        logger.info("Remove device state from queue");
        logger.info("Sessions device state size: " + getSessions().size());
    }

    @OnError
    public void onWebSocketError(Throwable cause) {
        cause.printStackTrace(System.err);
        logger.warn("onWebSocketError: ", cause);
        logger.info("Sessions device state size: " + getSessions().size());
    }

    public HashMap<String, String> getHsmSession() {
        return hsmSession;
    }

    public static boolean checkToSend(String sessionId, String device) {
        String sessionArea = hsmSession.get(md5Id(sessionId));
        if (sessionArea == null) {
            return false;
        }
        return sessionArea.contains("," + device + ",");
    }

    private static String md5Id(String input) {
        try {
            byte[] bytesOfMessage = input.getBytes("UTF-8");

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] byteData = md.digest(bytesOfMessage);

            return Base64.getEncoder().encodeToString(byteData);
        } catch (Exception ex) {
            logger.error("ERROR md5Id: ", ex);
            return null;
        }
    }
}
