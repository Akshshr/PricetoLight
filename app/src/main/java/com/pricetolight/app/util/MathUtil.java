package com.pricetolight.app.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class MathUtil {

    public static long clamp(long val, long min, long max) {
        return val < min ? min : val > max ? max : val;
    }

    public static int clamp(int val, int min, int max) {
        return val < min ? min : val > max ? max : val;
    }

    public static float clamp(float val, float min, float max) {
        return val < min ? min : val > max ? max : val;
    }

    // val is the value that need to check
    // tostart and tostop is the value that returned the vaules in between
    // from stop in the value from which we will calucalted start and stop value
    // fromstart is the interval that will calculated with respect tp fromstop
    // ex: if fronStop is 12 and fromStart is 4 then this will calculate values in between 12-4=8 and 12+4=16
    public static float mapClamp(float val, float fromStart, float fromStop,
                                 float toStart, float toStop) {
        if (fromStart == fromStop) {
            return toStart;
        }
        return clamp(map(val, fromStart, fromStop, toStart, toStop),
                toStart < toStop ? toStart : toStop,
                toStart < toStop ? toStop : toStart
        );
    }

    /**
     * Map value from in range [fromStart, fromStop] to new range [toStart, toStop].
     * Example map(2, 0, 10, 0, 100) -> 20
     *
     * @param val
     * @param fromStart
     * @param fromStop
     * @param toStart
     * @param toStop
     * @return value in new range.
     */
    public static float map(float val, float fromStart, float fromStop, float toStart, float toStop) {
        if (fromStart == fromStop) {
            return toStart;
        }
        return toStart + (toStop - toStart) * ((val - fromStart) / (fromStop - fromStart));
    }

    public static int map(int val, int fromStart, int fromStop, int toStart, int toStop) {
        return Math.round(toStart + (toStop - toStart)
                * ((val - fromStart) / (float) (fromStop - fromStart)));
    }

    public static long map(long val, long fromStart, long fromStop, long toStart, long toStop) {
        return Math.round(toStart + (toStop - toStart)
                * ((val - fromStart) / (double) (fromStop - fromStart)));
    }

    public static double map(double val, double fromStart, double fromStop, double toStart, double toStop) {
        return toStart + (toStop - toStart)
                * ((val - fromStart) / (fromStop - fromStart));
    }

    public static float lerp(float progress, float from, float to) {
        if (from == to) {
            return from;
        }
        return from + (to - from) * progress;
    }

    public static float lerpClamp(float progress, float from, float to) {
        if (from == to) {
            return from;
        }
        return clamp(from + (to - from) * progress, from < to ? from : to, from < to ? to : from);
    }

    public static double lerp(double progress, double from, double to) {
        if (from == to) {
            return from;
        }
        return from + (to - from) * progress;
    }

    public static PointF lerp(float progress, PointF from, PointF to) {

        return lerp(progress, from, to, null);
    }

    public static PointF lerp(float progress, PointF from, PointF to, @Nullable PointF target) {
        if (from.x == to.x && from.y == to.y) {
            return from;
        }
        if (target == null) {
            return new PointF(lerp(progress, from.x, to.x), lerp(progress, from.y, to.y));
        }
        target.set(lerp(progress, from.x, to.x), lerp(progress, from.y, to.y));
        return target;
    }

    public static int roundTo(int i, int v) {
        return Math.round(i / (float) v) * v;
    }

    public static @ColorInt
    int lerpColor(float progress, @ColorInt int fromColor, @ColorInt int toColor) {
        progress = clamp(progress, 0, 1);
        final int r1 = Color.red(fromColor);
        final int g1 = Color.green(fromColor);
        final int b1 = Color.blue(fromColor);
        final int a1 = Color.alpha(fromColor);
        final int r2 = Color.red(toColor);
        final int g2 = Color.green(toColor);
        final int b2 = Color.blue(toColor);
        final int a2 = Color.alpha(toColor);

        return Color.argb(
                Math.round(lerp(progress, a1, a2)),
                Math.round(lerp(progress, r1, r2)),
                Math.round(lerp(progress, g1, g2)),
                Math.round(lerp(progress, b1, b2)));
    }

    public static @ColorInt
    int mapResColor(
            Context context, float progress, float from, float to, @ColorRes int fromColor, @ColorRes int toColor) {
        return lerpColor(MathUtil.map(progress, from, to, 0, 1),
                ContextCompat.getColor(context, fromColor),
                ContextCompat.getColor(context, toColor));
    }

    public static @ColorInt
    int lerpResColor(
            Context context, float progress, @ColorRes int fromColor, @ColorRes int toColor) {
        return lerpColor(progress,
                ContextCompat.getColor(context, fromColor),
                ContextCompat.getColor(context, toColor));
    }

    public static float distance(PointF pointA, PointF pointB) {
        return (float) Math.sqrt(Math.pow(pointA.x - pointB.x, 2) + Math.pow(pointA.y - pointB.y, 2));
    }

    // 09-10 ....01-02....12-13....01-02
    // 01.00 - 02.00
    public static String numberTwoDecimalplaces(float x) {
        return String.valueOf(x).substring(0, String.valueOf(x).indexOf("."));
    }
}