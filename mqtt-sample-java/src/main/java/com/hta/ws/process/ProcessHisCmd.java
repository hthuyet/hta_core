/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hta.ws.process;

import com.hta.ws.database.DbProcess;
import com.hta.ws.mongo.ConnectToMongo;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
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
public class ProcessHisCmd extends ProcessThreadMX {

    private static final Logger logger = Logger.getLogger(ProcessHisCmd.class.getSimpleName());
    long sleepTime = 100;
    int capacity = 100;

    static ProcessHisCmd _instance;

    public static synchronized ProcessHisCmd getInstance() throws Exception {
        if (_instance == null) {
            _instance = new ProcessHisCmd(ProcessHisCmd.class.getSimpleName());
        }
        return _instance;
    }

    DbProcess dbProcess;
    BlockingQueue<Document> queue;

    public ProcessHisCmd(String threadName) throws Exception {
        super(threadName);
        dbProcess = new DbProcess();
        queue = new LinkedBlockingQueue<Document>();
    }

    public void addAll(List<Document> lstCmd) {
        queue.addAll(lstCmd);
    }

    public void add(Document document) {
        queue.add(document);
    }

    public int getSize() {
        return queue.size();
    }

    @Override
    protected void process() {
        try {
            Thread.sleep(sleepTime);
            if (queue != null && queue.size() > 0) {
                List<Document> listRecord = new ArrayList<Document>(capacity);
                queue.drainTo(listRecord, capacity);
                
                List listWrite = new ArrayList<>(listRecord.size());
                InsertOneModel insertOneModel;
                for (Document record : listRecord) {
                    insertOneModel = new InsertOneModel(record);
                    listWrite.add(insertOneModel);
                }                
                ConnectToMongo.getInstace().insertHis(listWrite);
            }
        } catch (Exception ex) {
            logger.error("ERROR process: ", ex);
        }
    }
}
