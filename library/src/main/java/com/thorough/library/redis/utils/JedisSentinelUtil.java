package com.thorough.library.redis.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.thorough.library.redis.cache.Cache;
import com.thorough.library.utils.ObjectUtils;
import com.thorough.library.utils.ApplicationContextHolder;
import com.thorough.library.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JedisSentinelUtil implements Cache {

    private static Logger logger = LoggerFactory.getLogger(JedisSentinelUtil.class);

    //测试环境集群
    private static JedisSentinelPool jedisPool = ApplicationContextHolder.getBean(JedisSentinelPool.class);

    /**
     * 获取缓存
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        String value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.get(key);
                value = StringUtils.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
                logger.debug("get {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("get {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取缓存
     * @param key 键
     * @return 值
     */
    public Object getObject(String key) {
        Object value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                value = toObject(jedis.get(getBytesKey(key)));
                logger.debug("getObject {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getObject {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
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
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.set(key, value);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("set {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("set {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public String setObject(String key, Object value, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.set(getBytesKey(key), toBytes(value));
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObject {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setObject {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取List缓存
     * @param key 键
     * @return 值
     */
    public List<String> getList(String key) {
        List<String> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.lrange(key, 0, -1);
                logger.debug("getList {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getList {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取List缓存
     * @param key 键
     * @return 值
     */
    public List<Object> getObjectList(String key) {
        List<Object> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                List<byte[]> list = jedis.lrange(getBytesKey(key), 0, -1);
                value = Lists.newArrayList();
                for (byte[] bs : list){
                    value.add(toObject(bs));
                }
                logger.debug("getObjectList {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getObjectList {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置List缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public long setList(String key, List<String> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            result = jedis.rpush(key, (String[])value.toArray());
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setList {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setList {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置List缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public long setObjectList(String key, List<Object> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                jedis.del(key);
            }
            List<byte[]> list = Lists.newArrayList();
            for (Object o : value){
                list.add(toBytes(o));
            }
            result = jedis.rpush(getBytesKey(key), (byte[][])list.toArray());
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObjectList {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setObjectList {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向List缓存中添加值
     * @param key 键
     * @param value 值
     * @return
     */
    public long listAdd(String key, String... value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.rpush(key, value);
            logger.debug("listAdd {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("listAdd {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向List缓存中添加值
     * @param key 键
     * @param value 值
     * @return
     */
    public long listObjectAdd(String key, Object... value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            List<byte[]> list = Lists.newArrayList();
            for (Object o : value){
                list.add(toBytes(o));
            }
            result = jedis.rpush(getBytesKey(key), (byte[][])list.toArray());
            logger.debug("listObjectAdd {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("listObjectAdd {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取缓存
     * @param key 键
     * @return 值
     */
    public Set<String> getSet(String key) {
        Set<String> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.smembers(key);
                logger.debug("getSet {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getSet {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取缓存
     * @param key 键
     * @return 值
     */
    public Set<Object> getObjectSet(String key) {
        Set<Object> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                value = Sets.newHashSet();
                Set<byte[]> set = jedis.smembers(getBytesKey(key));
                for (byte[] bs : set){
                    value.add(toObject(bs));
                }
                logger.debug("getObjectSet {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getObjectSet {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置Set缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public long setSet(String key, Set<String> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            result = jedis.sadd(key, (String[])value.toArray());
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setSet {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setSet {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置Set缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public long setObjectSet(String key, Set<Object> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                jedis.del(key);
            }
            Set<byte[]> set = Sets.newHashSet();
            for (Object o : value){
                set.add(toBytes(o));
            }
            result = jedis.sadd(getBytesKey(key), (byte[][])set.toArray());
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObjectSet {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setObjectSet {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Set缓存中添加值
     * @param key 键
     * @param value 值
     * @return
     */
    public long setSetAdd(String key, String... value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.sadd(key, value);
            logger.debug("setSetAdd {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setSetAdd {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Set缓存中添加值
     * @param key 键
     * @param value 值
     * @return
     */
    public long setSetObjectAdd(String key, Object... value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            Set<byte[]> set = Sets.newHashSet();
            for (Object o : value){
                set.add(toBytes(o));
            }
            result = jedis.rpush(getBytesKey(key), (byte[][])set.toArray());
            logger.debug("setSetObjectAdd {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setSetObjectAdd {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取Map缓存
     * @param key 键
     * @return 值
     */
    public Map<String, String> getMap(String key) {
        Map<String, String> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.hgetAll(key);
                logger.debug("getMap {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getMap {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取Map缓存
     * @param key 键
     * @return 值
     */
    public Map<String, Object> getObjectMap(String key) {
        Map<String, Object> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                value = Maps.newHashMap();
                Map<byte[], byte[]> map = jedis.hgetAll(getBytesKey(key));
                for (Map.Entry<byte[], byte[]> e : map.entrySet()){
                    value.put(StringUtils.toString(e.getKey()), toObject(e.getValue()));
                }
                logger.debug("getObjectMap {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getObjectMap {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置Map缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public String setMap(String key, Map<String, String> value, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            result = jedis.hmset(key, value);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setMap {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setMap {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置Map缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public String setObjectMap(String key, Map<String, Object> value, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                jedis.del(key);
            }
            Map<byte[], byte[]> map = Maps.newHashMap();
            for (Map.Entry<String, Object> e : value.entrySet()){
                map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
            }
            result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>)map);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObjectMap {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setObjectMap {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Map缓存中添加值
     * @param key 键
     * @param value 值
     * @return
     */
    public String mapPut(String key, Map<String, String> value) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hmset(key, value);
            logger.debug("mapPut {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("mapPut {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Map缓存中添加值
     * @param key 键
     * @param value 值
     * @return
     */
    public String mapObjectPut(String key, Map<String, Object> value) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            Map<byte[], byte[]> map = Maps.newHashMap();
            for (Map.Entry<String, Object> e : value.entrySet()){
                map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
            }
            result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>)map);
            logger.debug("mapObjectPut {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("mapObjectPut {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 移除Map缓存中的值
     * @param key 键
     * @param mapKey 值
     * @return
     */
    public long mapRemove(String key, String mapKey) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hdel(key, mapKey);
            logger.debug("mapRemove {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.warn("mapRemove {}  {}", key, mapKey, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 移除Map缓存中的值
     * @param key 键
     * @param mapKey 值
     * @return
     */
    public long mapObjectRemove(String key, String mapKey) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hdel(getBytesKey(key), getBytesKey(mapKey));
            logger.debug("mapObjectRemove {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.warn("mapObjectRemove {}  {}", key, mapKey, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 判断Map缓存中的Key是否存在
     * @param key 键
     * @param mapKey 值
     * @return
     */
    public boolean mapExists(String key, String mapKey) {
        boolean result = false;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hexists(key, mapKey);
            logger.debug("mapExists {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.warn("mapExists {}  {}", key, mapKey, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }


    /**
     * 判断Map缓存中的Key是否存在
     * @param key 键
     * @param mapKey 值
     * @return
     */
    public boolean mapObjectExists(Object key, Object mapKey) {
        boolean result = false;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hexists(getBytesKey(key), getBytesKey(mapKey));
            logger.debug("mapObjectExists {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.warn("mapObjectExists {}  {}", key, mapKey, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    @Override
    public String set(String key, String value, String nxxx, String expx, int time) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.set(key,value,nxxx,expx,time);
        } catch (Exception e) {
            logger.warn("set {}  {} {} {} {}", key, value,nxxx,expx,time, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    @Override
    public Object eval(String script, List<String> keys, List<String> args) {
        Object result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.eval(script,keys,args);
        } catch (Exception e) {
            logger.warn("eval {}  {} {}", script, keys,args,e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    @Override
    public long lpush(String key, String... string) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.lpush(key,string);
        } catch (Exception e) {
            logger.warn("lpush {}  {}", key, string);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    @Override
    public long rpush(String key, String... string) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.rpush(key,string);
        } catch (Exception e) {
            logger.warn("rpush {}  {}", key, string);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    @Override
    public long llen(String key) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.llen(key);
        } catch (Exception e) {
            logger.warn("llen {} ", key);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    @Override
    public long lrem(String key, long count, String value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.lrem(key,count,value);
        } catch (Exception e) {
            logger.warn("lrem {} {} {}", key,count,value);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    @Override
    public List<String> lrange(String key, long start, long end) {
        List<String> result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.lrange(key,start,end);
        } catch (Exception e) {
            logger.warn("lrem {} {} ", key,start,end);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 删除缓存
     * @param key 键
     * @return
     */
    public long del(String key) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)){
                result = jedis.del(key);
                logger.debug("del {}", key);
            }else{
                logger.debug("del {} not exists", key);
            }
        } catch (Exception e) {
            logger.warn("del {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 删除缓存
     * @param key 键
     * @return
     */
    public long delObject(String key) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))){
                result = jedis.del(getBytesKey(key));
                logger.debug("delObject {}", key);
            }else{
                logger.debug("delObject {} not exists", key);
            }
        } catch (Exception e) {
            logger.warn("delObject {}", key, e);
        } finally {
            returnResource(jedis);
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
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.exists(key);
            logger.debug("exists {}", key);
        } catch (Exception e) {
            logger.warn("exists {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 缓存是否存在
     * @param key 键
     * @return
     */
    public boolean existsObject(String key) {
        boolean result = false;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.exists(getBytesKey(key));
            logger.debug("existsObject {}", key);
        } catch (Exception e) {
            logger.warn("existsObject {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取资源
     * @return
     * @throws JedisException
     */
    public Jedis getResource() throws JedisException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
//			logger.debug("getResource.", jedis);
        } catch (JedisException e) {
            logger.warn("getResource.", e);
            returnBrokenResource(jedis);
            throw e;
        }
        return jedis;
    }

    /**
     * 归还资源
     * @param jedis
     * @param
     */
    public void returnBrokenResource(Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnBrokenResource(jedis);
        }
    }

    /**
     * 释放资源
     * @param jedis
     * @param
     */
    public void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnResource(jedis);
        }
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


    public void set(Object key,Object value){

        Jedis jedis = null;
        try {
            jedis = getResource();
            info(jedis);
            jedis.set(getBytesKey(key),getBytesKey(value));
        } catch (JedisException e) {
            logger.warn("set {} {}", key,value, e);
        } finally {
            returnResource(jedis);
        }
    }

    public void expire(String key,int second){

        Jedis jedis = null;
        try {
            jedis = getResource();
            jedis.expire(key,second);
        } catch (JedisException e) {
            logger.warn("expire {} {}", key,second, e);
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    public void expire(Object key, int second) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            info(jedis);
            jedis.expire(getBytesKey(key),second);
        } catch (JedisException e) {
            logger.warn("expire {} {}", key,second, e);
        } finally {
            returnResource(jedis);
        }
    }


    public void hdel(Object key){

        Jedis jedis = null;
        try {
            jedis = getResource();
            info(jedis);
            jedis.hdel(getBytesKey(key));
        } catch (JedisException e) {
            logger.warn("hdel {}", key, e);
        } finally {
            returnResource(jedis);
        }
    }

    public byte[] hget(Object key,Object field){

        Jedis jedis = null;
        byte[] value = null;
        try {
            jedis = getResource();
            info(jedis);
            value = jedis.hget(getBytesKey(key),getBytesKey(field));
        } catch (JedisException e) {
            logger.warn("hget {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    @Override
    public String hget(String key, String field) {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = getResource();
            info(jedis);
            value = jedis.hget(key,field);
        } catch (JedisException e) {
            logger.warn("expire {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    @Override
    public boolean hexists(String key, String field) {
        Jedis jedis = null;
        boolean exist = false;
        try {
            jedis = getResource();
            info(jedis);
            exist = jedis.hexists(key,field);
        } catch (JedisException e) {
            logger.warn("expire {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return exist;
    }

    @Override
    public boolean hexists(Object key, Object field) {
        Jedis jedis = null;
        boolean exist = false;
        try {
            jedis = getResource();
            info(jedis);
            exist = jedis.hexists(getBytesKey(key),getBytesKey(field));
        } catch (JedisException e) {
            logger.warn("expire {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return exist;
    }

    public Set<byte[]> hkeys(Object key){
        Jedis jedis = null;
        Set<byte[]> sets = null;
        try {
            jedis = getResource();
            info(jedis);
            sets = jedis.hkeys(getBytesKey(key));
        } catch (JedisException e) {
            logger.warn("hkeys {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return sets;
    }

    public void hset(Object key,Object field,Object value){

        Jedis jedis = null;
        try {
            jedis = getResource();
            info(jedis);
            jedis.hset(getBytesKey(key),getBytesKey(field),getBytesKey(value));
        } catch (JedisException e) {
            logger.warn("hset {}", key, e);
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    public void hset(String key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            info(jedis);
            jedis.hset(key,field,value);
        } catch (JedisException e) {
            logger.warn("hset {}", key, e);
        } finally {
            returnResource(jedis);
        }
    }

    public void hdel(Object key,Object field){

        Jedis jedis = null;
        try {
            jedis = getResource();
            info(jedis);
            jedis.hdel(getBytesKey(key),getBytesKey(field));
        } catch (JedisException e) {
            logger.warn("hdel {}", key, e);
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    public void hdel(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            info(jedis);
            jedis.hdel(key,field);
        } catch (JedisException e) {
            logger.warn("hdel {}", key, e);
        } finally {
            returnResource(jedis);
        }
    }


    public Long hlen(Object key){

        Jedis jedis = null;
        Long len = 0l;
        try {
            jedis = getResource();
            info(jedis);
            len = jedis.hlen(getBytesKey(key));
        } catch (JedisException e) {
            logger.warn("hlen {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return len;
    }

    public Collection<byte[]> hvals(Object key){

        Jedis jedis = null;
        Collection<byte[]> hvals = null;
        try {
            jedis = getResource();
            info(jedis);
            hvals = jedis.hvals(getBytesKey(key));
        } catch (JedisException e) {
            logger.warn("hvals {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return hvals;
    }

    public byte[] get(Object key){

        Jedis jedis = null;
        byte[] value = null;
        try {
            jedis = getResource();
            info(jedis);
            value = jedis.get(getBytesKey(key));
        } catch (JedisException e) {
            logger.warn("hvals {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    public Map<String, String> hgetAll(String key){

        Jedis jedis = null;
        Map<String, String> mapAll = null;
        try {
            jedis = getResource();
            info(jedis);
            mapAll = jedis.hgetAll(key);
        } catch (JedisException e) {
            logger.warn("hvals {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return  mapAll;
    }

    @Override
    public Map<byte[], byte[]> hgetAll(Object key) {

        Jedis jedis = null;
        Map<byte[], byte[]> mapAll = null;
        try {
            jedis = getResource();
            info(jedis);
            mapAll = jedis.hgetAll(getBytesKey(key));
        } catch (JedisException e) {
            logger.warn("hvals {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return  mapAll;
    }

    private void info(Jedis jedis){
//        try {
//            String string = jedis.info("clients");
//            System.out.println("---: "+string);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

}