/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.htaviet.websocket.database;

import com.htaviet.websocket.obj.Device;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author thuyetlv
 */
public class DbProcess {

    private static final Logger logger = Logger.getLogger(DbProcess.class);

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

    private static final int BATCH_LIMIT = 200;

    //<editor-fold defaultstate="collapsed" desc="updateDeviceOff">
    private static final String SQL_UPDATE_DEVICE_OFF = "UPDATE device SET state=?,port_status='[0,0,0,0]'";

    public Integer updateDeviceOff() {
        logger.debug("----------BEGIN updateDeviceOff--------");
        long startTime = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement ps = null;
        Integer rtn = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement(SQL_UPDATE_DEVICE_OFF);
            ps.setInt(1, Device.STATE_OFFLINE);
            rtn = ps.executeUpdate();
            return rtn;
        } catch (Exception e) {
            logger.error("ERROR updateDeviceOff: ", e);
            return null;
        } finally {
            closePs(ps);
            closeConnection(connection);
            logTime("Time to updateDeviceOff: ", startTime);
        }
    }//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getDevice">
    private static final String SQL_GET_DEVICE = "SELECT id,code,port_status,state FROM device WHERE `id` > ? ORDER BY `id` LIMIT ?";

    public List getDevice(long id, int limit) {
        long startTime = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List rtn = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement(SQL_GET_DEVICE);
            ps.setLong(1, id);
            ps.setInt(2, limit);
            rs = ps.executeQuery();
            rtn = new ArrayList<Device>(limit);
            Device device;
            while (rs.next()) {
                device = new Device();
                device.setId(rs.getLong(Device.ID));
                device.setCode(rs.getString(Device.CODE));
                device.setPortStatus(rs.getString(Device.PORT_STATUS));
                device.setState(rs.getInt(Device.STATE));
                rtn.add(device);
            }
            return rtn;
        } catch (Exception e) {
            logger.error("ERROR getDevice: ", e);
            return null;
        } finally {
            closeRs(rs);
            closePs(ps);
            closeConnection(connection);
            logTime("Time to getDevice: ", startTime);
        }
    }//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="insertDevice">
    private static final String SQL_INSERT_DEVICE = "INSERT INTO device(code, name,port_status,state,port_update,devicetype_id,user_id) VALUES(?,?,?,?,?,?,?)";

    public int[] insertDevice(List<Device> listDevice) {
        long startTime = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement ps = null;
        int[] rtn = null;
        try {
            if (listDevice == null || listDevice.isEmpty()) {
                return new int[1];
            }
            connection = ConnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement(SQL_INSERT_DEVICE);

            int i = 0;
            for (Device device : listDevice) {
                ps.setString(1, device.getCode());
                ps.setString(2, device.getCode());
                ps.setString(3, device.getPortStatus());
                ps.setInt(4, Device.STATE_ONLINE);
                ps.setTimestamp(5, new java.sql.Timestamp(startTime));
                ps.setInt(6, device.getType());
                ps.setLong(7, device.getUserId());
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
            logger.error("ERROR insertDevice: " + listDevice.size(), e);
            return null;
        } finally {
            closePs(ps);
            closeConnection(connection);
            logTime("Time to updatePortDevice: ", startTime);
        }
    }//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="updatePortDevice">
    private static final String SQL_UPDATE_DEVICE = "UPDATE device SET port_status=?,state=?,port_update=? WHERE `code`=?";

    public Integer updatePortDevice(Device device) {
        long startTime = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement ps = null;
        int[] rtn = null;
        try {
            if (device == null) {
                return 1;
            }
            connection = ConnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement(SQL_UPDATE_DEVICE);

            ps.setString(1, device.getPortStatus());
            ps.setInt(2, device.getState());
            ps.setTimestamp(3, new java.sql.Timestamp(startTime));
            ps.setString(4, device.getCode());
            return ps.executeUpdate();
        } catch (Exception e) {
            logger.error("ERROR updatePortDevice: ", e);
            return null;
        } finally {
            closePs(ps);
            closeConnection(connection);
            logTime("Time to updatePortDevice: ", startTime);
        }
    }

    public int[] updatePortDevice(List<Device> listDevice) {
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
            for (Device device : listDevice) {
                ps.setString(1, device.getPortStatus());
                ps.setInt(2, Device.STATE_ONLINE);
                ps.setTimestamp(3, new java.sql.Timestamp(startTime));
                ps.setString(4, device.getCode());
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
}
