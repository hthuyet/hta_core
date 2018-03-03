/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.htaviet.redis.data;

/**
 *
 * @author HungNT
 */
public class RedisObject {

    private String redisServer;
    private int redisPort;
    private String redisPasswd;
    private int maxTotal;
    private int minIdle;
    private int maxIdle;
    private long maxWaitTime;
    private String notifyServer;
    private int notifyPort;
    private String notifyPasswd;

    public String getNotifyServer() {
        return notifyServer;
    }

    public void setNotifyServer(String notifyServer) {
        this.notifyServer = notifyServer;
    }

    public int getNotifyPort() {
        return notifyPort;
    }

    public void setNotifyPort(int notifyPort) {
        this.notifyPort = notifyPort;
    }

    public String getNotifyPasswd() {
        return notifyPasswd;
    }

    public void setNotifyPasswd(String notifyPasswd) {
        this.notifyPasswd = notifyPasswd;
    }

    public String getRedisServer() {
        return redisServer;
    }

    public void setRedisServer(String redisServer) {
        this.redisServer = redisServer;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
    }

    public String getRedisPasswd() {
        return redisPasswd;
    }

    public void setRedisPasswd(String redisPasswd) {
        this.redisPasswd = redisPasswd;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public long getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(long maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

}
