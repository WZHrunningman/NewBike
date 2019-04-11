package com.example.newbike.model;

import cn.bmob.v3.BmobObject;

public class Money extends BmobObject {

    private int money;
    private String username;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
