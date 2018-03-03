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
import java.util.List;
import org.apache.log4j.Logger;
import com.hta.ws.common.Properties;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author ThuyetLV
 */
public class ProcessIrriStop extends ProcessThreadMX {

    private static final Logger logger = Logger.getLogger(ProcessIrriStop.class.getSimpleName());
    long sleepTime = 500;

    static ProcessIrriStop _instance;

    public static synchronized ProcessIrriStop getInstance() throws Exception {
        if (_instance == null) {
            _instance = new ProcessIrriStop(ProcessIrriStop.class.getSimpleName());
        }
        return _instance;
    }

    DbProcess dbProcess;
    int maxRow;
    String listTypeSendOff;

    ProcessIrriStop(String threadName) throws Exception {
        super(threadName);
        dbProcess = new DbProcess();
        maxRow = 100;
        listTypeSendOff = Properties.getListTypeSendOff();
    }

    @Override
    protected void process() {
        try {
            Thread.sleep(sleepTime);
            if (ProcessSendCommandMgr.getInstance().check2Start()) {
                //Lay ban ghi Irr de start
                List listRecord = dbProcess.getIrriToStop(maxRow);
                if (listRecord != null && !listRecord.isEmpty()) {
                    IrriObj obj;
                    List<CommandObj> listCmd = new ArrayList<CommandObj>(listRecord.size());
                    if (!StringUtils.isEmpty(listTypeSendOff)) {
                        for (Object object : listRecord) {
                            obj = (IrriObj) object;
                            obj.setIsStart(IrriObj.NOT_START);
                            obj.setCount(obj.getCount() + 1);
                            //Khong ghi log nen truyen -1
                            if (obj.getTopic().contains(listTypeSendOff)) {
                                listCmd.add(new CommandObj(obj.getDeviceId(), obj.getSerial(), obj.getTopic(), obj.getCommandOff(), -1, obj.getDescription()));
                            }
                        }
                    } else {
                        for (Object object : listRecord) {
                            obj = (IrriObj) object;
                            obj.setIsStart(IrriObj.NOT_START);
                            obj.setCount(obj.getCount() + 1);
                        }
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
