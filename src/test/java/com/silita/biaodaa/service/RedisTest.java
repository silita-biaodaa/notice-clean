package com.silita.biaodaa.service;

import com.silita.biaodaa.common.redis.RedisUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>Created by mayongbin01 on 2017/3/9.</p>
 */
public class RedisTest extends ConfigTest {

    @Autowired
    public RedisUtils redisUtils;

    @Test
    public void getAndPutTest() {
        //redisTemplate.opsForHash().put("user", "age", "20");
        Object object = redisUtils.getString("user");
        System.out.println(object);
    }






}