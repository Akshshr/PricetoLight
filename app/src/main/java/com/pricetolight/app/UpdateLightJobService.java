package com.pricetolight.app;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.graphics.Color;
import android.util.Log;

import com.google.gson.Gson;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.pricetolight.BuildConfig;
import com.pricetolight.api.Api;
import com.pricetolight.api.modal.Home;
import com.pricetolight.app.base.AppCache;
import com.pricetolight.app.base.AppPreferences;
import com.pricetolight.app.base.UserManager;
import com.pricetolight.app.util.IntentKeys;


public class UpdateLightJobService extends JobService {

    Api api;
    private AppPreferences appPreferences;
    private UserManager userManager;
    AppCache appCache;
    PHLight phLight;
    public static final String TAG = UpdateLightJobService.class.getSimpleName();
    private boolean jobCancelled;

    @Override
    public void onCreate() {
        super.onCreate();
        this.appPreferences = new AppPreferences(this);
        this.userManager = new UserManager(appPreferences, appCache);
        this.api = new Api(BuildConfig.API_HOST, userManager);
    }

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

                Log.d(TAG, "Job background task");
                api.getPriceApiService().getPrice(appPreferences.getActiveHomeId().get())
                        .subscribe(this::onPrice,
                                this::handleError);
            }

            private void handleError(Throwable throwable) {
                Log.d(TAG, "Job background API Error" + throwable);
            }

            private void onPrice(Home home) {

                String json = appPreferences.getLightData().get();

                if(json!=null) {
                    phLight = new Gson().fromJson(json, PHLight.class);

                    Log.d(TAG, "Job background RESULT SUCCESS");
                    PHHueSDK phHueSDK = PHHueSDK.getInstance();

                    int priceColor = home.getCurrentSubscription().getPriceInfo().getCurrent().getLevel().getLevelColor(UpdateLightJobService.this);

                    float xy[] = PHUtilities.calculateXYFromRGB(Color.red(priceColor), Color.green(priceColor), Color.blue(priceColor), phLight.getModelNumber());
                    PHLightState lightState = new PHLightState();
                    lightState.setX(xy[0]);
                    lightState.setY(xy[1]);
                    phHueSDK.getSelectedBridge().updateLightState(phLight, lightState);
                }
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
