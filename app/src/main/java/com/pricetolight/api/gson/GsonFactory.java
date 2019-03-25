package com.pricetolight.api.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pricetolight.api.gson.processor.DataViewerPreObjectProcessor;
import com.pricetolight.api.modal.CurrentSubscription;
import com.pricetolight.api.modal.Home;
import com.pricetolight.api.modal.Homes;

import io.gsonfire.GsonFireBuilder;

public class GsonFactory {

    private GsonFactory() {
    }

    public static Gson createWithPreprocessors() {
        final GsonFireBuilder fireBuilder = new GsonFireBuilder();
        fireBuilder.registerPreProcessor(Homes.class, new DataViewerPreObjectProcessor<>());
        fireBuilder.registerPreProcessor(Home.class, new DataViewerPreObjectProcessor<>("home"));

        return create(fireBuilder);
    }

    public static Gson create() {
        return create(new GsonFireBuilder());
    }

    private static Gson create(GsonFireBuilder fireBuilder) {
        final GsonBuilder builder = fireBuilder.createGsonBuilder();
//        builder.registerTypeAdapter(DateTimeZone.class, new DateTimeZoneConverter());
        return builder.create();
    }
}
