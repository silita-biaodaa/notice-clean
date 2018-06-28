package com.silita.biaodaa.common.redis;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Created by dh on 2017/7/24.
 */
@Component
public class RedisUtils {

    private Logger logger = Logger.getLogger(RedisUtils.class);

    public final static String VIRTUAL_COURSE_PREX = "";

    @Autowired
    private ShardedJedisPool shardedJedisPool;//注入ShardedJedisPool

    public String buildKey(String key){
        return VIRTUAL_COURSE_PREX + key;
    }

    private Set<String> getByPrefix(String prefix) {
        Set<String> setResult = new HashSet<>();
        ShardedJedis jedis = null;
        try {
            jedis =  getJedis();
            Iterator<Jedis> jedisIterator = jedis.getAllShards().iterator();
            while(jedisIterator.hasNext()){
                setResult.addAll(jedisIterator.next().keys(prefix+"*"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }finally{
            if(jedis != null) {
                jedis.close();
            }
        }
        return setResult;
    }

    /**
     * 根据前缀模糊匹配所有key，然后批量删除
     * @param prefix 前缀字符
     */
    public void batchDel(String prefix){
        ShardedJedis jedis= null;
        try {
            Set<String> keys = getByPrefix(prefix);
            if(keys != null && keys.size()>0) {
                jedis = getJedis();
                for (String key : keys) {
                    jedis.del(key);
                }
            }
        }catch (Exception e){
            logger.error("batchDel key error : "+e,e);
        }finally{
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 删除元素
     * @param key
     */
    public void del(String key){
        ShardedJedis jedis = null;
        try {
            jedis =  getJedis();
            jedis.del(key);
        }catch (RuntimeException e) {
            logger.error("del key RuntimeException error : "+e,e);
        } catch (Exception e) {
            logger.error("del key error : "+e,e);
        }finally{
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 设置 String
     * @param key
     * @param value
     */
    public void setString(String key ,String value){
        ShardedJedis jedis = null;
        try {
            jedis =  getJedis();
            jedis.set(buildKey(key),value);
        } catch (Exception e) {
            logger.error("Set key error : "+e);
        }finally{
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 设置 过期时间
     * @param key
     * @param value
     * @param seconds 以秒为单位
     */
    public void setString(String key ,String value,int seconds){
        ShardedJedis jedis = null;
        try {
            jedis =  getJedis();
            jedis.setex(buildKey(key), seconds, value);
        } catch (Exception e) {
            logger.error("Set keyex error : "+e);
        }finally{
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 获取String值
     * @param key
     * @return value
     */
    public String getString(String key){
        ShardedJedis jedis = null;
        String value = null;
        try {
            jedis = getJedis();
            String bKey = buildKey(key);
            if (jedis == null || !jedis.exists(bKey)) {
                return null;
            }
            value= jedis.get(bKey);
        }catch (Exception e){
            logger.error(e,e);
        }finally{
            if(jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    public synchronized ShardedJedis getJedis() {
        return shardedJedisPool.getResource();
    }



    public <T> void setList(String key ,List<T> list,int seconds){
        ShardedJedis jedis = null;
        try {
            String bKey = buildKey(key);
            jedis =getJedis();
            jedis.setex(bKey.getBytes(),seconds, ObjectTranscoder.serialize(list));
        } catch (Exception e) {
            logger.error("setList error : "+e);
        }finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public <T> List<T> getList(String key){
        List<T> list = null;
        ShardedJedis jedis = null;
        try {
            String bKey = buildKey(key);
            jedis = getJedis();
            if (jedis == null || !jedis.exists(bKey.getBytes())) {
                return null;
            }
            byte[] in = jedis.get(bKey.getBytes());
            list = (List<T>) ObjectTranscoder.deserialize(in);
        }catch(Exception e){
            logger.error(e,e);
        }finally{
            if(jedis != null) {
                jedis.close();
            }
        }
        return list;
    }



    public void setObject(String key ,Object obj,int seconds){
        ShardedJedis jedis = null;
        try {
            String bKey = buildKey(key);
            jedis =getJedis();
            jedis.setex(bKey.getBytes(),seconds, ObjectTranscoder.serialize(obj));
        } catch (Exception e) {
            logger.error("setObject error : "+e);
        }finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public Object getObject(String key){
        Object obj = null;
        ShardedJedis jedis = null;
        try {
            String bKey = buildKey(key);
            jedis = getJedis();
            if (jedis == null || !jedis.exists(bKey.getBytes())) {
                return null;
            }
            byte[] in = jedis.get(bKey.getBytes());
            obj = ObjectTranscoder.deserialize(in);
        }catch(Exception e){
            logger.error(e,e);
        }finally{
            if(jedis != null) {
                jedis.close();
            }
        }
        return obj;
    }



}
