/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hta.ws.process;

import com.hta.ws.database.DbProcess;
import com.hta.ws.obj.CommandObj;
import com.hta.ws.obj.ScheduleObj;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author ThuyetLV
 */
public class ProcessSchedule extends ProcessThreadMX {

    private static final Logger logger = Logger.getLogger(ProcessSchedule.class.getSimpleName());
    long sleepTime = 500;

    static ProcessSchedule _instance;

    public static synchronized ProcessSchedule getInstance() throws Exception {
        if (_instance == null) {
            _instance = new ProcessSchedule(ProcessSchedule.class.getSimpleName());
        }
        return _instance;
    }

    DbProcess dbProcess;

    ProcessSchedule(String threadName) throws Exception {
        super(threadName);
        dbProcess = new DbProcess();
    }

    @Override
    protected void process() {
        try {
            Thread.sleep(100);
            if (ProcessSendCommandMgr.getInstance().check2Start()) {
                List lstSchedule = dbProcess.getSchedule(100);
                if (lstSchedule != null && !lstSchedule.isEmpty()) {
                    ScheduleObj scheduleObj;
                    List<CommandObj> listCmd = new ArrayList<CommandObj>(lstSchedule.size());
                    for (Object object : lstSchedule) {
                        scheduleObj = (ScheduleObj) object;
                        logger.debug("----------Begin schedule : " + scheduleObj.getId() + " ------------------");
                        scheduleObj.setIsStart(ScheduleObj.STATUS_RUNNING);
                        if (ScheduleObj.TYPE_SCHEDULE == scheduleObj.getType()) {
                            listCmd.add(new CommandObj(scheduleObj.getDeviceId(), scheduleObj.getSerial(), scheduleObj.getTopic(), scheduleObj.getCommand(), CommandObj.TYPE_SCHE_SV, scheduleObj.getDescription()));
                        } else {
                            listCmd.add(new CommandObj(scheduleObj.getDeviceId(), scheduleObj.getSerial(), scheduleObj.getTopic(), scheduleObj.getCommand(), CommandObj.TYPE_CONTROL, scheduleObj.getDescription()));
                        }
                    }
                    int[] update = dbProcess.updateSchedule(lstSchedule);
                    if (update == null) {
                        logger.error("ERROR updateSchedule....");
                        Thread.sleep(1000);
                        return;
                    }

                    boolean add = true;
                    //Add to Process to send command
                    if (!listCmd.isEmpty()) {
                        add = ProcessSendCommandMgr.getInstance().addCmd(listCmd);
                    }
                    if (add) {
                        for (Object object : lstSchedule) {
                            scheduleObj = (ScheduleObj) object;
                            scheduleObj.setIsStart(ScheduleObj.STATUS_DONE);
                            if (scheduleObj.getStartTime() != null) {
                                scheduleObj.setStartTime(new Date(scheduleObj.getStartTime().getTime() + (1000 * 60 * 60 * 24)));
                            }

                            if (ScheduleObj.TYPE_SCHEDULE == scheduleObj.getType()) {
                                scheduleObj.setCount(scheduleObj.getCount() + 1);
                            }
                        }
                        dbProcess.updateSchedule(lstSchedule);
                    }

                    //@TODO: Schedule da xong thi chuyen sang schedule_history
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
