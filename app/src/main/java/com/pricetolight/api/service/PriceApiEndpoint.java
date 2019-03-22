package com.pricetolight.api.service;

import com.pricetolight.api.modal.Homes;
import com.pricetolight.api.rx.QueryRequest;

import java.util.ArrayList;

import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface PriceApiEndpoint {

    @POST("gql")
    Observable<Homes> getPrice(@Header("Authorization") String token, @Body QueryRequest query);

    @POST("gql")
    Observable<Homes> getMe(@Header("Authorization") String token, @Body QueryRequest query);

}
