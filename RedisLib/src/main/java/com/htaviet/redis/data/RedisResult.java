package com.htaviet.redis.data;

/**
 * Created by Pham Thi Mai Hoa on 5/22/2017.
 */
public class RedisResult {
    private RedisResponse response;//SUCCESS/FAIL
    private String message;//mo ta khi co loi xay ra

    public RedisResult() {
        //contructor
        this.response = RedisResponse.SUCCESS;
        this.message = "";
    }

    public RedisResult(RedisResponse response) {
        this.response = response;
    }

    public RedisResult(RedisResponse response, String message) {
        this.response = response;
        this.message = message;
    }

    public RedisResponse getResponse() {
        return response;
    }

    public void setResponse(RedisResponse response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "RedisResult{" + "response=" + response + ", message=" + message + '}';
    }
}
