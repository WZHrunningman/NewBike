package com.example.newbike.model;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

public class User extends BmobUser {

    private Boolean ispayment;
    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getIspayment() {
        return ispayment;
    }

    public void setIspayment(Boolean ispayment) {
        this.ispayment = ispayment;
    }
}
