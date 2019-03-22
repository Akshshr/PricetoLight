package com.pricetolight.api;

import com.pricetolight.api.service.ApiServiceFactory;
import com.pricetolight.api.service.Authenticator;
import com.pricetolight.api.service.PriceApiService;
import com.pricetolight.api.service.UserApiService;

public class Api {

    private PriceApiService priceApiService;
    private UserApiService userApiService;

    public Api(String apiHost, Authenticator authenticator) {
        priceApiService = new PriceApiService(new ApiServiceFactory(String.format(apiHost, "v1-beta"), authenticator), authenticator);
        userApiService = new UserApiService(new ApiServiceFactory(String.format(apiHost, "v1-beta"), authenticator), authenticator);
    }

    public PriceApiService getPriceApiService() {
        return priceApiService;
    }

    public UserApiService getUserApiService() {
        return userApiService;
    }



}
