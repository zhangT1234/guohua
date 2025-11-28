package com.newgrand.utils.security;

import java.math.BigDecimal;

public class DecimalUtils {
    public static BigDecimal stringToBigDecimal (String str) {
        if (str == null || str.trim().isEmpty()) {
            return BigDecimal.ZERO; // 或者抛出异常
        }

        try {
            return new BigDecimal(str.trim());
        } catch (NumberFormatException e) {
            System.err.println("无法转换的字符串: " + str);
            return BigDecimal.ZERO; // 或者抛出异常
            // throw new IllegalArgumentException("无效的数字格式: " + str, e);
        }
    }
}
