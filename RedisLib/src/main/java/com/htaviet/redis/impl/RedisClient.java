package com.htaviet.redis.impl;

import com.htaviet.redis.api.RedisService;
import com.htaviet.redis.common.RedisConstants;
import com.htaviet.redis.data.RedisResponse;
import com.htaviet.redis.data.RedisResult;
import org.nustaq.serialization.FSTConfiguration;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.htaviet.redis.data.RedisObject;
import org.apache.log4j.Logger;

/**
 * Created by HIEUDT on 5/24/2017.
 */
public class RedisClient implements RedisService {

    protected final Logger logger = Logger.getLogger(RedisClient.class);
    private JedisPool redisPool;
    private JedisPool redisPoolNotify;
    private String server;
    private int port;
    private String password;
    private String serverNotify;
    private int portNotify;
    private String passwordNotify;
    public static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    public RedisClient(RedisObject obj) {
        this.server = obj.getRedisServer();
        this.port = obj.getRedisPort();
        this.password = obj.getRedisPasswd();
        this.serverNotify = obj.getNotifyServer();
        this.portNotify = obj.getNotifyPort();
        this.passwordNotify = obj.getNotifyPasswd();
        JedisPoolConfig redisConfig = new JedisPoolConfig();
        redisConfig.setTestOnBorrow(false);
        redisConfig.setTestOnCreate(false);
        redisConfig.setTestOnReturn(false);
        redisConfig.setMaxTotal(obj.getMaxTotal());
        redisConfig.setMinIdle(obj.getMinIdle());
        redisConfig.setMaxIdle(obj.getMaxIdle());
        redisConfig.setMaxWaitMillis(obj.getMaxWaitTime());
        if ("".equals(password)) {
            redisPool = new JedisPool(redisConfig, server, port, RedisConstants.REDIS_DEFAULT_TIMEOUT);
        } else {
            redisPool = new JedisPool(redisConfig, server, port, RedisConstants.REDIS_DEFAULT_TIMEOUT, password);
        }
        if (serverNotify != null) {
            if ("".equals(passwordNotify)) {
                redisPoolNotify = new JedisPool(redisConfig, serverNotify, portNotify, RedisConstants.REDIS_DEFAULT_TIMEOUT);
            } else {
                redisPoolNotify = new JedisPool(redisConfig, serverNotify, portNotify, RedisConstants.REDIS_DEFAULT_TIMEOUT, passwordNotify);
            }
            logger.info("Redis Command Server Notify: " + serverNotify + " - Port : " + portNotify);
        }
        logger.info("Redis Command Server : " + server + " - Port : " + port);
    }

    public JedisPool getRedisPool() {
        return redisPool;
    }

    public JedisPool getRedisPoolNotify() {
        return redisPoolNotify;
    }

    public boolean exist(String key) {
        Jedis redisInstance = redisPool.getResource();
        boolean result = redisInstance.exists(key);
        redisInstance.close();
        return result;
    }

    public byte[] getByteArr(String key) {
        byte[] result = new byte[]{};
        Jedis redisServer = redisPool.getResource();
        result = redisServer.get((key).getBytes());
        redisServer.close();
        return result;
    }

    public Set<String> getAll(String domain) {
        Set<String> result = null;
        Jedis redisServer = redisPool.getResource();
        result = redisServer.keys(domain + "*");
        redisServer.close();
        return result;
    }

    public void insertByteArr(String key, byte[] data) {
        Jedis redisServer = redisPool.getResource();
        redisServer.set(key.getBytes(), data);
        redisServer.close();
    }

    @Override
    public RedisResult insertOrUpdate(String key, Object object) {
        RedisResult result = new RedisResult();
        try {
            Jedis redisInstance = redisPool.getResource();
            byte[] data = this.conf.asByteArray(object);
            String res = redisInstance.set(key.getBytes(), data);
            if (res.equals("OK")) {
                result.setResponse(RedisResponse.SUCCESS);
                result.setMessage("OK");
            } else {
                result.setResponse(RedisResponse.FAILURE);
                result.setMessage("SET command failure!!");
            }
            redisInstance.close();
        } catch (JedisException jex) {
            logger.error("Exception when SET data for key: " + key + " , " + jex);
            result.setResponse(RedisResponse.FAILURE);
            result.setMessage(jex.getMessage());
        } catch (Exception ex) {
            logger.error("Exception when SET data for key: " + key + " , " + ex);
            result.setResponse(RedisResponse.FAILURE);
            result.setMessage(ex.getMessage());
        }
        return result;
    }

    @Override
    public RedisResult insertOrUpdate(Map<String, Object> keyMap) {
        Jedis redisInstance = null;
        Pipeline pipeline = null;
        RedisResult result = new RedisResult();
        int i = 0;
        try {
            redisInstance = redisPool.getResource();
            pipeline = redisInstance.pipelined();
            for (String key : keyMap.keySet()) {
                ++i;
                byte[] data = this.conf.asByteArray(keyMap.get(key));
                pipeline.set(key.getBytes(), data);
                if (i % RedisConstants.REDIS_MAX_BLOCK == 0) {
                    pipeline.sync();
                }
            }
            pipeline.sync();
            result.setResponse(RedisResponse.SUCCESS);
            result.setMessage("OK");
        } catch (Exception ex) {
            logger.error("Exeption when SET data: " + ex.getMessage());
            result.setResponse(RedisResponse.FAILURE);
            result.setMessage(ex.getMessage());
        } finally {
            try {
                if (pipeline != null) {
                    pipeline.close();
                }
                if (redisInstance != null) {
                    redisInstance.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return result;
    }

    public RedisResult insertOrUpdateWithExpire(String key, Object object, int expireTime) {
        RedisResult result = new RedisResult();
        Jedis redisInstance = null;
        try {
//            System.out.println("--------insertOrUpdateWithExpire: " + key + " - " + expireTime);
            redisInstance = redisPoolNotify.getResource();
            byte[] data = this.conf.asByteArray(object);
            String res = redisInstance.setex(key.getBytes(), expireTime, data);
            if (res.equals("OK")) {
                result.setResponse(RedisResponse.SUCCESS);
                result.setMessage("OK");
            } else {
                result.setResponse(RedisResponse.FAILURE);
                result.setMessage("SET command failure!!");
            }
        } catch (JedisException jex) {
            logger.error("Exception when SET data for key: " + key + " , " + jex);
            result.setResponse(RedisResponse.FAILURE);
            result.setMessage(jex.getMessage());
        } catch (Exception ex) {
            logger.error("Exception when SET data for key: " + key + " , " + ex);
            result.setResponse(RedisResponse.FAILURE);
            result.setMessage(ex.getMessage());
        } finally {
            redisInstance.close();
        }
        return result;
    }

    @Override
    public Object getObject(String key) {
        return getByteArr(key) != null ? conf.asObject(getByteArr(key)) : null;
    }

    @Override
    public List<Object> getObjectList(List<String> keyList) {
        Jedis redisInstance = null;
        Pipeline pipeline = null;
        List<Object> objectList = new ArrayList<Object>();
        try {
            redisInstance = redisPool.getResource();
            pipeline = redisInstance.pipelined();
            List<Response<byte[]>> responses = new ArrayList<Response<byte[]>>();
            for (String key : keyList) {
                responses.add(pipeline.get(key.getBytes()));
            }
            pipeline.sync();
            for (Response<byte[]> response : responses) {
                byte[] data = response.get();
                if (data != null) {
                    Object member = this.conf.asObject(data);
                    objectList.add(member);
                }
            }
        } catch (Exception ex) {
            logger.error("Exception when GET list: " + ex.getMessage());
        } finally {
            try {
                if (pipeline != null) {
                    pipeline.close();
                }
                if (redisInstance != null) {
                    redisInstance.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return objectList;
    }

    @Override
    public RedisResult delete(String key) {
        Jedis redisInstance = null;
        RedisResult result = new RedisResult();
        try {
            redisInstance = redisPool.getResource();
            Long res = redisInstance.del(key.getBytes());
            if (res == 1) {
                result.setResponse(RedisResponse.SUCCESS);
                result.setMessage("OK");
            } else {
                result.setResponse(RedisResponse.FAILURE);
                result.setMessage("Del key failure, key may not exist!!");
            }
        } catch (Exception ex) {
            logger.error("Exception when delete key: " + key);
            result.setResponse(RedisResponse.FAILURE);
            result.setMessage(ex.getMessage());
        } finally {
            if (redisInstance != null) {
                redisInstance.close();
            }
        }
        return result;
    }

    @Override
    public RedisResult deleteList(List<String> keyList) {
        Jedis redisInstance = null;
        Pipeline pipeline = null;
        RedisResult result = new RedisResult();
        try {
            redisInstance = redisPool.getResource();
            pipeline = redisInstance.pipelined();
            for (String key : keyList) {
                pipeline.del(key);
            }
            pipeline.sync();
            result.setResponse(RedisResponse.SUCCESS);
            result.setMessage("OK");
        } catch (Exception ex) {
            logger.error("Exception when delete keyList ");
            result.setResponse(RedisResponse.FAILURE);
            result.setMessage(ex.getMessage());
        } finally {
            try {
                if (pipeline != null) {
                    pipeline.close();
                }
                if (redisInstance != null) {
                    redisInstance.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return result;
    }

    @Override
    public RedisResult setExpire(String key, int seconds) {
        Jedis redisInstance = null;
        RedisResult result = new RedisResult();
        try {
            redisInstance = redisPool.getResource();
            Long res = redisInstance.expire(key.getBytes(), seconds);
            if (res == 1) {
                result.setResponse(RedisResponse.SUCCESS);
                result.setMessage("OK");
            } else {
                result.setResponse(RedisResponse.FAILURE);
                result.setMessage("Expire key failure, key may not exist!!");
            }
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (msg == null) {
                msg = ex.getClass().getName();
            }
            logger.error("Exception when expire key: " + key + " with ttl: " + seconds + " , " + msg);
            result.setResponse(RedisResponse.FAILURE);
            result.setMessage("Exception: " + msg);
        } finally {
            if (redisInstance != null) {
                redisInstance.close();
            }
        }
        return result;
    }

    @Override
    public RedisResult setExpireList(Map<String, Integer> keyTime) {
        Jedis redisInstance = null;
        Pipeline pipeline = null;
        RedisResult result = new RedisResult();
        try {
            redisInstance = redisPool.getResource();
            pipeline = redisInstance.pipelined();
            for (String key : keyTime.keySet()) {
                pipeline.expire(key, keyTime.get(key));
            }
            pipeline.sync();
            result.setResponse(RedisResponse.SUCCESS);
            result.setMessage("OK");
        } catch (Exception ex) {
            logger.error("Exception when expire keyList ");
            result.setResponse(RedisResponse.FAILURE);
            result.setMessage(ex.getMessage());
        } finally {
            try {
                if (pipeline != null) {
                    pipeline.close();
                }
                if (redisInstance != null) {
                    redisInstance.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return result;
    }

    public RedisResult setNotification(String key, int seconds) {
        Jedis redisInstance = null;
        RedisResult result = new RedisResult();
        try {
            redisInstance = redisPoolNotify.getResource();
            String resEx = redisInstance.setex(key, seconds, "");
            if (resEx.equals("OK")) {
                result.setResponse(RedisResponse.SUCCESS);
                result.setMessage("OK");
            } else {
                result.setResponse(RedisResponse.FAILURE);
                result.setMessage("Notification key failure!!");
            }
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (msg == null) {
                msg = ex.getClass().getName();
            }
            logger.error("Exception when notify key: " + key + " with time: " + seconds + " , " + msg);
            result.setResponse(RedisResponse.FAILURE);
            result.setMessage("Exception: " + msg);
        } finally {
            if (redisInstance != null) {
                redisInstance.close();
            }
        }
        return result;
    }

    public RedisResult cancelNotification(String key) {
        Jedis redisInstance = null;
        RedisResult result = new RedisResult();
        try {
            redisInstance = redisPoolNotify.getResource();
            Long res = redisInstance.del(key.getBytes());
            if (res == 1) {
                result.setResponse(RedisResponse.SUCCESS);
                result.setMessage("OK");
            } else {
                result.setResponse(RedisResponse.FAILURE);
                result.setMessage("Cancel notification failure, key notification may not exist!!");
            }
        } catch (Exception ex) {
            logger.error("Exception when cancel notification for key: " + key);
            result.setResponse(RedisResponse.FAILURE);
            result.setMessage(ex.getMessage());
        } finally {
            if (redisInstance != null) {
                redisInstance.close();
            }
        }
        return result;
    }

    @Override
    public Long incrSeq(String Id) {
        Jedis redisInstance = redisPool.getResource();
        Long result = redisInstance.incr(Id);
        redisInstance.close();
        return result;
    }

    public void subscribe(String patterns, Subscriber object) {
        Jedis redisInstance = redisPool.getResource();
        redisInstance.psubscribe(object, patterns);
    }

    public void subscribe(String domain, JedisPubSub object) {
        Jedis redisServer = redisPool.getResource();
        redisServer.psubscribe(object, "__key*__:*");
    }

    public void expire(String key, int seconds) {
        Jedis redisServer = redisPool.getResource();
        redisServer.expire(key.getBytes(), seconds);
        redisServer.close();
    }

    public void subscribeEventSet(Subscriber object) {
        Jedis redisInstance = redisPool.getResource();
        redisInstance.psubscribe(object, RedisConstants.SET_PATTERN);
    }

    public void subscribeEventNotification(Subscriber object) {
        Jedis redisInstance = redisPoolNotify.getResource();
        redisInstance.psubscribe(object, RedisConstants.EXPIRE_PATTERN);
    }

    public void unSubscribeEventNotification(Subscriber object) {
        object.unsubscribe();
    }

    public void publish(String channel_name, String message) {
        Jedis redisInstance = redisPool.getResource();
        redisInstance.publish(channel_name, message);
        redisInstance.close();
    }

    public List<String> getKeyMatchPattern(String pattern) {
        Jedis redisInstance = null;
        List<String> list = new ArrayList<String>();
        try {
            redisInstance = redisPool.getResource();
            ScanParams params = new ScanParams();
            params.match(pattern);
            ScanResult<String> result;
            result = redisInstance.scan(0, params);
            list.addAll(result.getResult());
            while (result.getCursor() != 0) {
                result = redisInstance.scan(result.getCursor(), params);
                list.addAll(result.getResult());
            }
            return list;
        } catch (Exception ex) {
            logger.error("Exception when get list key with pattern: " + pattern + " , " + ex.getMessage());
            return null;
        } finally {
            if (redisInstance != null) {
                redisInstance.close();
            }
        }
    }

    public void deleteKey(String key) {
        Jedis redisServer = redisPool.getResource();
        redisServer.del(key);
        redisServer.close();
    }

    public RedisResult deleteKeyMatchPattern(String pattern) {
        RedisResult result = new RedisResult();
        try {
            List<String> listKey = getKeyMatchPattern(pattern);
            result = deleteList(listKey);
            return result;
        } catch (Exception ex) {
            logger.error("Exception when delete key with pattern: " + pattern + " , " + ex.getMessage());
            result.setResponse(RedisResponse.FAILURE);
            result.setMessage(ex.getMessage());
            return result;
        }
    }

    public List<String> getKeyWithPrefix(String prefix) {
        List<String> listKey = getKeyMatchPattern(prefix + "*");
        return listKey;
    }

    public RedisResult deleteKeyWithPrefix(String prefix) {
        RedisResult result = deleteKeyMatchPattern(prefix + "*");
        return result;
    }

    @Override
    public String toString() {
        return "RedisClient{" + "serverAddress=" + server + ", port=" + port + ", password=" + password
                + "serverAddressNotify=" + serverNotify + ", portNotify=" + portNotify + ", passwordNotify=" + passwordNotify + '}';
    }

    public Object get(String key) {
        byte[] result = getByteArr(key);
        if (result != null) {
            return conf.asObject(result);
        } else {
            return null;
        }
    }
}
