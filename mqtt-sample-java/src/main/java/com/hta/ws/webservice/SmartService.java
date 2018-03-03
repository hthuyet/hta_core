/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hta.ws.webservice;

import com.hta.ws.application.Publisher;
import com.hta.ws.command.CmdConfigMode;
import com.hta.ws.command.CmdControl;
import com.hta.ws.command.CmdReqState;
import com.hta.ws.command.Command;
import com.hta.ws.common.DeviceManager;
import com.hta.ws.common.WebServiceConfig;
import com.hta.ws.database.DbProcess;
import com.hta.ws.obj.Device;
import java.net.ConnectException;
import java.util.concurrent.TimeoutException;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author thuyetlv
 */
@WebService
public class SmartService {

    private final Logger logger = Logger.getLogger(SmartService.class);

    DbProcess dbProcess;

    public SmartService() {
        dbProcess = new DbProcess();
    }

    @WebMethod
    public int add(int a, int b) {
        return a + b;
    }

    //<editor-fold defaultstate="collapsed" desc="sendCommand">
    /**
     *
     * @param device: 123456
     * @param topic: HTAE1/123456/OUT
     * @param type: {1: cmd dinh ky, 2: cmd dieu khien, 3: cmd cau hinh che do
     * tung dau ra, 4: cmd cau hinh network, 5: cmd cau hinh id, 6: cmd request
     * trang thai}
     * @param typeHis: {1: CmdControl, 2: CmdScheSv, 3: CmdScheDevice, 4:
     * CmdIrriSche}
     * @param data: {"uid": "123456", "cmd": "2", "data": "1,1,2,2", "time":
     * ["100","200","0","0"]}
     * @return
     */
    @WebMethod
    public BasicResponse sendCommand(
            @WebParam(name = "id") Long deviceId,
            @WebParam(name = "device") String device,
            @WebParam(name = "topic") String topic,
            @WebParam(name = "type") String type,
            @WebParam(name = "typeHis") String typeHis,
            @WebParam(name = "data") String data,
            @WebParam(name = "description") String description) {
        BasicResponse result = new BasicResponse();
        try {
            logger.info("----------sendCommand: " + device + " data: " + data + " typeHis: " + typeHis + " description: " + description);
            Command cmd = null;
            switch (type) {
                case "2":
                    cmd = new CmdControl(deviceId, device, topic, data);
                    break;
                case "3":
                    cmd = new CmdConfigMode(device, topic, data);
                    break;
                case "6":
                    cmd = new CmdReqState(device, topic, data);
                    break;
                default:
                    cmd = new CmdControl(deviceId, device, topic, data);
                    break;
            }
            if (!StringUtils.isBlank(typeHis)) {
                cmd.setTypeHis(Integer.parseInt(typeHis));
            }
            cmd.setDescription(description);
            cmd.executeCommand();
            if (!cmd.errorCheck) {
                result.setErrorCode(WebServiceConfig.ErrorCode.SUCCESS);
                result.setMsg(WebServiceConfig.Message.SUCCESS);
                if (!StringUtils.isBlank(cmd.msg)) {
                    result.setData(cmd.msg);
                }
                //Gui lai lenh lay trang thai tbi de cap nhat luon cho dung
                if (cmd instanceof CmdControl) {
                    //
                    Publisher.getInstace().publish(device, topic, CmdReqState.createCommand(device), -1, "");
                }
                logger.debug("sendCommand return successed!");
            } else {
                result.setErrorCode(cmd.getResultCode());
                if (!StringUtils.isBlank(cmd.msg)) {
                    logger.info("sendCommand return Error: " + cmd.msg);
                    result.setMsg(cmd.msg);
                } else {
                    logger.info("sendCommand return Error!");
                    result.setMsg(WebServiceConfig.Message.Error);
                }
            }
        } catch (Exception ex) {
            if (ex instanceof ConnectException) {
                result.setErrorCode(WebServiceConfig.ErrorCode.CONNECTION_ERROR);
                logger.warn("sendCommand got exception", ex);
            } else if (ex instanceof TimeoutException) {
                result.setErrorCode(WebServiceConfig.ErrorCode.TIMEOUT_ERROR);
                logger.warn("sendCommand got timeout exception");
            } else {
                result.setErrorCode(WebServiceConfig.ErrorCode.CONFIGURATION_ERROR);
                logger.warn("sendCommand got exception", ex);
            }
            result.setMsg(ex.getMessage());
        }

        return result;
    }//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="deleteDevice">
    @WebMethod
    public BasicResponse deleteDevice(
            @WebParam(name = "id") Long id,
            @WebParam(name = "code") String code) {
        BasicResponse result = new BasicResponse();
        try {
            logger.info("----------deleteDevice: " + code);
            Device device = DeviceManager.getInstance().getDevice(code);
            if (device != null) {
                boolean delete = DeviceManager.getInstance().removeDevice(code);
                if (delete) {
                    //Delete success
                    dbProcess.deleteDevice(code);
                    result.setErrorCode(WebServiceConfig.ErrorCode.SUCCESS);
                    result.setMsg(WebServiceConfig.Message.SUCCESS);
                } else {
                    logger.warn(String.format("Delete %s fail from redis.", code));
                    result.setErrorCode(WebServiceConfig.ErrorCode.SYSTEM_FAIL);
                    result.setMsg(WebServiceConfig.Message.FAILURE);
                }
            } else {
                //Chi ton tai tren db
                dbProcess.deleteDevice(code);
                result.setErrorCode(WebServiceConfig.ErrorCode.SUCCESS);
                result.setMsg(WebServiceConfig.Message.SUCCESS);
            }
        } catch (Exception ex) {
            if (ex instanceof ConnectException) {
                result.setErrorCode(WebServiceConfig.ErrorCode.CONNECTION_ERROR);
                logger.warn("deleteDevice got exception", ex);
            } else if (ex instanceof TimeoutException) {
                result.setErrorCode(WebServiceConfig.ErrorCode.TIMEOUT_ERROR);
                logger.warn("deleteDevice got timeout exception");
            } else {
                result.setErrorCode(WebServiceConfig.ErrorCode.CONFIGURATION_ERROR);
                logger.warn("deleteDevice got exception", ex);
            }
            result.setMsg(ex.getMessage());
        }

        return result;
    }//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="addDevice">
    @WebMethod
    public BasicResponse addDevice(
            @WebParam(name = "id") Long id,
            @WebParam(name = "code") String code) {
        BasicResponse result = new BasicResponse();
        try {
            logger.info("----------addDevice: " + id + " - " + code);
            Device device = DeviceManager.getInstance().getDevice(code);
            if (device == null) {
                device = new Device();
                device.setId(id);
                device.setCode(code);
                device.setState(Device.STATE_OFFLINE);
                boolean add = DeviceManager.getInstance().addDevice(code, device);
                if (add) {
                    result.setErrorCode(WebServiceConfig.ErrorCode.SUCCESS);
                    result.setMsg(WebServiceConfig.Message.SUCCESS);
                } else {
                    logger.warn(String.format("Add %s fail from redis.", code));
                    result.setErrorCode(WebServiceConfig.ErrorCode.SYSTEM_FAIL);
                    result.setMsg(WebServiceConfig.Message.FAILURE);
                }
            } else {
                result.setErrorCode(WebServiceConfig.ErrorCode.SUCCESS);
                result.setMsg(WebServiceConfig.Message.SUCCESS);
            }
        } catch (Exception ex) {
            if (ex instanceof ConnectException) {
                result.setErrorCode(WebServiceConfig.ErrorCode.CONNECTION_ERROR);
                logger.warn("addDevice got exception", ex);
            } else if (ex instanceof TimeoutException) {
                result.setErrorCode(WebServiceConfig.ErrorCode.TIMEOUT_ERROR);
                logger.warn("addDevice got timeout exception");
            } else {
                result.setErrorCode(WebServiceConfig.ErrorCode.CONFIGURATION_ERROR);
                logger.warn("addDevice got exception", ex);
            }
            result.setMsg(ex.getMessage());
        }

        return result;
    }//</editor-fold>
}
