package com.newgrand.utils.uuid;

/**
 * @Author: zhanglixin
 * @Data: 2022/9/12 14:31
 * @Description: TODO
 */
public class GetNewPhidUtils {
    static SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);

    public static String getPhid() {
        Long phid = idWorker.nextId();
        return phid.toString();
    }

    public static Long getLPhid() {
        return idWorker.nextId();
    }
}
