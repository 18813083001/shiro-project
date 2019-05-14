package com.thorough.library.redis.utils;


import com.google.common.collect.Maps;
import com.thorough.library.redis.cache.Cache;
import com.thorough.library.utils.ApplicationContextHolder;
import com.thorough.library.utils.ObjectUtils;
import com.thorough.library.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SpringRedisUtil implements Cache {

    private  Logger logger = LoggerFactory.getLogger(this.getClass());
    RedisTemplate redisTemplate = (RedisTemplate) ApplicationContextHolder.getBean("redisTemplate");
    StringRedisTemplate stringRedisTemplate = (StringRedisTemplate) ApplicationContextHolder.getBean("stringRedisTemplate");

    @Override
    public String get(String key) {
       return (String) redisTemplate.opsForValue().get(key);
    }

    @Override
    public String set(String key, String value, int cacheSeconds) {
        info();
        redisTemplate.opsForValue().set(key,value,cacheSeconds, TimeUnit.SECONDS);
        return null;
    }

    @Override
    public void set(Object key, Object value) {
        redisTemplate.opsForValue().set(getBytesKey(key),getBytesKey(value));
    }

    @Override
    public void expire(String key, int second) {
        redisTemplate.expire(key,second,TimeUnit.SECONDS);
    }

    @Override
    public void expire(Object key, int second) {
        redisTemplate.expire(getBytesKey(key),second,TimeUnit.SECONDS);
    }


    @Override
    public long del(String key) {
        redisTemplate.delete(key);
        return 0;
    }

    @Override
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public byte[] getBytesKey(Object object) {
        info();
        if(object instanceof String){
            return StringUtils.getBytes((String)object);
        }else{
            return ObjectUtils.serialize(object);
        }
    }

    @Override
    public Object getObjectKey(byte[] key) {
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

    @Override
    public byte[] toBytes(Object object) {
        return ObjectUtils.serialize(object);
    }

    @Override
    public Object toObject(byte[] bytes) {
        return ObjectUtils.unserialize(bytes);
    }

    @Override
    public void hdel(Object key) {
        info();
        redisTemplate.delete(getBytesKey(key));
    }

    @Override
    public byte[] hget(Object key, Object field) {
        info();
        byte[] result = (byte[]) redisTemplate.opsForHash().get(getBytesKey(key),getBytesKey(field));
        return result;
    }

    @Override
    public String hget(String key, String field) {
        String value = stringRedisTemplate.opsForValue().getAndSet(key,field);
        return value;
    }

    @Override
    public boolean hexists(String key, String field) {
        boolean exist = stringRedisTemplate.opsForHash().hasKey(key,field);
        return exist;
    }

    @Override
    public boolean hexists(Object key, Object field) {
        boolean exist = redisTemplate.opsForHash().hasKey(key,field);
        return exist;
    }

    @Override
    public Set<byte[]> hkeys(Object key) {
        info();
        return redisTemplate.opsForHash().keys(getBytesKey(key));
    }

    @Override
    public void hset(Object key, Object field, Object value) {
        info();
        redisTemplate.opsForHash().put(getBytesKey(key),getBytesKey(field),getBytesKey(value));
    }

    @Override
    public void hset(String key, String field, String value) {
        info();
        stringRedisTemplate.opsForHash().put(key,field,value);
    }

    @Override
    public void hdel(Object key, Object field) {
        info();
        redisTemplate.opsForHash().delete(getBytesKey(key),getBytesKey(field));
    }

    @Override
    public void hdel(String key, String field) {
        info();
        redisTemplate.opsForHash().delete(key,field);
    }

    @Override
    public Long hlen(Object key) {

        info();
        return redisTemplate.opsForHash().size(getBytesKey(key));
    }

    @Override
    public Collection<byte[]> hvals(Object key) {
        info();
        return redisTemplate.opsForHash().values(getBytesKey(key));
    }

    @Override
    public byte[] get(Object key) {
        info();
        return (byte[]) redisTemplate.opsForValue().get(getBytesKey(key));
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        info();
        HashOperations<String,String,String> operations = stringRedisTemplate.opsForHash();
        Map<String, String> map = operations.entries(key);
        return map;
    }

    @Override
    public Map<byte[], byte[]> hgetAll(Object key) {
        info();
        return (Map<byte[], byte[]>) redisTemplate.opsForValue().get(getBytesKey(key));
    }

    @Override
    public boolean mapExists(String key, String mapKey) {
        return stringRedisTemplate.opsForHash().hasKey(key,mapKey);
    }


    @Override
    public boolean mapObjectExists(Object key, Object mapKey) {
        return redisTemplate.opsForHash().hasKey(getBytesKey(key),getBytesKey(mapKey));
    }

    @Override
    public String set(String key, String value, String nxxx, String expx, int time) {
//        RedisCallback get = new RedisCallback() {
//            @Override
//            public Object doInRedis(RedisConnection connection) throws DataAccessException {
//                  connection.set(redisTemplate.getDefaultSerializer().serialize(key),redisTemplate.getDefaultSerializer().serialize(value)
//                 ,Expiration.from(time,TimeUnit.MILLISECONDS), RedisStringCommands.SetOption.SET_IF_ABSENT);
//                return value;
//            }
//        };

        boolean result = stringRedisTemplate.opsForValue().setIfAbsent(key,value);
        boolean result2 = stringRedisTemplate.expire(key,time,TimeUnit.MICROSECONDS);
        if (result){
            if (result2)
                return "OK";
            else {
                stringRedisTemplate.delete(key);
                return null;
            }
        }
        else return null;

    }

    @Override
    public Object eval(String script, List<String> keys, List<String> args) {
        DefaultRedisScript<Object> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Object.class);
        return stringRedisTemplate.execute(redisScript,keys,args);
    }

    @Override
    public long lpush(String key, String... string) {
        return stringRedisTemplate.opsForList().leftPushAll(key,string);
    }

    @Override
    public long rpush(String key, String... string) {
        return stringRedisTemplate.opsForList().rightPushAll(key,string);
    }

    @Override
    public long llen(String key) {
        return stringRedisTemplate.opsForList().size(key);
    }

    @Override
    public long lrem(String key, long count, String value) {
        return stringRedisTemplate.opsForList().remove(key,count,value);
    }

    @Override
    public List<String> lrange(String key, long start, long end) {
        return stringRedisTemplate.opsForList().range(key,start,end);
    }

    /**
     * 获取Map缓存
     * @param key 键
     * @return 值
     */
    public  Map<String, String> hgetStringAll(String key) {
        info();
        Map<String, String> value = null;
        if (redisTemplate.hasKey(getBytesKey(key))) {
            value = Maps.newHashMap();
            Map<byte[], byte[]> map = redisTemplate.opsForHash().entries(getBytesKey(key));
            for (Map.Entry<byte[], byte[]> e : map.entrySet()){
                value.put(StringUtils.toString(e.getKey()), StringUtils.toString(e.getValue()));
            }
            logger.debug("getObjectMap {} = {}", key, value);
        }
        return value;
    }

    private void info(){
//        RedisCallback callback = new RedisCallback() {
//            @Override
//            public Object doInRedis(RedisConnection connection) throws DataAccessException {
//                Properties properties = connection.info("clients");
////                System.out.println( properties.getProperty("connected_clients"));
//                System.out.println(properties.getProperty("connected_clients")+"-----"+properties.toString());
//                return null;
//            }
//        };
//        redisTemplate.execute(callback);
    }
}
