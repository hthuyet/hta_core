///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.htaviet.websocket.database;
//
//import com.htaviet.websocket.common.Properties;
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import java.sql.Connection;
//import java.sql.SQLException;
//import javax.sql.DataSource;
//import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
//
///**
// *
// * @author thuyetlv
// */
//public class ConnectionPool {
//
//    private final Logger logger = Logger.getLogger(ConnectionPool.class);
//
//    static ConnectionPool _instance;
//
//    public static synchronized ConnectionPool getInstance() {
//        if (_instance == null) {
//            _instance = new ConnectionPool();
//        }
//        return _instance;
//    }
//
//    ConnectionPool() {
//        init();
//    }
//
//    private static DataSource datasource;
//
//    private void init() {
//        if (datasource == null) {
//            HikariConfig config = new HikariConfig();
//            logger.info("----------HikariUrl: " + Properties.getHikariUrl());
//            logger.info("----------HikariUser: " + Properties.getHikariUser());
//            logger.info("----------HikariPass: " + Properties.getHikariPass());
//
//            config.setJdbcUrl(Properties.getHikariUrl());
//            config.setUsername(Properties.getHikariUser());
//            config.setPassword(Properties.getHikariPass());
//            if (!StringUtils.isBlank(Properties.getHikariDatasouce())) {
//                config.setDataSourceClassName(Properties.getHikariDatasouce());
//            }
//
//            config.setMinimumIdle(Properties.getHikariMinPool());
//            config.setMaximumPoolSize(Properties.getHikariMaxPool());
//            config.setAutoCommit(true);
//            config.addDataSourceProperty("cachePrepStmts", Properties.getHikariCachePre());
//            config.addDataSourceProperty("prepStmtCacheSize", Properties.getHikariPrepStmt());
//            config.addDataSourceProperty("prepStmtCacheSqlLimit", Properties.getHikariPrepStmtSqlLimit());
//
//            datasource = new HikariDataSource(config);
//        }
//    }
//
//    public Connection getConnection() throws SQLException {
//        return datasource.getConnection();
//    }
//
//}
