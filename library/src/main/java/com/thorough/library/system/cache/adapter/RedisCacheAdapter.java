package com.thorough.library.system.cache.adapter;


import com.thorough.library.shiro.cache.JedisCache;
import com.thorough.library.system.cache.Cache;

public class RedisCacheAdapter extends JedisCache implements Cache {
    public RedisCacheAdapter(String cacheKeyName) {
        super(cacheKeyName);
    }
}
