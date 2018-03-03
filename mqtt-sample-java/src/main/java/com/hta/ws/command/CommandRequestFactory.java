package com.hta.ws.command;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

/**
 * class nay luu cac command request xuong thiet bi qua web service cac thong
 * tin se duoc luu tru tap trung tren server redis de dam bao cac instance
 * mediation la stateless
 *
 * @author ThuyetLV
 */
public class CommandRequestFactory {

    private static final Map<String, Command> listLocalCommand = new ConcurrentHashMap();
    private static final Logger logger = Logger.getLogger(CommandRequestFactory.class.getName());
    protected static final StringBuilder sb = new StringBuilder();

    public static boolean removeCommand(Command cm) {
        if (cm == null) {
            return false;
        }

        logger.info("remove command with key: " + cm.getKeyCommand());
        listLocalCommand.remove(cm.getKeyCommand());
        return false;

    }

    public static void addCommand(Command cm) throws Exception {
        if (cm == null) {
            throw new Exception("Command is NULL !");
        } else if (cm.getSerialNumber() == null) {
            throw new Exception("Command with NULL serialNumber, Type=" + cm.getType());
        }

        cm.setTimeExist(System.currentTimeMillis());
        logger.info("--------addCommand: " + cm.getKeyCommand());
        listLocalCommand.put(cm.getKeyCommand(), cm);
    }

//    public static void saveCommand(Command cm) {
//        if (cm == null || (cm.getSerialNumber() == null)) {
//            return;
//        }
//    }
    public static Command getCommand(String serial, int cmdType) {
        if (serial == null) {
            return null;
        }
        logger.debug("--------getCommand: " + (serial + Command.SPE + cmdType));
        Object returnValue = listLocalCommand.get(serial + Command.SPE + cmdType);
        if (returnValue instanceof Command == false) {
            return null;
        }
        logger.debug("------getCommand: " + (serial + Command.SPE + cmdType));
        return (Command) returnValue;
    }

    public static boolean localContain(String serial, int cmdType) {
        if (serial == null) {
            return false;
        }
        return listLocalCommand.containsKey(serial + Command.SPE + cmdType);
    }
}
