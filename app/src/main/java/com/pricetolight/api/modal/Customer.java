package com.pricetolight.api.modal;

import java.util.List;

public class Customer {

    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String ssn;
    private String email;
    private String language;
    private String tone;
    private String mobile;
    private List<Home> homes;

    public List<Home> getHomes() {
        return homes;
    }



    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSsn() {
        return ssn;
    }

    public String getEmail() {
        return email;
    }

    public String getLanguage() {
        return language;
    }

    public String getTone() {
        return tone;
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
