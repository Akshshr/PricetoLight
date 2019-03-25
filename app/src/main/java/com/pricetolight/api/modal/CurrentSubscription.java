package com.pricetolight.api.modal;

import java.io.Serializable;

public class CurrentSubscription implements Serializable {

    private PriceInfo priceInfo;


    public PriceInfo getPriceInfo() {
        return priceInfo;
    }


}



//          "home": {
//        "currentSubscription": {
//            "priceInfo": {
//                "current": {
//                    "total": 0.593,
//                            "energy": 0.4154,
//                            "tax": 0.1776,
//                            "startsAt": "2019-03-25T13:00:00+01:00"
//                }
//            }
//        }
//    }


