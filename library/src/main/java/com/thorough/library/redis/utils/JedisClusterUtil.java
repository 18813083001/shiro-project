package com.thorough.library.redis.utils;


import com.thorough.library.redis.cache.Cache;
import com.thorough.library.utils.ObjectUtils;
import com.thorough.library.utils.ApplicationContextHolder;
import com.thorough.library.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JedisClusterUtil implements Cache {

    private static Logger logger = LoggerFactory.getLogger(JedisClusterUtil.class);

    //线上集群
    private static JedisCluster jedisCluster = ApplicationContextHolder.getBean(JedisCluster.class);

    /**
     * 获取缓存
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        String value = null;
        try {
            if (jedisCluster.exists(key)) {
                value = jedisCluster.get(key);
                value = StringUtils.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
                logger.debug("get {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("get {} = {}", key, value, e);
        } finally {
        }
        return value;
    }


    /**
     * 设置缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public String set(String key, String value, int cacheSeconds) {
        String result = null;
        try {
            result = jedisCluster.set(key, value);
            if (cacheSeconds != 0) {
                jedisCluster.expire(key, cacheSeconds);
            }
            logger.debug("set {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("set {} = {}", key, value, e);
        } finally {
        }
        return result;
    }

    public void set(Object key,Object value){
        jedisCluster.set(get(key),getBytesKey(value));
    }

    public void expire(String key,int second){
        jedisCluster.expire(key,second);
    }

    @Override
    public void expire(Object key, int second) {
        jedisCluster.expire(getBytesKey(key),second);
    }

    /**
     * 删除缓存
     * @param key 键
     * @return
     */
    public long del(String key) {
        long result = 0;
        try {
            if (jedisCluster.exists(key)){
                result = jedisCluster.del(key);
                logger.debug("del {}", key);
            }else{
                logger.debug("del {} not exists", key);
            }
        } catch (Exception e) {
            logger.warn("del {}", key, e);
        } finally {
        }
        return result;
    }

    /**
     * 缓存是否存在
     * @param key 键
     * @return
     */
    public boolean exists(String key) {
        boolean result = false;
        try {
            result = jedisCluster.exists(key);
            logger.debug("exists {}", key);
        } catch (Exception e) {
            logger.warn("exists {}", key, e);
        } finally {
        }
        return result;
    }

    /**
     * 获取byte[]类型Key
     * @param object
     * @return
     */
    public byte[] getBytesKey(Object object){
        if(object instanceof String){
            return StringUtils.getBytes((String)object);
        }else{
            return ObjectUtils.serialize(object);
        }
    }

    /**
     * 获取byte[]类型Key
     * @param key
     * @return
     */
    public Object getObjectKey(byte[] key){
        try{
            return StringUtils.toString(key);
        }catch(UnsupportedOperationException uoe){
            try{
                return toObject(key);
            }catch(UnsupportedOperationException uoe2){
                uoe2.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Object转换byte[]类型
     * @param object
     * @return
     */
    public byte[] toBytes(Object object){
        return ObjectUtils.serialize(object);
    }

    /**
     * byte[]型转换Object
     * @param bytes
     * @return
     */
    public Object toObject(byte[] bytes){
        return ObjectUtils.unserialize(bytes);
    }

    public void hdel(Object key){
        jedisCluster.hdel(getBytesKey(key));
    }

    public byte[] hget(Object key,Object field){
       byte[] value = jedisCluster.hget(getBytesKey(key),getBytesKey(field));
       return value;
    }

    @Override
    public String hget(String key, String field) {
        String value = jedisCluster.hget(key,field);
        return value;
    }

    @Override
    public boolean hexists(String key, String field) {
        boolean exist = jedisCluster.hexists(key,field);
        return exist;
    }

    @Override
    public boolean hexists(Object key, Object field) {
        boolean exist = jedisCluster.hexists(getBytesKey(key),getBytesKey(field));
        return exist;
    }

    public Set<byte[]> hkeys(Object key){
        Set<byte[]> sets = jedisCluster.hkeys(getBytesKey(key));
        return  sets;
    }

    public void hset(Object key,Object field,Object value){
         jedisCluster.hset(getBytesKey(key),getBytesKey(field),getBytesKey(value));
    }

    @Override
    public void hset(String key, String field, String value) {
        jedisCluster.hset(key,field,value);
    }

    public void hdel(Object key,Object field){
        jedisCluster.hdel(getBytesKey(key),getBytesKey(field));
    }

    @Override
    public void hdel(String key, String field) {
        jedisCluster.hdel(key,field);
    }

    public Long hlen(Object key){
        Long len = jedisCluster.hlen(getBytesKey(key));
        return len;
    }

    public Collection<byte[]> hvals(Object key){
        Collection<byte[]> hvals = jedisCluster.hvals(getBytesKey(key));
        return hvals;
    }

    public byte[] get(Object key){
        byte[] value = jedisCluster.get(getBytesKey(key));
        return value;
    }

    public Map<String, String> hgetAll(String key){
        Map<String, String> mapAll = jedisCluster.hgetAll(key);
        return  mapAll;
    }

    @Override
    public Map<byte[], byte[]> hgetAll(Object key) {
        return jedisCluster.hgetAll(getBytesKey(key));
    }

    @Override
    public boolean mapExists(String key, String mapKey) {
        return jedisCluster.hexists(key,mapKey);
    }

    @Override
    public boolean mapObjectExists(Object key, Object mapKey) {
        return jedisCluster.hexists(getBytesKey(key),getBytesKey(mapKey));
    }

    @Override
    public String set(String key, String value, String nxxx, String expx, int time) {
        return jedisCluster.set(key,value,nxxx,expx,time);
    }

    @Override
    public Object eval(String script, List<String> keys, List<String> args) {
        return jedisCluster.eval(script,keys,args);
    }

    @Override
    public long lpush(String key, String... string) {
        return jedisCluster.lpush(key,string);
    }

    @Override
    public long rpush(String key, String... string) {
        return jedisCluster.rpush(key,string);
    }

    @Override
    public long llen(String key) {
        return jedisCluster.llen(key);
    }

    @Override
    public long lrem(String key, long count, String value) {
        return jedisCluster.lrem(key,count,value);
    }

    @Override
    public List<String> lrange(String key, long start, long end) {
        return jedisCluster.lrange(key,start,end);
    }

}
