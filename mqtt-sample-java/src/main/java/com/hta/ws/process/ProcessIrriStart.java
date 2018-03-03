/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hta.ws.process;

import com.hta.ws.database.DbProcess;
import com.hta.ws.obj.CommandObj;
import com.hta.ws.obj.IrriObj;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author ThuyetLV
 */
public class ProcessIrriStart extends ProcessThreadMX {

    private static final Logger logger = Logger.getLogger(ProcessIrriStart.class.getSimpleName());
    long sleepTime = 500;

    static ProcessIrriStart _instance;

    public static synchronized ProcessIrriStart getInstance() throws Exception {
        if (_instance == null) {
            _instance = new ProcessIrriStart(ProcessIrriStart.class.getSimpleName());
        }
        return _instance;
    }

    DbProcess dbProcess;
    int maxRow;

    ProcessIrriStart(String threadName) throws Exception {
        super(threadName);
        dbProcess = new DbProcess();
        maxRow = 100;
    }

    int lastMove = -1;

    private void moveToHistory() {
        //1h chay 1 lan
        Date now = new Date();
        if (now.getMinutes() <= 0) {
            if (lastMove <= 0) {
                dbProcess.moveIrriToHis();
                lastMove = 1;
            }
        } else {
            lastMove = -1;
        }
    }

    @Override
    protected void process() {
        try {
            Thread.sleep(sleepTime);
            if (ProcessSendCommandMgr.getInstance().check2Start()) {
                moveToHistory();
                //Lay ban ghi Irr de start
                List listRecord = dbProcess.getIrriToStart(maxRow);
                if (listRecord != null && !listRecord.isEmpty()) {
                    IrriObj obj;
                    List<CommandObj> listCmd = new ArrayList<CommandObj>(listRecord.size());
                    for (Object object : listRecord) {
                        obj = (IrriObj) object;
                        obj.setIsStart(IrriObj.START);
                        listCmd.add(new CommandObj(obj.getDeviceId(), obj.getSerial(), obj.getTopic(), obj.getCommand(), CommandObj.TYPE_IRR, obj.getDescription()));
                    }

                    //Add to Process to send command
                    boolean add = ProcessSendCommandMgr.getInstance().addCmd(listCmd);
                    if (add) {
                        //Send command thanh cong -> cap nhat status start cho irri
                        int[] update = dbProcess.updateIrri(listRecord);
                        if (update == null) {
                            logger.error("ERROR updateIrri....");
                            Thread.sleep(1000);
                            return;
                        }
                    }
                }
            } else {
                logger.warn("-------ProcessSendCommandMgr NOT AVAIABLT-----");
                Thread.sleep(1000);
            }
        } catch (Exception ex) {
            logger.error("ERROR process: ", ex);
        }
    }
}
