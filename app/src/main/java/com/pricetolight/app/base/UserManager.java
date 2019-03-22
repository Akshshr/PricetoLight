package com.pricetolight.app.base;

import android.support.annotation.Nullable;

import com.pricetolight.api.modal.Home;
import com.pricetolight.api.modal.HomeAddress;
import com.pricetolight.api.service.Authenticator;

import java.io.IOException;

import rx.Observable;

public class UserManager implements Authenticator {

    private final AppPreferences appPreferences;

    public UserManager(final AppPreferences appPreferences) {
        this.appPreferences = appPreferences;
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

    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    @Override
    public void updateToken(String token) {
        appPreferences.setAuthenticationToken("86067adbddf3c0653a20ca79b9cefbd72aba7e90d4feadb61e5131b7a0633ef1");
    }

    @Override
    public void updateCustomer(Home home) {
        appPreferences.setCustomerId(home.getId());
        if (!home.getId().isEmpty()) {
            appPreferences.setActiveHomeId(home.getId());
        }

    }


}
