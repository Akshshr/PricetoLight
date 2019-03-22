package com.pricetolight.api.modal;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

public enum HomeType {
    @SerializedName("apartment")
    APARTMENT,
    @SerializedName("rowhouse")
    ROW_HOUSE,
    @SerializedName("cottage")
    COTTAGE,
    @SerializedName("something")
    SOMETHING,
    @SerializedName("something1")
    SOMETHING1,
    @SerializedName("castle")
    CASTLE;



    public String description(Context context) {
        switch (this) {
            case APARTMENT:
                return "Apartment";
            case ROW_HOUSE:
                return "Row house";
            case COTTAGE:
                return "Cottage";
            case SOMETHING:
                return "Something";
            case SOMETHING1:
                return "Something 1";
            case CASTLE:
                return "Castle";
            default:
                return "";
        }
    }
}
