package com.pricetolight.app.base;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;

import com.pricetolight.app.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityLifeCycleBookKeeper implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = ActivityLifeCycleBookKeeper.class.getSimpleName();

    public List<Activity> created = new ArrayList<>();
    private List<Activity> started = new ArrayList<>();
    private Activity resumed = new Activity();

    private boolean isLoggedIn;
    private boolean shouldShowLoggedOutSnackbarOnNextResumedActivity = false;

    public ActivityLifeCycleBookKeeper() {
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity instanceof AuthRequired && !isLoggedIn) {
            Log.i(TAG, String.format("Killing: %s", getSimpleName(activity)));
            if(created.isEmpty()) {
                // If there's no valid activities in the pipe, we open login activity
                activity.startActivity(new Intent(activity, LoginActivity.class));
            }
            activity.finish();
            shouldShowLoggedOutSnackbarOnNextResumedActivity = true;
        } else {
            Log.i(TAG, String.format("Add %s to created", getSimpleName(activity)));
            created.add(activity);
        }
    }

    private String getSimpleName(Activity activity) {
        return activity.getClass().getSimpleName();
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activity instanceof AuthRequired && !isLoggedIn) {
            Log.i(TAG, String.format("Killing: %s", getSimpleName(activity)));
            if(started.isEmpty()) {
                // If there's no valid activities in the pipe, we open login activity
                activity.startActivity(new Intent(activity, LoginActivity.class));
            }
            activity.finish();
            shouldShowLoggedOutSnackbarOnNextResumedActivity = true;
        } else {
            Log.v(TAG, String.format("Add %s to started", getSimpleName(activity)));
            started.add(activity);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        resumed = activity;
        if (shouldShowLoggedOutSnackbarOnNextResumedActivity) {
            shouldShowLoggedOutSnackbarOnNextResumedActivity = false;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        resumed = null;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        started.remove(activity);
        Log.v("Removed %s to started", getSimpleName(activity));
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.i(TAG, String.format("Removed %s from created", getSimpleName(activity)));
        created.remove(activity);
    }

    @Nullable
    public Activity getResumedActivity() {
        return resumed;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        boolean previous = this.isLoggedIn;
        this.isLoggedIn = isLoggedIn;
        if (this.isLoggedIn) {
            shouldShowLoggedOutSnackbarOnNextResumedActivity = false;
        } else if (previous) {
            Log.i(TAG, String.format("Check for activities that requires login, %d pending", created.size()));
            int finished = 0;
            int createAtStart = created.size();
            for (int i = created.size() - 1; i >= 0; i--) {
                Activity activity = created.get(i);
                Log.i(TAG, String.format("Checking: %s", getSimpleName(activity)));
                if (activity instanceof AuthRequired) {
                    Log.i(TAG, String.format("Killing: %s", getSimpleName(activity)));
                    finished++;
                    if(finished == createAtStart) {
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                    }
                    activity.finish();
                    shouldShowLoggedOutSnackbarOnNextResumedActivity = true;
                } else {
                    if (!activity.isFinishing()) {
                        break;
                    } else {
                        Log.v(TAG, String.format("%s, is finishing. Moving on...", getSimpleName(activity)));
                    }
                }
            }


        }
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}
