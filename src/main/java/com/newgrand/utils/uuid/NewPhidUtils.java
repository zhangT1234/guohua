package com.newgrand.utils.uuid;

/**
 * @author 14736
 * @className GetPhidUtils
 * @description:
 * @date 2022/3/24 11:10
 */
public class NewPhidUtils {
    static SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);

    public static String getPhid() {
        Long phid = idWorker.nextId();
        return phid.toString();
    }

    public static Long getLPhid() {
        return idWorker.nextId();
    }
}
