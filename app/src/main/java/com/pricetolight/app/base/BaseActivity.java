package com.pricetolight.app.base;

import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;
import com.pricetolight.R;
import com.pricetolight.api.Api;
import com.pricetolight.app.PriceToLightsApplication;
import com.pricetolight.app.main.ConnectHueActivity;
import com.pricetolight.app.util.UI;

import java.util.List;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        showSnackBar(throwable);
        //TODO Show Error message here in SnackBar
    }

    public Observable<ActivityEvent> getLifecycleEvents(ActivityEvent event) {
        return lifecycleSubject.filter((ActivityEvent activityEvent) -> activityEvent == event);
    }

    public void showSnackBar(Throwable throwable){
        View contextView = findViewById(android.R.id.content);
        final Snackbar snackBar = Snackbar.make(contextView, throwable.getMessage(), Snackbar.LENGTH_INDEFINITE);

        snackBar.setAction(this.getResources().getString(R.string.dismiss), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        snackBar.setActionTextColor(ContextCompat.getColor(this, R.color.red300));
        snackBar.show();

    }

}
