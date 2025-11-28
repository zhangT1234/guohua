package com.newgrand.utils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class BigDecimalUtil {
    public static BigDecimal toSpecial(BigDecimal data) {
        BigDecimal tmpData = data.stripTrailingZeros();
        List<BigDecimal> normal = Arrays.asList(new BigDecimal("0.015"), new BigDecimal("0.03"), new BigDecimal("0.05"), new BigDecimal("0.06"), new BigDecimal("0.09"), new BigDecimal("0.13"));
        if(normal.contains(tmpData)) {
            tmpData = tmpData.multiply(new BigDecimal(100));
        }
        return tmpData;
    }
}
