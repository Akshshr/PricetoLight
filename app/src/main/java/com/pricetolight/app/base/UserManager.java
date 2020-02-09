package com.pricetolight.app.base;

import android.content.Context;

import androidx.annotation.Nullable;

import com.pricetolight.api.modal.Home;
import com.pricetolight.api.service.Authenticator;

import rx.Observable;

public class UserManager implements Authenticator {

    private final AppPreferences appPreferences;
    private final AppCache appCache;


    public UserManager(final AppPreferences appPreferences, AppCache appCache) {
        this.appPreferences = appPreferences;
        this.appCache = appCache;
    }

    @Nullable
    @Override
    public String getToken() {
        return appPreferences.getAuthenticationToken().get();
    }

    public Observable<String> getTokenObservable() {
        return appPreferences.getAuthenticationToken().asObservable();
    }

    @Override
    public void logout() {

        appPreferences.setCustomerId(null);
        appPreferences.setActiveHomeId(null);
        appPreferences.setAuthenticationToken(null);
        appPreferences.setLastConnectedIPAddressHue(null);
        appPreferences.setUserNameHue(null);
        appCache.clear();

    }

    public void clearCache(Context context) {
        AppCache.deleteCache(context);
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    @Override
    public void updateToken(String token) {
        appPreferences.setAuthenticationToken(token);
    }

    @Override
    public void updateCustomer(Home home) {
        appPreferences.setCustomerId(home.getId());
        if (!home.getId().isEmpty()) {
            appPreferences.setActiveHomeId(home.getId());
        }

    }


}
