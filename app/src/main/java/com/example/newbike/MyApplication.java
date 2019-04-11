package com.example.newbike;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import cn.bmob.v3.Bmob;

public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Bmob.initialize(this,"ab56e4b50754c2f87898ceaab28a385f");
    }

    public static MyApplication getInstance(){
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
