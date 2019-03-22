package com.pricetolight.api.modal;

import java.io.Serializable;

public class HomeAddress implements Serializable {

    private String address1;
    private String address2;
    private String address3;
    private String postalCode;
    private String city;
    private String country;
    private String latitude;

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getAddress3() {
        return address3;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getLatitude() {
        return latitude;
    }
}

// "address": {
//         "address1": "Mellanbergsvägen 13",
//         "address2": null,
//         "address3": null,
//         "postalCode": "12642",
//         "city": "Hägersten",
//         "country": "SE",
//         "latitude": "59.2910128",
//         "longitude": "17.9863367"
//         }