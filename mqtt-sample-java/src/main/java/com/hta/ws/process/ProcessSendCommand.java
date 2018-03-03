/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hta.ws.process;

import com.hta.ws.application.Publisher;
import com.hta.ws.database.DbProcess;
import com.hta.ws.obj.CommandObj;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;
import org.bson.Document;

/**
 *
 * @author ThuyetLV
 */
public class ProcessSendCommand extends ProcessThreadMX {

    private static final Logger logger = Logger.getLogger(ProcessSendCommand.class.getSimpleName());
    long sleepTime = 100;
    int capacity = 100;

    static ProcessSendCommand _instance;

    public static synchronized ProcessSendCommand getInstance() throws Exception {
        if (_instance == null) {
            _instance = new ProcessSendCommand(ProcessSendCommand.class.getSimpleName());
        }
        return _instance;
    }

    DbProcess dbProcess;
    BlockingQueue<CommandObj> queue;

    public ProcessSendCommand(String threadName) throws Exception {
        super(threadName);
        dbProcess = new DbProcess();
        queue = new LinkedBlockingQueue<CommandObj>();
    }

    public void addAll(List<CommandObj> lstCmd) {
        queue.addAll(lstCmd);
    }

    public int getSize() {
        return queue.size();
    }

    @Override
    protected void process() {
        try {
            Thread.sleep(sleepTime);
            if (queue != null && queue.size() > 0) {
                List<CommandObj> listRecord = new ArrayList<CommandObj>(capacity);
                List<Document> listHis = new ArrayList<Document>(capacity);
                queue.drainTo(listRecord, capacity);
                Document history;
                for (CommandObj commandObj : listRecord) {
                    history = Publisher.getInstace().publish(commandObj.getDevice(), commandObj.getTopic(), commandObj.getData(), commandObj.getType(), commandObj.getDescription());
                    if (history != null) {
                        listHis.add(history);
                        if (commandObj.getType() == CommandObj.TYPE_CONTROL
                                || commandObj.getType() == CommandObj.TYPE_SCHE_SV
                                || commandObj.getType() == CommandObj.TYPE_IRR) {
                            commandObj.insertListIrriHis();
                        }
                    }
                }
                ProcessHisCmd.getInstance().addAll(listHis);
            }
        } catch (Exception ex) {
            logger.error("ERROR process: ", ex);
        }
    }
}
