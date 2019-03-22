package com.pricetolight.api.service;

import com.pricetolight.api.modal.Homes;
import com.pricetolight.api.rx.QueryRequest;

import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface UserApiEndpoint {

    @POST("gql")
    Observable<Homes> signinUserWithEmail(@Header("Authorization") String token, @Body QueryRequest query);

}
