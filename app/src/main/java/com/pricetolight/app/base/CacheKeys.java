package com.pricetolight.app.base;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class CacheKeys {
        //    public static final String GET_CUSTOMER_HOME_hID= "gch-%1$s";
//    public static final String GET_DAY_NIGHT_SCHEDULE__hID = "dns-%1$s";
//    public static final String GET_HOME__hID = "h-%1$s";
//    public static final String GET_ME = "me";
//
        static void validate() {
            Field[] fields = CacheKeys.class.getDeclaredFields();
            ArrayList<String> values = new ArrayList<>();

            for (Field field : fields) {
                String type = field.getType().getSimpleName();
                String name = field.getName();
                if("String".equals(type)) {
                    try {
                        String value = (String) field.get(null);
                        values.add(value);
                        if(values.contains(value)) {
                            throw new IllegalStateException("There appears to be a duplicate cache key for " + name);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
}
