package com.newgrand.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheUtil {

    private static final Map<String, CacheInfo> cacheMap = new ConcurrentHashMap<>();

    public static CacheInfo get(String key) {
        return cacheMap.get(key);
    }

    public static void put(String key, CacheInfo value) {
        if (value == null) {
            cacheMap.remove(key);
            return;
        }
        cacheMap.put(key, value);
    }

    public static void remove(String key) {
        cacheMap.remove(key);
    }

    public static boolean hasKey(String key) {
        return cacheMap.containsKey(key);
    }

}
