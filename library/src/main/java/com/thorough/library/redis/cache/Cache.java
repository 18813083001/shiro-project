package com.thorough.library.redis.cache;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Cache {

    public String get(String key);
    public String set(String key, String value, int cacheSeconds);
    public void set(Object key, Object value);

    public void expire(String key, int second);
    public void expire(Object key, int second);

    /**
     * 删除缓存
     * @param key 键
     * @return
     */
    public long del(String key);

    /**
     * 缓存是否存在
     * @param key 键
     * @return
     */
    public boolean exists(String key);

    /**
     * 获取byte[]类型Key
     * @param object
     * @return
     */
    public byte[] getBytesKey(Object object);

    /**
     * 获取byte[]类型Key
     * @param key
     * @return
     */
    public Object getObjectKey(byte[] key);

    /**
     * Object转换byte[]类型
     * @param object
     * @return
     */
    public byte[] toBytes(Object object);

    /**
     * byte[]型转换Object
     * @param bytes
     * @return
     */
    public Object toObject(byte[] bytes);

    public void hdel(Object key);

    public byte[] hget(Object key, Object field);
    public String hget(String key, String field);
    public boolean hexists(String key, String field);
    public boolean hexists(Object key, Object field);

    public Set<byte[]> hkeys(Object key);

    public void hset(Object key, Object field, Object value);
    public void hset(String key, String field, String value);

    public void hdel(Object key, Object field);
    public void hdel(String key, String field);

    public Long hlen(Object key);

    public Collection<byte[]> hvals(Object key);

    public byte[] get(Object key);

    public Map<String, String> hgetAll(String key);
    public Map<byte[], byte[]> hgetAll(Object key);

    /**
     * 判断Map缓存中的Key是否存在
     * @param key 键
     * @param mapKey 值
     * @return
     */
    public boolean mapExists(String key, String mapKey);
    public boolean mapObjectExists(Object key, Object mapKey);

    public String set(final String key, final String value, final String nxxx, final String expx,
                      final int time);

    public Object eval(String script, List<String> keys, List<String> args);

    /*list*/

    public long lpush(String key, String... string);
    public long rpush(String key, String... string);
	public long llen(String key);
	public long lrem(String key, long count, String value);
	public List<String> lrange(String key, long start, long end);

}
