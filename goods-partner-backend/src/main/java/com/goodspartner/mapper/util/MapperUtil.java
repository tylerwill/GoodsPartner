package com.goodspartner.mapper.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class MapperUtil {
    private static final Integer ROUND_SCALE = 2;

    public static Double getRoundedDouble(Double value) {
        BigDecimal bigValue = BigDecimal.valueOf(value);
        bigValue = bigValue.setScale(ROUND_SCALE, RoundingMode.HALF_UP);
        return bigValue.doubleValue();
    }
}
