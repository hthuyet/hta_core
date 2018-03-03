/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hta.ws.process;

import com.hta.ws.common.Properties;
import com.hta.ws.obj.CommandObj;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author ThuyetLV
 */
public class ProcessSendCommandMgr {

    private static final Logger logger = Logger.getLogger(ProcessSendCommandMgr.class.getSimpleName());
    long sleepTime = 500;

    static ProcessSendCommandMgr _instance;

    public static synchronized ProcessSendCommandMgr getInstance() throws Exception {
        if (_instance == null) {
            _instance = new ProcessSendCommandMgr(ProcessSendCommandMgr.class.getSimpleName());
        }
        return _instance;
    }

    int numberOfThread;
    List<ProcessSendCommand> lstProcessSendCommands;

    ProcessSendCommandMgr(String threadName) throws Exception {
        minCmd = Properties.getMinCmd();
        maxCmd = Properties.getMaxCmd();
        numberOfThread = Properties.getNumberThreadSendCmd();

        //Set default
        minCmd = (minCmd <= 0) ? 1 : minCmd;
        maxCmd = (maxCmd <= 0) ? 10 : maxCmd;
        numberOfThread = (numberOfThread <= 0) ? 1 : numberOfThread;

        logger.debug("---------------ProcessSendCommandMgr-------------");
        logger.debug("---------------ProcessSendCommandMgr minCmd: " + minCmd);
        logger.debug("---------------ProcessSendCommandMgr maxCmd: " + maxCmd);
        logger.debug("---------------ProcessSendCommandMgr numberOfThread: " + numberOfThread);
        logger.debug("---------------END ProcessSendCommandMgr-------------");

        lstProcessSendCommands = new ArrayList<>(numberOfThread);
        for (int i = 0; i < numberOfThread; i++) {
            ProcessSendCommand psc = new ProcessSendCommand(ProcessSendCommand.class.getSimpleName() + "_" + i);
            lstProcessSendCommands.add(psc);
        }
    }

    //Other
    int minCmd;
    int maxCmd;

    /**
     *
     * @return true: ok cho chay, false: wait
     */
    public boolean check2Start() {
        boolean rtn = false;
        for (ProcessSendCommand psc : lstProcessSendCommands) {
            if (psc.getSize() < maxCmd) {
                rtn = true;
                break;
            }
        }
        return rtn;
    }

    public boolean addCmd(List<CommandObj> lstCmd) {
        //Tim process co it queue nhat de gui
        int minSize = maxCmd;
        int size = 0;
        ProcessSendCommand process = null;
        for (ProcessSendCommand psc : lstProcessSendCommands) {
            size = psc.getSize();
            if (size < maxCmd) {
                if (size < minSize) {
                    minSize = size;
                    process = psc;
                }
            }
        }
        if (process != null) {
            process.addAll(lstCmd);
            return true;
        }
        logger.warn("-----addCmd to Process false ");
        return false;
    }

    public void start() {
        try {
            for (ProcessSendCommand psc : lstProcessSendCommands) {
                psc.start();
            }
            logger.info("ProcessSendCommand " + numberOfThread + " started...");
        } catch (Exception ex) {
            logger.error("ERROR start: ", ex);
        }
    }

    public void stop() {
        try {
            int i = 0;
            for (ProcessSendCommand psc : lstProcessSendCommands) {
                try {
                    psc.stop();
                } catch (Exception ex) {
                    logger.error("ERROR stop ProcessSendCommand: " + ProcessSendCommand.class.getSimpleName() + "_" + i, ex);
                }
                i++;
            }
            logger.info("ProcessSendCommand " + numberOfThread + " stop...");
        } catch (Exception ex) {
            logger.error("ERROR stop: ", ex);
        }
    }
}
