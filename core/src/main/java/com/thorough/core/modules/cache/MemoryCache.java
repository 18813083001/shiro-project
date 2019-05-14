package com.thorough.core.modules.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryCache {

    public static Map<String,String> hospitalCodeCache = new ConcurrentHashMap<>();

    public static String putHospitalCode(String key,String value){
        return hospitalCodeCache.put(key,value);
    }

    public static String getHospitalCodeCahce(String key){
        return hospitalCodeCache.get(key);
    }

    public static String deleteHospitalCodeCahce(String key){
        return hospitalCodeCache.remove(key);
    }

}
