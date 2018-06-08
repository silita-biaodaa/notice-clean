package com.silita.biaodaa.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * <p>Created by mayongbin01 on 2017/3/9.</p>
 */
public class RedisTest extends ConfigTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void getAndPutTest() {
        //redisTemplate.opsForHash().put("user", "age", "20");
        Object object = redisTemplate.opsForHash().get("user", "age");
        System.out.println(object);
    }






}