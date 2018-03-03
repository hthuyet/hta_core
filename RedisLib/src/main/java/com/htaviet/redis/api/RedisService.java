package com.htaviet.redis.api;

import com.htaviet.redis.data.RedisResult;

import java.util.List;
import java.util.Map;

/**
 * Created by Pham Thi Mai Hoa on 5/22/2017.
 */
public interface RedisService {

    RedisResult insertOrUpdate(String key, Object object);

    RedisResult insertOrUpdate(Map<String, Object> keyMap);

    Object getObject(String key);

    List<Object> getObjectList(List<String> keyList);

    RedisResult delete(String key);

    RedisResult deleteList(List<String> keyList);

    RedisResult setExpire(String key, int seconds);

    RedisResult setExpireList(Map<String, Integer> keyTime);

    Long incrSeq(String Id);
}
