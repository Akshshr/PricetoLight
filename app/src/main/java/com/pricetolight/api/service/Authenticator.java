package com.pricetolight.api.service;

import androidx.annotation.Nullable;

import com.pricetolight.api.modal.Home;

public interface Authenticator {

    @Nullable
    String getToken();

    void logout();

    void updateToken(String token);

    void updateCustomer(Home home);


}
