package com.newgrand.utils;

import java.util.HashMap;
import java.util.Map;

public class CacheUtil {

    private static Map<String, CacheInfo> cacheMap = new HashMap<>();

    public static CacheInfo get(String key) {
        return cacheMap.get(key);
    }

    public static void put(String key, CacheInfo value) {
        cacheMap.put(key, value);
    }

}
