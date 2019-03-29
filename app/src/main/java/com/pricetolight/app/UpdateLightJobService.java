package com.pricetolight.app;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.graphics.Color;
import android.util.Log;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.pricetolight.api.Api;
import com.pricetolight.api.modal.Home;
import com.pricetolight.app.base.AppPreferences;

/**
 * Created by akashshrivastava on 2019-03-29.
 */

public class UpdateLightJobService extends JobService {

    Api api;
    private AppPreferences appPreferences;

    public Api getApi() {
        return api;
    }

    public AppPreferences getAppPreferences() {
        return appPreferences;
    }

    public static final String TAG = UpdateLightJobService.class.getSimpleName();
    private boolean jobCancelled;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob: ");
        doBackgroundTask(params);
        return true;
    }

    private void doBackgroundTask(JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(jobCancelled){
                    return;
                }

                api.getPriceApiService().getPrice(appPreferences.getActiveHomeId().get())
                        .subscribe(this::onPrice,
                                this::handleError);
            }

            private void handleError(Throwable throwable) {

            }

            private void onPrice(Home home) {
                    PHHueSDK phHueSDK=PHHueSDK.getInstance();

                    //CHeCK WHICH LIGHT IS PICKED
                    int priceColor = home.getCurrentSubscription().getPriceInfo().getCurrent().getLevel().getLevelColor(UpdateLightJobService.this);

//                    float xy[] = PHUtilities.calculateXYFromRGB( Color.red(priceColor), Color.green(priceColor), Color.blue(priceColor), light.getModelNumber());
//                    PHLightState lightState = new PHLightState();
//                    lightState.setX(xy[0]);
//                    lightState.setY(xy[1]);
//                    phHueSDK.getSelectedBridge().updateLightState(light,lightState);
            }

        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob: ");
        jobCancelled = true;
        return true;
    }
}
