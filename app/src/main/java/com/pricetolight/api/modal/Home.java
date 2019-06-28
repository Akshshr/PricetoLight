package com.pricetolight.api.modal;

import android.location.Address;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Home implements Serializable {

    private String appNickname;
    private HomeType type;
    private String id;
    private HomeAddress address;


    public HomeType getType() {
        return type;
    }

    private CurrentSubscription currentSubscription;

    public CurrentSubscription getCurrentSubscription() {
        return currentSubscription;
    }

    public String getId() {
        return id;
    }

    public String getAppNickname() {
        return appNickname;
    }


    public HomeAddress getAddress() {
        return address;
    }


//    { viewer { homes { appNickname type address { address1 address2 address3 postalCode city country latitude longitude } } } }


}
