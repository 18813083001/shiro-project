package com.thorough.library.system.cache;


import com.thorough.library.constant.Constant;
import com.thorough.library.system.cache.adapter.RedisCacheAdapter;
import org.springframework.stereotype.Component;

/**
 * 系统层面用CacheManager
 * 具体的Cache方式可以自定义
 * */
@Component
public class SysCacheManger implements CacheManager{

    private String cacheKeyName = Constant.cacheKeyName;

    @Override
    public <K, V> Cache<K, V> getCache(String name) {
        return new RedisCacheAdapter(cacheKeyName);
    }

    public static void main(String[] args) {
        SysCacheManger sysCacheManger = new SysCacheManger();
        Cache cache = sysCacheManger.getCache("je");
        System.out.println(cache.get("dd"));
    }
}
