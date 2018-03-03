package com.hta.ws.application;

import com.hta.ws.common.Properties;
import com.hta.ws.webservice.SmartService;
import javax.xml.ws.Endpoint;
import org.apache.log4j.Logger;

/**
 *
 * @author ThuyetLV
 */
public class WebServicePublisher {

    protected static final Logger logger = Logger.getLogger(WebServicePublisher.class);

    public static void publishWebservice() {
        String serviceAddress = Properties.getWsAddress();

        try {
            Endpoint.publish(serviceAddress, new SmartService());
            logger.info("Smartagri Webservice Address: " + serviceAddress);
        } catch (Exception ex) {
            logger.error("Can't publish service address: " + serviceAddress, ex);
        }
    }
}
