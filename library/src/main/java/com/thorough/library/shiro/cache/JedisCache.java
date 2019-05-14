package com.thorough.library.shiro.cache;

import com.google.common.collect.Sets;
import com.thorough.library.redis.utils.RedisUtils;
import com.thorough.library.utils.Servlets;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * 自定义授权缓存管理类
 * @author ThinkGem
 * @version 2014-7-20
 */
public class JedisCache<K, V> implements Cache<K, V> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String cacheKeyName = null;

    public JedisCache(String cacheKeyName) {
        this.cacheKeyName = cacheKeyName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(K key) throws CacheException {
        if (key == null){
            return null;
        }

        V v = null;
        HttpServletRequest request = Servlets.getRequest();
        if (request != null){
//				v = (V)request.getAttribute(cacheKeyName);
//				if (v != null){
//					return v;
//				}
        }

        V value = null;
        try {
            value = (V) RedisUtils.toObject(RedisUtils.hget(cacheKeyName,key));
            logger.debug("get {} {} {}", cacheKeyName, key, request != null ? request.getRequestURI() : "");
        } catch (Exception e) {
            logger.error("get {} {} {}", cacheKeyName, key, request != null ? request.getRequestURI() : "", e);
        } finally {
        }

        if (request != null && value != null){
            request.setAttribute(cacheKeyName, value);
        }

        return value;
    }

    @Override
    public V put(K key, V value) throws CacheException {
        if (key == null){
            return null;
        }

        try {
            RedisUtils.hset(cacheKeyName,key,value);
            logger.debug("put {} {} = {}", cacheKeyName, key, value);
        } catch (Exception e) {
            logger.error("put {} {}", cacheKeyName, key, e);
        } finally {
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V remove(K key) throws CacheException {
        V value = null;
        try {
            value = (V) RedisUtils.toObject(RedisUtils.hget(cacheKeyName,key));

            RedisUtils.hdel(cacheKeyName, key);
            logger.debug("remove {} {}", cacheKeyName, key);
        } catch (Exception e) {
            logger.warn("remove {} {}", cacheKeyName, key, e);
        } finally {
        }
        return value;
    }

    @Override
    public void clear() throws CacheException {

        try {
            RedisUtils.hdel(cacheKeyName);
            logger.debug("clear {}", cacheKeyName);
        } catch (Exception e) {
            logger.error("clear {}", cacheKeyName, e);
        } finally {
        }
    }

    @Override
    public int size() {
        int size = 0;
        try {
            size = RedisUtils.hlen(cacheKeyName).intValue();
            logger.debug("size {} {} ", cacheKeyName, size);
            return size;
        } catch (Exception e) {
            logger.error("clear {}",  cacheKeyName, e);
        } finally {
        }
        return size;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<K> keys() {
        Set<K> keys = Sets.newHashSet();
        try {

            Set<byte[]> set = RedisUtils.hkeys(cacheKeyName);
            for(byte[] key : set){
                Object obj = (K) RedisUtils.getObjectKey(key);
                if (obj != null){
                    keys.add((K) obj);
                }
            }
            logger.debug("keys {} {} ", cacheKeyName, keys);
            return keys;
        } catch (Exception e) {
            logger.error("keys {}", cacheKeyName, e);
        } finally {

        }
        return keys;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<V> values() {
        Collection<V> vals = Collections.emptyList();;
        try {
            Collection<byte[]> col = RedisUtils.hvals(cacheKeyName);
            for(byte[] val : col){
                Object obj = RedisUtils.toObject(val);
                if (obj != null){
                    vals.add((V) obj);
                }
            }
            logger.debug("values {} {} ", cacheKeyName, vals);
            return vals;
        } catch (Exception e) {
            logger.error("values {}",  cacheKeyName, e);
        } finally {
        }
        return vals;
    }
}
