package com.thorough.library.system.cache;

public interface CacheManager {
    <K, V> Cache<K, V> getCache(String name);
}
