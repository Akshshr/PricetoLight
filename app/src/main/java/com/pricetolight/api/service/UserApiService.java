package com.pricetolight.api.service;

import com.pricetolight.api.modal.Homes;
import com.pricetolight.api.rx.QueryRequest;

import rx.Observable;

public class UserApiService {


    private UserApiEndpoint userApiEndpoint;
    private Authenticator authenticator;

    public UserApiService(ApiServiceFactory apiServiceFactory, Authenticator authenticator) {
        this.userApiEndpoint = apiServiceFactory.createApiService(UserApiEndpoint.class);
        this.authenticator = authenticator;
    }

    public Observable<Homes> signinUserWithEmail(String homeId, String password) {
        String query = "{ viewer { homes { currentSubscription{ priceInfo{ current{ total, energy, tax, startsAt } } } } } }";
        return userApiEndpoint.signinUserWithEmail(authenticator.getToken(), new QueryRequest(String.format(query, homeId)));
    }


}
