/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hta.ws.database;

import com.hta.ws.obj.DevicePortObj;
import com.hta.ws.obj.IrriObj;
import com.hta.ws.obj.ScheduleObj;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author thuyetlv
 */
public class DbProcess {

    private static final Logger logger = Logger.getLogger(DbProcess.class);
    private static final int BATCH_LIMIT = 200;
    private int logTimeQuerySlow = 1000;        //Query vuot qua logTimeQuerySlow ms thi log

    //<editor-fold defaultstate="collapsed" desc="close">
    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                logger.error("ERROR closeConnection: ", ex);
            }
        }
    }

    private void closePs(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException ex) {
                logger.error("ERROR closePs: ", ex);
            }
        }
    }

    private void closeRs(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                logger.error("ERROR closeRs: ", ex);
            }
        }
    }//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="logTime">
    private void logTime(String label, long startTime) {
        if (logTimeQuerySlow < (System.currentTimeMillis() - startTime)) {
            logger.info(label + (System.currentTimeMillis() - startTime) + " ms");
        }
    }//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getSchedule">
    private static final String SQL_GET_SCHEDULE = "SELECT * FROM schedule WHERE is_start IN(0,2,3) AND start_time<NOW() LIMIT ?";

    public List getSchedule(int max) {
        long startTime = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List rtn = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement(SQL_GET_SCHEDULE);
            ps.setInt(1, max);
            rs = ps.executeQuery();
            rtn = new ArrayList<>();
            ScheduleObj scheduleObj;
            while (rs.next()) {
                scheduleObj = new ScheduleObj();
                scheduleObj.setId(rs.getLong(ScheduleObj.ID));
                scheduleObj.setTopic(rs.getString(ScheduleObj.TOPIC));
                scheduleObj.setCount(rs.getInt(ScheduleObj.COUNT));
                scheduleObj.setType(rs.getInt(ScheduleObj.TYPE));
                scheduleObj.setCommand(rs.getString(ScheduleObj.COMMAND));
                scheduleObj.setIsStart(rs.getInt(ScheduleObj.IS_START));
                scheduleObj.setStartTime(rs.getTimestamp(ScheduleObj.START_TIME));
                scheduleObj.setDeviceId(rs.getLong(ScheduleObj.DEVICE_ID));
                scheduleObj.setSerial(rs.getString(ScheduleObj.SERIAL));
                scheduleObj.setDescription(rs.getString(ScheduleObj.DESCRIPTION));
                rtn.add(scheduleObj);
            }
            return rtn;
        } catch (Exception e) {
            logger.error("ERROR getSchedule: ", e);
            return null;
        } finally {
            closeRs(rs);
            closePs(ps);
            closeConnection(connection);
            logTime("Time to getSchedule: ", startTime);
        }
    }//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="updateSchedule">
    private static final String SQL_UPDATE_SCHEDULE = "UPDATE schedule set is_start=?,start_time=?,count=? WHERE id=?";

    public int[] updateSchedule(List<ScheduleObj> list) {
        long startTime = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement ps = null;
        int[] rtn = null;
        try {
            if (list == null || list.isEmpty()) {
                return new int[0];
            }
            connection = ConnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement(SQL_UPDATE_SCHEDULE);
            int i = 0;
            for (ScheduleObj obj : list) {
                ps.setInt(1, obj.getIsStart());
                if (obj.getStartTime() != null) {
                    ps.setTimestamp(2, new java.sql.Timestamp(obj.getStartTime().getTime()));
                } else {
                    ps.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
                }
                ps.setInt(3, obj.getCount());
                ps.setLong(4, obj.getId());
                ps.addBatch();
                if (i++ % BATCH_LIMIT == 0) {
                    rtn = ps.executeBatch();
                    i = 0;
                }
            }
            if (i > 0) {
                rtn = ps.executeBatch();
            }
            return rtn;
        } catch (Exception e) {
            logger.error("ERROR updateSchedule: ", e);
            return null;
        } finally {
            closePs(ps);
            closeConnection(connection);
            logTime("Time to updateSchedule: ", startTime);
        }
    }//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getIrriToStart">
    private static final String SQL_GET_IRRI_TO_START = "SELECT i.id AS id,i.status AS STATUS,id.id AS IRR_ID,id.device_id as device_id,"
            + "id.is_start AS IRR_IS_START,id.command AS command,id.command_off AS command_off,id.count AS COUNT, id.`from` AS `from`,"
            + "id.`to` AS `to`, id.`topic` AS `topic`,id.`serial` AS `serial`,id.description as description "
            + "FROM `irrigation_detail` id LEFT JOIN `irrigation` i ON i.id=id.`irrigation_id` "
            + "WHERE i.status IN (" + IrriObj.STATUS_ON + ") AND i.from_date<NOW() AND i.to_date>NOW() "
            + "AND id.is_start=" + IrriObj.NOT_START + " AND STR_TO_DATE(CONCAT(DATE(NOW()),id.`from`),'%Y-%m-%d %H:%i:%s') < NOW() "
            + "AND STR_TO_DATE(CONCAT(DATE(NOW()),id.`to`),'%Y-%m-%d %H:%i:%s') > NOW() "
            + "ORDER BY `step` ASC LIMIT ?";

    public List getIrriToStart(int max) {
        long startTime = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List rtn = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement(SQL_GET_IRRI_TO_START);
            ps.setInt(1, max);
            rs = ps.executeQuery();
            rtn = new ArrayList<>();
            IrriObj obj;
            while (rs.next()) {
                obj = new IrriObj();
                obj.setId(rs.getLong(IrriObj.IRR_ID));
                obj.setDeviceId(rs.getLong(IrriObj.DEVICE_ID));
                obj.setTopic(rs.getString(IrriObj.TOPIC));
                obj.setCount(rs.getInt(IrriObj.COUNT));
                obj.setCommand(rs.getString(IrriObj.COMMAND));
                obj.setCommandOff(rs.getString(IrriObj.COMMAND_OFF));
                obj.setIsStart(rs.getInt(IrriObj.IRR_IS_START));
                obj.setSerial(rs.getString(IrriObj.SERIAL));
                obj.setDescription(rs.getString(IrriObj.DESCRIPTION));
                rtn.add(obj);
            }
            return rtn;
        } catch (Exception e) {
            logger.error("ERROR getIrriToStart: ", e);
            return null;
        } finally {
            closeRs(rs);
            closePs(ps);
            closeConnection(connection);
            logTime("Time to getIrriToStart: ", startTime);
        }
    }//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getIrriToStop">
    private static final String SQL_GET_IRRI_TO_STOP = "SELECT i.id AS id,i.status AS STATUS,id.id AS IRR_ID,"
            + "id.is_start AS IRR_IS_START,id.command AS command,id.command_off AS command_off,id.count AS COUNT, id.`from` AS `from`,"
            + "id.`to` AS `to`, id.`topic` AS `topic`,id.`serial` AS `serial`,id.description as description "
            + "FROM `irrigation_detail` id LEFT JOIN `irrigation` i ON i.id=id.`irrigation_id` "
            + "WHERE i.status IN (" + IrriObj.STATUS_ON + ") AND i.from_date<NOW() AND i.to_date>NOW() "
            + "AND id.is_start=" + IrriObj.START + " AND STR_TO_DATE(CONCAT(DATE(NOW()),id.`to`),'%Y-%m-%d %H:%i:%s') <= NOW() "
            + "ORDER BY `step` ASC LIMIT ?";

    public List getIrriToStop(int max) {
        long startTime = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List rtn = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement(SQL_GET_IRRI_TO_STOP);
            ps.setInt(1, max);
            rs = ps.executeQuery();
            rtn = new ArrayList<>();
            IrriObj obj;
            while (rs.next()) {
                obj = new IrriObj();
                obj.setId(rs.getLong(IrriObj.IRR_ID));
                obj.setTopic(rs.getString(IrriObj.TOPIC));
                obj.setCount(rs.getInt(IrriObj.COUNT));
                obj.setCommand(rs.getString(IrriObj.COMMAND));
                obj.setCommandOff(rs.getString(IrriObj.COMMAND_OFF));
                obj.setIsStart(rs.getInt(IrriObj.IRR_IS_START));
                obj.setSerial(rs.getString(IrriObj.SERIAL));
                obj.setDescription(rs.getString(IrriObj.DESCRIPTION));
                rtn.add(obj);
            }
            return rtn;
        } catch (Exception e) {
            logger.error("ERROR getIrriToStop: ", e);
            return null;
        } finally {
            closeRs(rs);
            closePs(ps);
            closeConnection(connection);
            logTime("Time to getIrriToStop: ", startTime);
        }
    }//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="updateIrri">
    private static final String SQL_UPDATE_IRRI = "UPDATE `irrigation_detail` set `is_start`=?,`count`=? WHERE id=?";

    public int[] updateIrri(List<IrriObj> list) {
        long startTime = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement ps = null;
        int[] rtn = null;
        try {
            if (list == null || list.isEmpty()) {
                return new int[0];
            }
            connection = ConnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement(SQL_UPDATE_IRRI);
            int i = 0;
            for (IrriObj obj : list) {
                ps.setInt(1, obj.getIsStart());
                ps.setInt(2, obj.getCount());
                ps.setLong(3, obj.getId());
                ps.addBatch();
                if (i++ % BATCH_LIMIT == 0) {
                    rtn = ps.executeBatch();
                    i = 0;
                }
            }
            if (i > 0) {
                rtn = ps.executeBatch();
            }
            return rtn;
        } catch (Exception e) {
            logger.error("ERROR updateIrri: ", e);
            return null;
        } finally {
            closePs(ps);
            closeConnection(connection);
            logTime("Time to updateIrri: ", startTime);
        }
    }//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="updatePortDevice">
    private static final String SQL_UPDATE_DEVICE = "UPDATE device SET state=?,port_update=? WHERE `code`=?";
    public static final int STATE_ONLINE = 1;

    public int[] updatePortDevice(List<String> listDevice) {
        long startTime = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement ps = null;
        int[] rtn = null;
        try {
            if (listDevice == null || listDevice.isEmpty()) {
                return new int[1];
            }
            connection = ConnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement(SQL_UPDATE_DEVICE);

            int i = 0;
            for (String device : listDevice) {
                ps.setInt(1, STATE_ONLINE);
                ps.setTimestamp(2, new java.sql.Timestamp(startTime));
                ps.setString(3, device);
                ps.addBatch();
                i++;
                if (i % BATCH_LIMIT == 0) {
                    rtn = ps.executeBatch();
                    i = 0;
                }
            }
            if (i > 0) {
                rtn = ps.executeBatch();
            }
            return rtn;
        } catch (Exception e) {
            logger.error("ERROR updatePortDevice: ", e);
            return null;
        } finally {
            closePs(ps);
            closeConnection(connection);
            logTime("Time to updatePortDevice: ", startTime);
        }
    }//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="deleteDevice">
    private static final String SQL_DELETE_DEVICE = "DELETE FROM device WHERE `code`=?";

    public Integer deleteDevice(String device) {
        long startTime = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement ps = null;
        Integer rtn = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement(SQL_DELETE_DEVICE);
            ps.setString(1, device);
            rtn = ps.executeUpdate();
            return rtn;
        } catch (Exception e) {
            logger.error("ERROR deleteDevice: ", e);
            return null;
        } finally {
            closePs(ps);
            closeConnection(connection);
            logTime("Time to deleteDevice: ", startTime);
        }
    }

    public int[] deleteDevice(List<String> listDevice) {
        long startTime = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement ps = null;
        int[] rtn = null;
        try {
            if (listDevice == null || listDevice.isEmpty()) {
                return new int[1];
            }
            connection = ConnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement(SQL_DELETE_DEVICE);

            int i = 0;
            for (String device : listDevice) {
                ps.setString(1, device);
                ps.addBatch();
                i++;
                if (i % BATCH_LIMIT == 0) {
                    rtn = ps.executeBatch();
                    i = 0;
                }
            }
            if (i > 0) {
                rtn = ps.executeBatch();
            }
            return rtn;
        } catch (Exception e) {
            logger.error("ERROR deleteDevice: ", e);
            return null;
        } finally {
            closePs(ps);
            closeConnection(connection);
            logTime("Time to deleteDevice: ", startTime);
        }
    }//</editor-fold>

    //Delete from schedule
    //Delete from irrigation_detail
    //<editor-fold defaultstate="collapsed" desc="deleteIrrigationDetail">
    private static final String SQL_DELETE_IRR = "DELETE FROM irrigation_detail WHERE `device_id`=?";

    public Integer deleteIrrigationDetail(Long device) {
        long startTime = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement ps = null;
        Integer rtn = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement(SQL_DELETE_IRR);
            ps.setLong(1, device);
            rtn = ps.executeUpdate();
            return rtn;
        } catch (Exception e) {
            logger.error("ERROR deleteIrrigationDetail: ", e);
            return null;
        } finally {
            closePs(ps);
            closeConnection(connection);
            logTime("Time to deleteIrrigationDetail: ", startTime);
        }
    }

    public int[] deleteIrrigationDetail(List<Long> listDevice) {
        long startTime = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement ps = null;
        int[] rtn = null;
        try {
            if (listDevice == null || listDevice.isEmpty()) {
                return new int[1];
            }
            connection = ConnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement(SQL_DELETE_IRR);

            int i = 0;
            for (Long device : listDevice) {
                ps.setLong(1, device);
                ps.addBatch();
                i++;
                if (i % BATCH_LIMIT == 0) {
                    rtn = ps.executeBatch();
                    i = 0;
                }
            }
            if (i > 0) {
                rtn = ps.executeBatch();
            }
            return rtn;
        } catch (Exception e) {
            logger.error("ERROR deleteIrrigationDetail: ", e);
            return null;
        } finally {
            closePs(ps);
            closeConnection(connection);
            logTime("Time to deleteIrrigationDetail: ", startTime);
        }
    }//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="deleteSchedule">
    private static final String SQL_DELETE_SCHEDULE = "DELETE FROM schedule WHERE `device_id`=?";

    public Integer deleteSchedule(Long device) {
        long startTime = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement ps = null;
        Integer rtn = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement(SQL_DELETE_SCHEDULE);
            ps.setLong(1, device);
            rtn = ps.executeUpdate();
            return rtn;
        } catch (Exception e) {
            logger.error("ERROR deleteSchedule: ", e);
            return null;
        } finally {
            closePs(ps);
            closeConnection(connection);
            logTime("Time to deleteSchedule: ", startTime);
        }
    }

    public int[] deleteSchedule(List<Long> listDevice) {
        long startTime = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement ps = null;
        int[] rtn = null;
        try {
            if (listDevice == null || listDevice.isEmpty()) {
                return new int[1];
            }
            connection = ConnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement(SQL_DELETE_SCHEDULE);

            int i = 0;
            for (Long device : listDevice) {
                ps.setLong(1, device);
                ps.addBatch();
                i++;
                if (i % BATCH_LIMIT == 0) {
                    rtn = ps.executeBatch();
                    i = 0;
                }
            }
            if (i > 0) {
                rtn = ps.executeBatch();
            }
            return rtn;
        } catch (Exception e) {
            logger.error("ERROR deleteSchedule: ", e);
            return null;
        } finally {
            closePs(ps);
            closeConnection(connection);
            logTime("Time to deleteSchedule: ", startTime);
        }
    }//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="getDevicePort">
    private static final String SQL_GET_DEVICE_PORT = "SELECT * FROM device_port WHERE device_id=?";

    public HashMap getDevicePort(Long deviceId) {
        long startTime = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        HashMap rtn = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement(SQL_GET_DEVICE_PORT);
            ps.setLong(1, deviceId);
            rs = ps.executeQuery();
            rtn = new HashMap<>();
            DevicePortObj obj;
            while (rs.next()) {
                obj = new DevicePortObj();
                obj.setId(rs.getLong(DevicePortObj.ID));
                obj.setDeviceId(rs.getLong(DevicePortObj.DEVICE_ID));
                obj.setPort(rs.getInt(DevicePortObj.PORT));
                obj.setDautuoi(rs.getInt(DevicePortObj.DAUTUOI));
                obj.setGoc(rs.getInt(DevicePortObj.GOC));
                obj.setLuongnuoc(rs.getFloat(DevicePortObj.LUONGNUOC));
                obj.setPortName(rs.getString(DevicePortObj.PORT_NAME));
                obj.setAreaId(rs.getLong(DevicePortObj.AREA_ID));
                rtn.put(obj.getPort(), obj);
            }
            return rtn;
        } catch (Exception e) {
            logger.error("ERROR getDevicePort: ", e);
            return null;
        } finally {
            closeRs(rs);
            closePs(ps);
            closeConnection(connection);
            logTime("Time to getIrriToStart: ", startTime);
        }
    }//</editor-fold>

    //Move to His
    //<editor-fold defaultstate="collapsed" desc="moveIrriToHis">
    private static final String SQL_MOVE_IRRI_DETAIL_TO_HIS = "INSERT INTO irrigation_detail_history SELECT * from irrigation_detail WHERE irrigation_id IN (SELECT id from irrigation WHERE to_date < NOW())";
    private static final String SQL_MOVE_IRRI_TO_HIS = "INSERT INTO irrigation_history SELECT * from irrigation WHERE to_date < NOW()";

    public Boolean moveIrriToHis() {
        long startTime = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement(SQL_MOVE_IRRI_DETAIL_TO_HIS);
            ps.executeUpdate();
            ps = connection.prepareStatement(SQL_MOVE_IRRI_TO_HIS);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            logger.error("ERROR moveIrriToHis: ", e);
            return null;
        } finally {
            closePs(ps);
            closeConnection(connection);
            logTime("Time to moveIrriToHis: ", startTime);
        }
    }//</editor-fold>
}
