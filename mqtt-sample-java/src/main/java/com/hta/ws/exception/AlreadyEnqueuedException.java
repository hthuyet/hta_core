/*
 * Copyright 2014 VNPT-Technology. All rights reserved.
 * VNPT-Technology PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.hta.ws.exception;

/**
 * AlreadyEnqueuedException: Only one request at a time
 *
 * @author Vunb
 * @date Oct 21, 2014
 * @update Oct 21, 2014
 */
public class AlreadyEnqueuedException extends Exception {

    public AlreadyEnqueuedException() {
    }

    public AlreadyEnqueuedException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        if (super.getMessage() == null
                || super.getMessage().length() == 0) {
            return "Already enqueued";
        }
        return super.getMessage();
    }
}
