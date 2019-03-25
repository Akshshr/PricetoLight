package com.pricetolight.api.modal;

import java.io.Serializable;

public class PriceInfo implements Serializable {

    private CurrentPrice current;

    public CurrentPrice getCurrent() {
        return current;
    }

}

//    priceInfo {
//        current {
//            total
//                    energy
//            tax
//                    startsAt
//            level
//        }
//    }
