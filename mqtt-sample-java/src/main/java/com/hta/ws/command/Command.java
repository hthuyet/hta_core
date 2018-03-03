package com.hta.ws.command;

import com.hta.ws.common.Properties;
import com.hta.ws.exception.AlreadyEnqueuedException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.bson.Document;

/**
 * ham nay duoc thuc hien nhu sau
 *
 * 1. thread A goi execute command, add command len redis, isFinished == false
 *
 * 2. thread B giao tiep voi thiet bi, lay cac thong tin can thiet, save vao
 * redis, isFinished == true
 *
 * 3. thead A lang nghe event filter theo serialNumber va flag isFinished roi
 * thoat khoi ham wait
 *
 *
 * @author ThuyetLV
 */
public abstract class Command implements Serializable {

    protected static final StringBuilder sb = new StringBuilder();

    protected static final Logger logger = Logger.getLogger(Command.class);

    protected long serialVersionUID = 1l;
    private static final String UUID_STRING = UUID.randomUUID().toString();

    //Cac loai command
    public final static int CMD_CONTROL = 2;
    public final static int CMD_CONFIG_MODE = 3;
    public final static int CMD_CONFIG_NETWORK = 4;
    public final static int CMD_CONFIG_ID = 5;
    public final static int CMD_REQ_STATE = 6;

    public static long TIMEOUT_REQUEST_COMMAND;

    //Device serial number
    protected String serialNumber;
    protected String topic;
    protected String data;
    protected String description;
    //command type: CMD_CONTROL, CMD_CONFIG_MODE, CMD_CONFIG_NETWORK, CMD_CONFIG_ID, CMD_REQ_STATE
    protected int type;
    //TypeHis: 1: TYPE_CONTROL, 2: TYPE_SCHE_SV, 3: TYPE_SCHE_DEVICE,4: TYPE_IRR
    protected int typeHis = 1;

    //tag value
    protected Object results;

    // request timeout
    protected int timeout;

    //check Command da nhan duoc ket qua
    //protected boolean hasResult = false;
    //check loi
    public volatile boolean errorCheck = true;

    public volatile boolean isFinished = false;

    //thoi gian ton tai
    public long timeExist;

    // unique id for this command
    public String myID;

    //thong bao loi
    public volatile String msg;

    public volatile Map<String, String> returnValue;

    public volatile int instance; // for addobject command

    public boolean isErrorCheck() {
        return errorCheck;
    }

    public void setErrorCheck(boolean errorCheck) {
        this.errorCheck = errorCheck;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private String getTypeString(int type) {
        switch (type) {
            case CMD_CONTROL:
                return "CMD_CONTROL";
            case CMD_CONFIG_MODE:
                return "CMD_CONFIG_MODE";
            case CMD_CONFIG_NETWORK:
                return "CMD_CONFIG_NETWORK";
            case CMD_CONFIG_ID:
                return "CMD_CONFIG_ID";
            case CMD_REQ_STATE:
                return "CMD_REQ_STATE";
        }
        return "";
    }

    /**
     * return false if execute ok return true if execute not ok
     *
     * @return
     * @throws Exception
     */
    public boolean executeCommand() throws Exception {
        Command lastCommand = CommandRequestFactory.getCommand(serialNumber, type);
        if (lastCommand != null && lastCommand.getType() != CMD_REQ_STATE) {
            throw new AlreadyEnqueuedException(serialNumber + " Device is used by another: " + getTypeString(lastCommand.getType()));
        }
        long startTime = System.currentTimeMillis();
        synchronized (this) {
            try {
                preExecuteCommand();
                beginRequest();
                //Request AC to access Device.
                sendCommand();
                this.wait(timeout);
                if (System.currentTimeMillis() - startTime > timeout) {
                    sb.setLength(0);
                    sb.append(this.getType())
                            .append(", Serial=")
                            .append(serialNumber)
                            .append(", Timeout: REQUEST_TIMEOUT=")
                            .append(timeout)
                            .append("ms");
                    logger.error(sb);
                    receiveError("Timeout");
                } else {
                    sb.setLength(0);
                    sb.append(this.getType())
                            .append(", Serial=")
                            .append(serialNumber)
                            .append(", Success. Execute time: ")
                            .append(System.currentTimeMillis() - startTime)
                            .append("ms");
                    logger.info(sb);
                    postProcessResult(results);
                }
            } catch (Exception ex) {
                receiveError(ex.getMessage());
                sb.setLength(0);
                sb.append(this.getType())
                        .append(", Request=")
                        .append(serialNumber)
                        .append(", Error: ")
                        .append(ex.getMessage());
                logger.error(sb, ex);
                throw ex;
            } finally {
                CommandRequestFactory.removeCommand(this);
            }
        }
        return isFinished;
    }

    private int resultCode = -1;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public synchronized void receiveError(String errorString) {
        isFinished = true;
        errorCheck = true;
        this.msg = errorString;
        logger.warn("receiveError: " + errorString);
        notifyAll();
    }

    public synchronized void receiveResult(String response) {
        resultCode = 0;
        isFinished = true;
        errorCheck = false;
        this.msg = response;
        logger.info("receiveResult");
        notifyAll();
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setTimeExist(long timeExist) { // khi set time exist thi tao id duy nhat lun
        this.timeExist = timeExist;
        if (myID == null) {
            StringBuilder sb = new StringBuilder(UUID_STRING);
            sb.append("-").append(timeExist);
            this.myID = sb.toString();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        Command compareCommand = (Command) obj;
        return this.myID.equals(compareCommand.myID);
    }

    public long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setSerialVersionUID(long serialVersionUID) {
        this.serialVersionUID = serialVersionUID;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getResults() {
        return results;
    }

    public void setResults(Object results) {
        this.results = results;
    }

    public boolean isIsFinished() {
        return isFinished;
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public String getMyID() {
        return myID;
    }

    public void setMyID(String myID) {
        this.myID = myID;
    }

    public Map<String, String> getReturnValue() {
        return returnValue;
    }

    public String getRtnValue() {
        if (this.returnValue == null) {
            return null;
        }
        return this.returnValue.get(RETURN_KEY);
    }

    public void setReturnValue(Map<String, String> returnValue) {
        this.returnValue = returnValue;
    }

    private static final String RETURN_KEY = "RETURN";

    public void setReturnValue(String returnValue) {
        if (this.returnValue == null) {
            this.returnValue = new HashMap<String, String>();
        }
        this.returnValue.put(RETURN_KEY, returnValue);
    }

    public int getInstance() {
        return instance;
    }

    public void setInstance(int instance) {
        this.instance = instance;
    }

    protected void preExecuteCommand() {
        logger.debug("preExecuteCommand");
        if (timeout <= 0) {
            timeout = Properties.getCommandTimeout();
        }
    }

    private void beginRequest() {
        try {
            this.isFinished = false;
            CommandRequestFactory.addCommand(this);
            logger.info("Command waiting for " + serialNumber + " - type: " + getTypeString(this.type) + " timeout: " + timeout + " ms");
        } catch (Exception ex) {
            logger.error("ERROR beginRequest: ", ex);
        }
    }

    public abstract void sendCommand() throws Exception;

    protected void postProcessResult(Object result) {
        logger.debug("postProcessResult");
    }

    public String getTypeString() {
        return getTypeString(this.type);
    }

    @Override
    public String toString() {
        return "serialNumber: " + this.serialNumber + ", timeout: " + this.timeout + " ms, type: " + getTypeString(this.type);
    }

    public static final String SPE = "@";

    public String getKeyCommand() {
        return this.serialNumber + SPE + this.type;
    }

    public int getTypeHis() {
        return typeHis;
    }

    public void setTypeHis(int typeHis) {
        this.typeHis = typeHis;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private Document history;

    public Document getHistory() {
        return history;
    }

    public void setHistory(Document history) {
        this.history = history;
    }

}
