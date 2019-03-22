package com.pricetolight.app;

import android.app.Application;

import com.pricetolight.BuildConfig;
import com.pricetolight.api.Api;
import com.pricetolight.app.base.AppPreferences;
import com.pricetolight.app.base.UserManager;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTimeZone;


public class PriceToLightsApplication extends Application {
    public AppPreferences appPreferences;
    private Api api;
    private UserManager userManager;


    @Override
    public void onCreate() {
        super.onCreate();
        initThirdPartyLibraries();
        this.appPreferences = new AppPreferences(this);
        this.userManager = new UserManager(appPreferences);
        this.api = new Api(BuildConfig.API_HOST, userManager);
        setupActivityLifeCycleBookKeeper(userManager);

    }

    private void initThirdPartyLibraries() {
        JodaTimeAndroid.init(this);
        DateTimeZone.setDefault(DateTimeZone.forID("Europe/Stockholm"));

    }

    private void setupActivityLifeCycleBookKeeper(UserManager userManager) {
//        activityLifeCycleBookKeeper = new ActivityLifeCycleBookKeeper();
//        activityLifeCycleBookKeeper.setLoggedIn(userManager.isLoggedIn());
//        userManager.getTokenObservable().subscribe(token -> activityLifeCycleBookKeeper.setLoggedIn(userManager.isLoggedIn()));
    }

    public Api getApi() {
        return api;
    }

    public AppPreferences getAppPreferences() {
        return appPreferences;
    }

    public UserManager getUserManager() {
        return userManager;
    }

}
