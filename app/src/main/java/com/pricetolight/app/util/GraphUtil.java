package com.pricetolight.app.util;

public class GraphUtil {

    public static int roundedLimit(int minValue, int maxValue, int dividableBy) {
        return (int) (Math.ceil((maxValue - minValue) / (float) dividableBy)) * dividableBy + minValue;
    }


}
