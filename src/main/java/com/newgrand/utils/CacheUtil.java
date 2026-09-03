package com.newgrand.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheUtil {

    private static final Map<String, CacheInfo> cacheMap = new ConcurrentHashMap<>();

    public static CacheInfo get(String key) {
        CacheInfo value = cacheMap.get(key);
        if (value == null) {
            return null;
        }
        if (isExpired(value)) {
            cacheMap.remove(key, value);
            return null;
        }
        return value;
    }

    public static void put(String key, CacheInfo value) {
        if (value == null) {
            cacheMap.remove(key);
            return;
        }
        cacheMap.put(key, value);
    }

    /**
     * 仅当 key 不存在或已过期时写入，保证不重复
     */
    public static boolean putIfAbsent(String key, CacheInfo value) {
        if (value == null) {
            return false;
        }
        CacheInfo existing = cacheMap.get(key);
        if (existing != null && isExpired(existing)) {
            cacheMap.remove(key, existing);
        }
        return cacheMap.putIfAbsent(key, value) == null;
    }

    public static void remove(String key) {
        cacheMap.remove(key);
    }

    public static boolean hasKey(String key) {
        return get(key) != null;
    }

    private static boolean isExpired(CacheInfo value) {
        Long validTime = value.getValidTime();
        return validTime != null && System.currentTimeMillis() > validTime;
    }

}
