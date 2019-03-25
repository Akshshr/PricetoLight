package com.pricetolight.app.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class Util {

    private static final String TAG = Util.class.getSimpleName();

    public static int dpToPx(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static float pxToDp(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static void hideKeyboardFromView(final @Nullable View focusView) {
        if (focusView != null) {
            InputMethodManager imm = (InputMethodManager) focusView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        }
    }


    public static float getCircleProgress(int percentValue) {
        float progress = MathUtil.mapClamp((float) percentValue, 0, 100, 0, 1);
        return MathUtil.lerp(progress, 0.05f, 0.95f);
    }



    @SuppressLint("DefaultLocale")
    public static String getformattedTimeframe() {
        return String.format("%d:00 - %d:00", DateTime.now().getHourOfDay() - 1, DateTime.now().getHourOfDay());
    }






}
