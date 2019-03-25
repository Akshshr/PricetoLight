package com.pricetolight.api.modal;

import org.joda.time.DateTime;

import java.io.Serializable;

public class CurrentPrice implements Serializable {

    private double total;
    private double energy;
    private double tax;
    private String startsAt;

    public PriceLevel getLevel() {
        return level;
    }

    private PriceLevel level;

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



//       "total": 0.593,
//               "energy": 0.4154,
//               "tax": 0.1776,
//               "startsAt": "2019-03-25T13:00:00+01:00",
//               "level": "NORMAL"


}
