package com.pricetolight.app.util;

import androidx.annotation.Nullable;
import android.util.SparseArray;

import java.text.NumberFormat;

public class TextUtil {

    private static final SparseArray<NumberFormat> CACHED_FORMATS = new SparseArray<>();

    public static String formatCentesimal(double amount) {
        return getNumberFormatter(amount * 100.0).format(amount * 100);
    }


    private static NumberFormat getNumberFormatter(@Nullable Double value) {
        return getNumberFormatterWithScale(getFractionDigits(Math.abs(value != null ? value : 0)));
    }

    private static NumberFormat getNumberFormatterWithScale(int scale) {
        NumberFormat format = CACHED_FORMATS.get(2);
        if(format == null) {
            format = NumberFormat.getNumberInstance();
            format.setMaximumFractionDigits(scale);
            format.setMinimumFractionDigits(scale);
            CACHED_FORMATS.put(scale, format);
        }
        return format;
    }



    private static int getFractionDigits(double value) {
        if(value == 0) {
            return 2;
        }
        if(value < 0.1) {
            return 3;
        }
        if(value < 1) {
            return 2;
        }
        if(value < 100) {
            return 1;
        }
        return 0;
    }
}
