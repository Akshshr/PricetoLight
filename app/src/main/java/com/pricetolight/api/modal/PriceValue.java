package com.pricetolight.api.modal;

import java.io.Serializable;

public class PriceValue implements Serializable {


    private double total;
    private double energy;
    private double tax;
    private String startsAt;


    public double getTotal() {
        return total;
    }

    public double getEnergy() {
        return energy;
    }

    public double getTax() {
        return tax;
    }

    public String getStartsAt() {
        return startsAt;
    }


//        "total": 0.3895,
//            "energy": 0.2796,
//            "tax": 0.1099,
//            "startsAt": "2019-06-24T00:00:00+02:00"

}
