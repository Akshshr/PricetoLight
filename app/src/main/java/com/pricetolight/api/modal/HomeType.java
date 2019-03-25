package com.pricetolight.api.modal;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

public enum HomeType {

    @SerializedName("APARTMENT")
    APARTMENT,
    @SerializedName("ROWHOUSE")
    ROWHOUSE,
    @SerializedName("FLOORHOUSE1")
    FLOORHOUSE1,
    @SerializedName("FLOORHOUSE2")
    FLOORHOUSE2,
    @SerializedName("FLOORHOUSE3")
    FLOORHOUSE3,
    @SerializedName("COTTAGE")
    COTTAGE,
    @SerializedName("CASTLE")
    CASTLE;


    public String description(Context context) {
        switch (this) {
            case APARTMENT:
                return "Apartment";
            case ROWHOUSE:
                return "Row house";
            case FLOORHOUSE1:
                return "FLOORHOUSE1";
            case FLOORHOUSE2:
                return "FLOORHOUSE2";
            case FLOORHOUSE3:
                return "FLOORHOUSE3 ";
            case CASTLE:
                return "Castle";
            case COTTAGE:
                return "Cottage";
            default:
                return "";
        }
    }
}
