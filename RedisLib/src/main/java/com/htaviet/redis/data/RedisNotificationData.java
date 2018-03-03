package com.htaviet.redis.data;

/**
 * Created by HIEUDT on 5/29/2017.
 */
public class RedisNotificationData {
    private String key;
    private Object data;

    public RedisNotificationData(String key) {
        this.key = key;
        data = null;
    }

    public RedisNotificationData(String key, Object data) {
        this.key = key;
        this.data = data;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RedisNotificationData{" + "key=" + key + ", data=" + data + '}';
    }
}
