package com.thorough.library.redis.utils;

import com.thorough.library.redis.cache.Cache;
import com.thorough.library.utils.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedisUtils {

    private static Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    private static Cache jedisUtils = null;
    static {
        String cluster =  PropertyUtil.getProperty("redis.redisCache");
        if(cluster.equals("onlineCluster"))
            //线上集群
            jedisUtils = new JedisClusterUtil();
        else  if(cluster.equals("devCluster"))
            //测试环境集群
            jedisUtils = new JedisSentinelUtil();
        else  if(cluster.equals("single"))
            //线上单机版和本地单机版
            jedisUtils = new JedisPoolUtil();
        else if(cluster.equals("springRedis"))
            jedisUtils = new SpringRedisUtil();
    }

    public static void set(Object key,Object value){
         jedisUtils.set(key,value);
    }

    public static void expire(String key,int second){
        jedisUtils.expire(key,second);
    }

    public static void expire(Object key,int second){
        jedisUtils.expire(key,second);
    }

    /**
     * 删除缓存
     * @param key 键
     * @return
     */
    public static long del(String key) {
        return jedisUtils.del(key);
    }

    /**
     * 缓存是否存在
     * @param key 键
     * @return
     */
    public static boolean exists(String key) {
         return jedisUtils.exists(key);
    }

    /**
     * 获取byte[]类型Key
     * @param object
     * @return
     */
    public static byte[] getBytesKey(Object object){
         return jedisUtils.getBytesKey(object);
    }

    /**
     * 获取byte[]类型Key
     * @param key
     * @return
     */
    public static Object getObjectKey(byte[] key){
        return jedisUtils.getObjectKey(key);
    }

    /**
     * Object转换byte[]类型
     * @param object
     * @return
     */
    public static byte[] toBytes(Object object){
         return jedisUtils.toBytes(object);
    }

    /**
     * byte[]型转换Object
     * @param bytes
     * @return
     */
    public static Object toObject(byte[] bytes){
         return jedisUtils.toObject(bytes);
    }

    public static void hdel(Object key){
          jedisUtils.hdel(key);
    }

    public static byte[] hget(Object key,Object field){
         return jedisUtils.hget(key,field);
    }

    public static Set<byte[]> hkeys(Object key){
         return jedisUtils.hkeys(key);
    }

    public static void hset(Object key,Object field,Object value){
         jedisUtils.hset(key,field,value);
    }

    public static void hset(String key,String field,String value){
        jedisUtils.hset(key,field,value);
    }

    public static void hdel(Object key,Object field){
         jedisUtils.hdel(key,field);
    }

    public static Long hlen(Object key){
         return jedisUtils.hlen(key);
    }

    public static Collection<byte[]> hvals(Object key){
         return jedisUtils.hvals(key);
    }

    public static String hget(String key, String field) {
       return jedisUtils.hget(key,field);
    }

    public static boolean hexists(String key, String field) {
        return jedisUtils.hexists(key,field);
    }

    public static boolean hexists(Object key, Object field) {
        return jedisUtils.hexists(key,field);
    }

    public static byte[] get(Object key){
         return jedisUtils.get(key);
    }

    public static Map<String, String> hgetAll(String key){
         return jedisUtils.hgetAll(key);
    }

    public static boolean mapExists(String key, String mapKey){
        return jedisUtils.mapExists(key,mapKey);
    }
    public static boolean mapObjectExists(String key, String mapKey){
        return jedisUtils.mapObjectExists(key,mapKey);
    }
    public static String set(final String key, final String value, final String nxxx, final String expx,
                      final int time){
        return jedisUtils.set(key,value,nxxx,expx,time);
    }

    public static Object eval(String script, List<String> keys, List<String> args){
        return jedisUtils.eval(script,keys,args);
    }

    public static long lpush(String key,String... string){
        return jedisUtils.lpush(key,string);
    }
    public static long rpush(String key,String... string){
        return jedisUtils.rpush(key,string);
    }
    public static long llen(String key){
        return jedisUtils.llen(key);
    }
    public static long lrem(String key,long count,String value){
        return jedisUtils.lrem(key,count,value);
    }
    public static List<String> lrange(String key, long start, long end){
        return jedisUtils.lrange(key,start,end);
    }
}
