package com.pricetolight.api.service;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pricetolight.BuildConfig;
import com.pricetolight.api.gson.GsonFactory;
import com.pricetolight.api.rx.RxAndroidCallAdapterFactory;
import com.pricetolight.api.util.UserAgentInterceptor;

import java.net.HttpURLConnection;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServiceFactory {

    private static final String TAG = ApiServiceFactory.class.getSimpleName();

    private final Retrofit retrofit;
    private final Authenticator authenticator;

    public ApiServiceFactory(
            final String apiHost,
            final Authenticator authenticator
            ) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        this.authenticator = authenticator;
        this.retrofit = new Retrofit.Builder()
                .baseUrl(apiHost)
                .client(createClient(gson))
                .addConverterFactory(GsonConverterFactory.create(GsonFactory.createWithPreprocessors()))
                .addCallAdapterFactory(RxAndroidCallAdapterFactory.create())
                .build();
    }


    private OkHttpClient createClient(Gson gson) {
        final OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addNetworkInterceptor(chain -> {
            final Response response = chain.proceed(chain.request());
            if (!response.isSuccessful() && response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                authenticator.logout();
                Log.d(TAG, "Clearing authentication token");
            }
            return response;
        });
        client.addNetworkInterceptor(chain -> {
            Request.Builder builder = chain.request().newBuilder();
            Request request = builder.build();
            return chain.proceed(request);
        });

        String UserAgent = System.getProperty("http.agent");
        String transformedUserAgent = String.format(
                Locale.US, "PriceToLights/%s (versionCode: %d%s)",
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE,
                UserAgent);

        client.interceptors().add(new UserAgentInterceptor(transformedUserAgent));
        client.connectTimeout(60, TimeUnit.SECONDS);
        client.readTimeout(60, TimeUnit.SECONDS);

        return client.build();
    }


    <S> S createApiService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
