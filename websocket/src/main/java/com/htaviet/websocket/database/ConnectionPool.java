/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.htaviet.websocket.database;

import com.htaviet.websocket.common.Properties;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Zan
 */
public class ConnectionPool {

    protected static Logger logger = Logger.getLogger(ConnectionPool.class);
    private static ConnectionPool singletonInstance;
    private ComboPooledDataSource cpds;
    private static DataSource datasource;   //For hikari

    private String type;

    private ConnectionPool() throws IOException, SQLException, PropertyVetoException {
        init();
    }

    private void init() throws PropertyVetoException {
        type = Properties.getPoolType();
        if (type.equalsIgnoreCase("c3p0")) {
            initC3p0();
        } else {
            initHikari();
        }
    }

    private void initC3p0() throws PropertyVetoException {
        if (cpds == null) {
            logger.info("----------initC3p0 Url: " + Properties.getHikariUrl());
            logger.info("----------initC3p0 User: " + Properties.getHikariUser());
            logger.info("----------initC3p0 Pass: " + Properties.getHikariPass());
            
            cpds = new ComboPooledDataSource();
            cpds.setDriverClass(Properties.getHikariDatasouce()); //loads the jdbc driver
            cpds.setJdbcUrl(Properties.getHikariUrl());
            cpds.setUser(Properties.getHikariUser());
            cpds.setPassword(Properties.getHikariPass());

            // the settings below are optional -- c3p0 can work with defaults
            cpds.setMinPoolSize(Properties.getHikariMinPool());
            cpds.setInitialPoolSize(Properties.getHikariMinPool());
            cpds.setMaxPoolSize(Properties.getHikariMaxPool());
            cpds.setTestConnectionOnCheckout(true);
        }
    }

    private void initHikari() {
        if (datasource == null) {
            HikariConfig config = new HikariConfig();
            logger.info("----------HikariUrl: " + Properties.getHikariUrl());
            logger.info("----------HikariUser: " + Properties.getHikariUser());
            logger.info("----------HikariPass: " + Properties.getHikariPass());

            config.setJdbcUrl(Properties.getHikariUrl());
            config.setUsername(Properties.getHikariUser());
            config.setPassword(Properties.getHikariPass());
            if (!StringUtils.isBlank(Properties.getHikariDatasouce())) {
                config.setDataSourceClassName(Properties.getHikariDatasouce());
            }

            config.setMinimumIdle(Properties.getHikariMinPool());
            config.setMaximumPoolSize(Properties.getHikariMaxPool());
            config.setAutoCommit(true);
            config.addDataSourceProperty("cachePrepStmts", Properties.getHikariCachePre());
            config.addDataSourceProperty("prepStmtCacheSize", Properties.getHikariPrepStmt());
            config.addDataSourceProperty("prepStmtCacheSqlLimit", Properties.getHikariPrepStmtSqlLimit());

            datasource = new HikariDataSource(config);
        }
    }

    public static synchronized ConnectionPool getInstance() throws IOException, SQLException, PropertyVetoException {
        if (singletonInstance == null) {
            singletonInstance = new ConnectionPool();
        }
        return singletonInstance;
    }

    public Connection getConnection() throws SQLException {
        if (type.equalsIgnoreCase("c3p0")) {
            return this.cpds.getConnection();
        } else {
            return datasource.getConnection();
        }
    }
}
