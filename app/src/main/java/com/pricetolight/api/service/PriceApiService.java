package com.pricetolight.api.service;

import com.pricetolight.api.modal.Home;
import com.pricetolight.api.modal.Homes;
import com.pricetolight.api.rx.QueryRequest;

import java.util.ArrayList;

import rx.Observable;

public class PriceApiService {


    private PriceApiEndpoint priceApiEndpoint;
    private Authenticator authenticator;
    private String token = "86067adbddf3c0653a20ca79b9cefbd72aba7e90d4feadb61e5131b7a0633ef1";

    public PriceApiService(ApiServiceFactory apiServiceFactory, Authenticator authenticator) {
        this.priceApiEndpoint = apiServiceFactory.createApiService(PriceApiEndpoint.class);
        this.authenticator = authenticator;
    }

    public Observable<Homes> getPrice(String homeId) {
        String query = "{ viewer { homes { currentSubscription{ priceInfo{ current{ total energy tax startsAt } } } } } }";
        return priceApiEndpoint.getPrice(authenticator.getToken(), new QueryRequest(String.format(query, homeId)));
    }

    public Observable<Object> getMe() {
        String query = "{ viewer { homes { id appNickname type address { address1 address2 address3 postalCode city country latitude longitude } } } }";
        return priceApiEndpoint.getMe(token, new QueryRequest(query));
    }


}
