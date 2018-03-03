/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hta.ws.common;

import com.hta.ws.database.DbProcess;
import com.hta.ws.mongo.ConnectToMongo;
import com.hta.ws.obj.CommandObj;
import static com.hta.ws.obj.CommandObj.CMD_CONTROL;
import com.hta.ws.obj.DevicePortObj;
import com.hta.ws.obj.IrriHisObj;
import com.mongodb.client.model.InsertOneModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author thuyetlv
 */
public class Ultils {

    private final Logger logger = Logger.getLogger(Ultils.class);
    static Ultils _instance;
    DbProcess dbProcess;

    public static synchronized Ultils getInstance() {
        if (_instance == null) {
            _instance = new Ultils();
        }
        return _instance;
    }

    public Ultils() {
        this.dbProcess = new DbProcess();
    }

    public Boolean insertListIrriHis(Long deviceId, String data, int type) {
        try {
            List<Document> listRecord = createDocIrriHis(deviceId, data, type);
            if (listRecord != null && !listRecord.isEmpty()) {
                List listWrite = new ArrayList<>();
                InsertOneModel insertOneModel;
                for (Document record : listRecord) {
                    insertOneModel = new InsertOneModel(record);
                    listWrite.add(insertOneModel);
                }
                ConnectToMongo.getInstace().insertHisIrr(listWrite);
            }
            return true;
        } catch (Exception ex) {
            logger.error("ERROR insertListIrriHis: ", ex);
            return null;
        }
    }

    private boolean checkDevicePortInfo(DevicePortObj devicePortObj) {
        if (devicePortObj == null) {
            return false;
        } else if (devicePortObj.getAreaId() <= 0 || devicePortObj.getDautuoi() <= 0
                || devicePortObj.getGoc() <= 0 || devicePortObj.getLuongnuoc() <= 0) {
            return false;
        }
        return true;
    }

    public static double rounddb(double value, int numberOfDigitsAfterDecimalPoint) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(numberOfDigitsAfterDecimalPoint,
                BigDecimal.ROUND_HALF_UP);
        return bigDecimal.doubleValue();
    }

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    public static float round2(float number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++) {
            pow *= 10;
        }
        float tmp = number * pow;
        return ((float) ((int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp))) / pow;
    }

    public static float round3(float value, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++) {
            pow *= 10;
        }
        float tmp = value * pow;
        float tmpSub = tmp - (int) tmp;

        return ((float) ((int) (value >= 0
                ? (tmpSub >= 0.5f ? tmp + 1 : tmp)
                : (tmpSub >= -0.5f ? tmp : tmp - 1)))) / pow;

    // Below will only handles +ve values
        // return ( (float) ( (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) ) ) / pow;
    }

    public List<Document> createDocIrriHis(Long deviceId, String data, int type) {
        JSONObject jsonObj = new JSONObject(data);
        int cmd = (jsonObj.getString("cmd") == null) ? 0 : jsonObj.getInt("cmd");
        if (CMD_CONTROL == cmd) {
            //Lay thong tin port, luong nuoc, area tu db de luu lich su
            HashMap hsmPort = dbProcess.getDevicePort(deviceId);
            //Hien tai chi quan ly luong nuoc tuoi voi command cmd = 2
            List<Document> rtn = new ArrayList<>();
            String ports = jsonObj.getString("data");
            String[] tmpPort = ports.split(",");
            JSONArray tmpTime = jsonObj.getJSONArray("time");

            logger.info("----tmpTime: " + String.valueOf(tmpTime.get(0)));
            String[] arrTime = String.valueOf(tmpTime.get(0)).split(",");
            int time = 0;
            if (tmpPort != null && tmpPort.length > 0) {
                Document document = null;
                DevicePortObj devicePortObj = null;
                int i = 0;
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                cal.add(Calendar.HOUR, 7);

                Calendar toCal = Calendar.getInstance();
                toCal.setTimeInMillis(cal.getTimeInMillis());

                for (String port : tmpPort) {
                    if (CommandObj.CTRL_ON.equalsIgnoreCase(port)) {
                        devicePortObj = (DevicePortObj) hsmPort.get(i);
                        if (checkDevicePortInfo(devicePortObj)) {
                            try {
                                time = Integer.parseInt(String.valueOf(tmpTime.get(i)));
                            } catch (JSONException | NumberFormatException ex) {
                                time = Integer.parseInt(arrTime[i]);
                                logger.error("ERROR get time: " + String.valueOf(tmpTime.get(i)), ex);
                            }
                            toCal.setTimeInMillis(cal.getTimeInMillis());
                            document = new Document();
                            document.put(IrriHisObj.DEVICE_ID, deviceId);
                            document.put(IrriHisObj.AREA_ID, devicePortObj.getAreaId());
                            document.put(IrriHisObj.PORT, Integer.parseInt(port));
                            document.put(IrriHisObj.DAU_TUOI, devicePortObj.getDautuoi());
                            document.put(IrriHisObj.GOC, devicePortObj.getGoc());
                            document.put(IrriHisObj.LUONG_NUOC, devicePortObj.getLuongnuoc());
                            document.put(IrriHisObj.PORT_NAME, devicePortObj.getPortName());
                            document.put(IrriHisObj.TIME, time);
                            document.put(IrriHisObj.TONG, rounddb((devicePortObj.getLuongnuoc() * time * devicePortObj.getDautuoi()) / 3600, 2));
                            document.put(IrriHisObj.START_TIME, cal.getTime());
                            toCal.add(Calendar.SECOND, time);
                            document.put(IrriHisObj.END_TIME, toCal.getTime());
                            //@TODO
                            if (CommandObj.CMD_CONTROL == type) {
                                document.put(IrriHisObj.TYPE, IrriHisObj.TYPE_CTRL);
                            } else if (CommandObj.TYPE_SCHE_SV == type) {
                                document.put(IrriHisObj.TYPE, IrriHisObj.TYPE_SCHE_SV);
                            } else if (CommandObj.TYPE_IRR == type) {
                                document.put(IrriHisObj.TYPE, IrriHisObj.TYPE_IRR);
                            }

                            rtn.add(document);
                        }
                    }
                    i++;
                }
            }
            return rtn;
        }
        return null;
    }

//    public List<IrriHisObj> createListIrriHis(Long deviceId, String data, int type) {
//        JSONObject jsonObj = new JSONObject(data);
//        int cmd = (jsonObj.getString("cmd") == null) ? 0 : jsonObj.getInt("cmd");
//        if (CMD_CONTROL == cmd) {
//            //Lay thong tin port, luong nuoc, area tu db de luu lich su
//            HashMap hsmPort = dbProcess.getDevicePort(deviceId);
//            //Hien tai chi quan ly luong nuoc tuoi voi command cmd = 2
//            List<IrriHisObj> rtn = new ArrayList<>();
//            String ports = jsonObj.getString("data");
//            String[] tmpPort = ports.split(",");
//            JSONArray tmpTime = jsonObj.getJSONArray("time");
//            if (tmpPort != null && tmpPort.length > 0) {
//                IrriHisObj irriHisObj = null;
//                DevicePortObj devicePortObj = null;
//                int i = 0;
//                Calendar cal = Calendar.getInstance();
//                cal.setTimeInMillis(System.currentTimeMillis());
//
//                Calendar toCal = Calendar.getInstance();
//                toCal.setTimeInMillis(cal.getTimeInMillis());
//
//                for (String port : tmpPort) {
//                    devicePortObj = (DevicePortObj) hsmPort.get(Integer.parseInt(port));
//                    if (checkDevicePortInfo(devicePortObj)) {
//                        toCal.setTimeInMillis(cal.getTimeInMillis());
//                        irriHisObj = new IrriHisObj();
//                        irriHisObj.setDeviceId(deviceId);
//                        irriHisObj.setAreaId(devicePortObj.getAreaId());
//                        irriHisObj.setPort(Integer.parseInt(port));
//                        irriHisObj.setDautuoi(devicePortObj.getDautuoi());
//                        irriHisObj.setGoc(devicePortObj.getGoc());
//                        irriHisObj.setLuongnuoc(devicePortObj.getLuongnuoc());
//                        irriHisObj.setPortName(devicePortObj.getPortName());
//                        irriHisObj.setTime(Integer.parseInt(String.valueOf(tmpTime.get(i))));
//                        irriHisObj.setStartSime(cal.getTime());
//                        toCal.add(Calendar.SECOND, irriHisObj.getTime());
//                        irriHisObj.setEndTime(toCal.getTime());
//                        //@TODO
//                        if (CommandObj.CMD_CONTROL == type) {
//                            irriHisObj.setType(IrriHisObj.TYPE_CTRL);
//                        } else if (CommandObj.TYPE_SCHE_SV == type) {
//                            irriHisObj.setType(IrriHisObj.TYPE_SCHE_SV);
//                        } else if (CommandObj.TYPE_IRR == type) {
//                            irriHisObj.setType(IrriHisObj.TYPE_IRR);
//                        }
//
//                        rtn.add(irriHisObj);
//                    }
//                    i++;
//                }
//            }
//            return rtn;
//        }
//        return null;
//    }
}
