package com.pricetolight.api.modal;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.gson.annotations.SerializedName;
import com.pricetolight.R;

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
    @SerializedName("nohouse")
    NOHOUSE,
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
            case NOHOUSE:
                return "nohouse";
            default:
                return "";
        }
    }


    public Drawable getAvatarType(Context context) {
        switch (this) {
            case APARTMENT:
                return context.getResources().getDrawable(R.drawable.ic_apartment,null);
            case ROWHOUSE:
                return context.getResources().getDrawable(R.drawable.ic_rowhouse,null);
            case FLOORHOUSE1:
                return context.getResources().getDrawable(R.drawable.ic_floorhouse1,null);
            case FLOORHOUSE2:
                return context.getResources().getDrawable(R.drawable.ic_floorhouse2,null);
            case FLOORHOUSE3:
                return context.getResources().getDrawable(R.drawable.ic_floorhouse3,null);
            case CASTLE:
                return context.getResources().getDrawable(R.drawable.ic_castle,null);
            case COTTAGE:
                return context.getResources().getDrawable(R.drawable.ic_cottage,null);
            default:
                return context.getResources().getDrawable(R.drawable.ic_apartment,null);
        }
    }


}
