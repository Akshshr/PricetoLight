package com.pricetolight.api.modal;

import java.util.Arrays;
import java.util.List;

public class Licence {

    private String dependency;
    private String type;

    public String getDependency() {
        return dependency;
    }

    public String getType() {
        return type;
    }

    public Licence(String dependency, String type) {
        this.dependency = dependency;
        this.type = type;
    }

    public static List<Licence> createLicences() {
        return Arrays.asList(
        new Licence("https://developers.meethue.com/develop/tools-and-sdks/hue-sdk/", "Philips Hue SDK"),
        new Licence("io.reactivex:rxjava:$rootProject.ext.rxJavaVersion", "Reactive extentions android"),
        new Licence("com.squareup.retrofit2:retrofit","Retrofit"),
        new Licence("com.squareup.retrofit2:converter-gson","Retrofit"),
        new Licence("com.squareup.retrofit2:adapter-rxjava","Retrofit"),
        new Licence("com.android.support:support-v4:28.0.0","Android support"),
        new Licence("com.f2prateek.rx.preferences:rx-preferences:1.0.2","Rx Preferences"),
        new Licence("net.danlew:android.joda:2.10.1", "Joda time android"),
        new Licence("com.google.code.gson:gson:2.8.5", "Gson"),
        new Licence("io.gsonfire:gson-fire:1.8.0","Gson"),
        new Licence("io.reactivex:rxandroid:1.2.1", "Reactive extentions android"),
        new Licence("com.airbnb.android:lottie:2.5.4", "Lottie by AirBnB"),
        new Licence("com.android.support.test.espresso:espresso-core:3.0.2", "Android test"),
        new Licence("com.android.support:support-core-utils", "Android support"),
        new Licence("com.android.support:support-v13","Android support"),
        new Licence("com.android.support:cardview-v7","Android support"),
        new Licence("com.android.support:design","Android support"),
        new Licence("com.android.support:percent","Android support"),
        new Licence("com.android.support:appcompat-v7","Android support"),
        new Licence("com.android.support:percent","Android support"),
        new Licence("com.android.support:support-v4","Android support"),
        new Licence("com.android.support:support-annotations","Android support"),
        new Licence("com.android.support:customtabs","Android support"),
        new Licence("com.android.support:support-v4","Android support"),
        new Licence("https://www.flaticon.com/ , Creative Commons 3.0","Icons")

        );
    }
}
