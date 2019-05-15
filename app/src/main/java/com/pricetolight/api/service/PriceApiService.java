package com.pricetolight.api.service;

import com.pricetolight.api.modal.CurrentSubscription;
import com.pricetolight.api.modal.Home;
import com.pricetolight.api.modal.Homes;
import com.pricetolight.api.rx.QueryRequest;

import java.util.ArrayList;

import rx.Observable;

public class PriceApiService {
    
    private PriceApiEndpoint priceApiEndpoint;
    private Authenticator authenticator;
    private String token;

    public PriceApiService(ApiServiceFactory apiServiceFactory, Authenticator authenticator) {
        this.priceApiEndpoint = apiServiceFactory.createApiService(PriceApiEndpoint.class);
        this.authenticator = authenticator;
        token = authenticator.getToken();
    }

    public Observable<Home> getPrice(String homeId) {
        String query = "{ viewer { home (id:\"%1$s\") { currentSubscription{ priceInfo{ current{ level total energy tax startsAt } } } } } }";
        return priceApiEndpoint.getPrice(token, new QueryRequest(String.format(query, homeId)));
    }

    public Observable<Homes> getMe() {
        String query = "{ viewer { homes { id appNickname type address { address1 address2 address3 postalCode city country latitude longitude } } } }";
        return priceApiEndpoint.getMe(token, new QueryRequest(query));
    }


}
