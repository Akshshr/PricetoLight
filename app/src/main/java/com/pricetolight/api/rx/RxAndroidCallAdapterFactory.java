package com.pricetolight.api.rx;

import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import com.pricetolight.BuildConfig;
import com.pricetolight.api.retrofit.RetrofitException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RxAndroidCallAdapterFactory extends CallAdapter.Factory {

    private static final String TAG = RxAndroidCallAdapterFactory.class.getSimpleName();
    private final RxJavaCallAdapterFactory original;

    private RxAndroidCallAdapterFactory() {
        // Always call on background thread
        this.original = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
    }

    public static CallAdapter.Factory create() {
        return new RxAndroidCallAdapterFactory();
    }


    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        return new RxCallAdapterWrapper(retrofit, original.get(returnType, annotations, retrofit));
    }

    private static class RxCallAdapterWrapper implements CallAdapter<Observable<?>> {
        private final CallAdapter<?> wrapped;
        private final Retrofit retrofit;

        private RxCallAdapterWrapper(Retrofit retrofit, CallAdapter<?> wrapped) {
            this.retrofit = retrofit;
            this.wrapped = wrapped;
        }

        @Override
        public Type responseType() {
            return wrapped.responseType();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R> Observable<?> adapt(Call<R> call) {
            return ((Observable) wrapped.adapt(call))
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(new Func1<Throwable, Observable>() {
                        @Override
                        public Observable call(Throwable throwable) {
                            return Observable.error(asRetrofitException(throwable));
                        }
                    });
        }

        private RetrofitException asRetrofitException(Throwable throwable) {

            try {
                if (throwable instanceof HttpException) {
                    HttpException httpException = (HttpException) throwable;
                    Response response = httpException.response();
                    return RetrofitException.httpError(response.raw().request().url().toString(), response, retrofit);
                }
                // A network error happened
                if (throwable instanceof IOException) {
                    return RetrofitException.networkError((IOException) throwable);
                }
            } catch (Exception e) {
                Log.e(TAG, "An error occured while throwing error", e);
            }
            // We don't know what happened. We need to simply convert to an unknown error
            return RetrofitException.unexpectedError(throwable);
        }
    }
}
