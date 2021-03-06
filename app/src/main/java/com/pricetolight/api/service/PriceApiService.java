package com.pricetolight.api.service;

import com.pricetolight.api.modal.CurrentSubscription;
import com.pricetolight.api.modal.Home;
import com.pricetolight.api.modal.Homes;
import com.pricetolight.api.rx.QueryRequest;
import com.pricetolight.app.base.AppPreferences;

import java.util.ArrayList;

import rx.Observable;

public class PriceApiService {
    
    private PriceApiEndpoint priceApiEndpoint;
    private Authenticator authenticator;

    public PriceApiService(ApiServiceFactory apiServiceFactory, Authenticator authenticator) {
        this.priceApiEndpoint = apiServiceFactory.createApiService(PriceApiEndpoint.class);
        this.authenticator = authenticator;
    }

    public Observable<Home> getPrice(String homeId) {
        String query = "{ viewer { home (id:\"%1$s\") { currentSubscription{ priceInfo{ current{ level total energy tax startsAt } } } } } }";
        return priceApiEndpoint.getPrice(authenticator.getToken(), new QueryRequest(String.format(query, homeId)));
    }

    public Observable<Homes> getMe() {
        String query = "{ viewer { homes { id appNickname type address { address1 address2 address3 postalCode city country latitude longitude } } } }";
        return priceApiEndpoint.getMe(authenticator.getToken(), new QueryRequest(query));
    }

    public Observable<Home> getPriceHistory(String homeId) {
        String query = "{ viewer { home (id:\"%1$s\") { currentSubscription{ priceInfo{ today{ total energy tax startsAt } } } } } }";
        query = String.format(query, homeId);
        return priceApiEndpoint.getPriceHistory(authenticator.getToken(), new QueryRequest(query));
    }


}
