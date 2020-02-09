package com.pricetolight.app.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.CheckResult;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.io.Serializable;

public class UI {
    /**
     * Shorthand functions to move away from e.g. Butterknife.findById and make code lines shorter
     */

    @SuppressWarnings({ "unchecked", "UnusedDeclaration" }) // Checked by runtime cast. Public API.
    @CheckResult
    public static <T extends Serializable> T serializable(@NonNull Activity activity, String key) {
        return (T) activity.getIntent().getSerializableExtra(key);
    }

    @SuppressWarnings({ "unchecked", "UnusedDeclaration" }) // Checked by runtime cast. Public API.
    @CheckResult
    public static <T extends Serializable> T serializable(@NonNull Bundle bundle, String key) {
        return (T) bundle.getSerializable(key);
    }

    @SuppressWarnings({ "unchecked", "UnusedDeclaration" }) // Checked by runtime cast. Public API.
    @CheckResult
    public static <T extends Serializable> T serializable(@NonNull Intent bundle, String key) {
        return (T) bundle.getSerializableExtra(key);
    }

    @SuppressWarnings({ "unchecked", "UnusedDeclaration" }) // Checked by runtime cast. Public API.
    @CheckResult
    public static <T extends View> T byId(@NonNull Activity activity, @IdRes int id) {
        return (T) activity.findViewById(id);
    }

    @SuppressWarnings({ "unchecked", "UnusedDeclaration" }) // Checked by runtime cast. Public API.
    @CheckResult
    public static <T extends View> T byId(@NonNull View view, @IdRes int id) {
        return (T) view.findViewById(id);
    }

    public static int screenWidth(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int screenHeight(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

}
