package com.example.newbike.model;

import cn.bmob.v3.BmobObject;

public class Bike extends BmobObject {

    private String number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
