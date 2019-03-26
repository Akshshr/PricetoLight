package com.pricetolight.api.modal;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.pricetolight.R;

public enum PriceLevel {

    @SerializedName("NORMAL")
    NORMAL,
    @SerializedName("EXPENSIVE")
    EXPENSIVE,
    @SerializedName("VERY_EXPENSIVE")
    VERY_EXPENSIVE,
    @SerializedName("CHEAP")
    CHEAP,
    @SerializedName("VERY_CHEAP")
    VERY_CHEAP;


    public int getLevelColor(Context context) {
        switch (this) {
            case NORMAL:
                return context.getResources().getColor(R.color.orange,null);
            case EXPENSIVE:
                return context.getResources().getColor(R.color.red300,null);
            case VERY_EXPENSIVE:
                return context.getResources().getColor(R.color.red500,null);
            case CHEAP:
                return context.getResources().getColor(R.color.green600,null);
            case VERY_CHEAP:
                return context.getResources().getColor(R.color.green400,null);
            default:
                return context.getResources().getColor(R.color.green600,null);
        }
    }

    public int getLevelProgress(Context context) {
        switch (this) {
            case VERY_EXPENSIVE:
                return 95;
            case EXPENSIVE:
                return 65;
            case NORMAL:
                return 40;
            case CHEAP:
                return 25;
            case VERY_CHEAP:
                return 10;
            default:
                return 10;
        }
    }

//                    "level": "NORMAL", "HIGH","LOW"
}
