package com.newgrand.utils.i8util;

/**
 * @Author: zhanglixin
 * @Data: 2022/9/12 16:38
 * @Description: TODO
 */
public class StringHelper {
    public static String nullToEmpty(Object obj) {
        if (obj == null) {
            return "";
        } else {
            return obj.toString();
        }
    }
}
