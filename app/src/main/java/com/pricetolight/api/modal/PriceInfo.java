package com.pricetolight.api.modal;

import java.io.Serializable;
import java.util.ArrayList;

public class PriceInfo implements Serializable {

    private CurrentPrice current;
    private ArrayList<PriceValue> today;

    public ArrayList<PriceValue> getToday() {
        return today;
    }

    public CurrentPrice getCurrent() {
        return current;
    }

}
