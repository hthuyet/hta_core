package com.hta.ws.common;

/**
 * Define contanst, error code and message
 *
 * @author ThuyetLV
 */
public class WebServiceConfig {

    public static final String WS_NAME = "SmartagriWebservice";
    public static final String WS_SERVICE_NAME = "SmartagriWebservice";
    public static final String WS_TARGET_NAMESPACE = "http://app.htaviet.com";

    public static class ErrorCode {

        public static final int SUCCESS = 0;
        public static final int SYSTEM_FAIL = -1;
        public static final int TIMEOUT_ERROR = 1;
        public static final int CONFIGURATION_ERROR = 2;
        public static final int AUTHENTICATION_FAIL = 3;
        public static final int CONNECTION_ERROR = 4;
        public static final int NO_UPDATE = 5;
        public static final int ALREADY_CMD = 6;
        public static final int NO_RUN = 7;
        public static final int PARAM_INVALID = 8;
        public static final int LIST_EMPTY = 9;
    }

    public static class Message {

        public static final String SUCCESS = "Success";
        public static final String FAILURE = "Failure";
        public static final String Error = "Error";
        public static final String NO_ALARM = "NoAlarm";
        public static final String NO_UPDATE = "NoUpdate";
        public static final String ALREADY_CMD = "AlreadyCmd";
        public static final String NO_RUN = "NoRun";

    }
}
