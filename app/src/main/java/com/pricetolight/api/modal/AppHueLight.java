package com.pricetolight.api.modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.philips.lighting.model.PHLight;

import java.io.Serializable;

public class AppHueLight extends PHLight implements Serializable {

    PHLight light;

    public AppHueLight(String name, String identifier, String versionNumber, String modelNumber) {
        super(name, identifier, versionNumber, modelNumber);
    }

    public AppHueLight(PHLight light) {
        super(light);
    }



}
