package com.pricetolight.app.base;

import android.support.annotation.Keep;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.pricetolight.R;
import com.pricetolight.api.Api;
import com.pricetolight.app.PriceToLightsApplication;
import com.pricetolight.app.util.UI;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();

    public enum ActivityEvent {
        CREATE,
        DESTROY,
        START,
        STOP,
    }

    public Api getApi() {
        return ((PriceToLightsApplication) getApplication()).getApi();
    }

    public AppPreferences getAppPreferences() {
        return ((PriceToLightsApplication) getApplication()).getAppPreferences();
    }

    public UserManager getUserManager() {
        return ((PriceToLightsApplication) getApplication()).getUserManager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override
    protected void onStop() {
        lifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        super.onDestroy();
    }

    @Keep
    public void handleError(Throwable throwable) {
        Log.w(TAG, "An error occured and handled by genering error message", throwable);
        if (UI.byId(this, R.id.coordinator) == null) {
            Log.e(TAG, "No CoordinatorLayout attached with ID = R.id.coordinator => we aren't able to show error message");
            return;
        }

        //TODO Show Error message here in SnackBar
    }

    public Observable<ActivityEvent> getLifecycleEvents(ActivityEvent event) {
        return lifecycleSubject.filter((ActivityEvent activityEvent) -> activityEvent == event);
    }

}
