package com.example.newbike.util;

import android.content.Context;
import android.widget.Toast;

import com.example.newbike.MyApplication;

public class ToastUtil {

    private static Toast toast;
    private static Context context = MyApplication.getInstance();

    public static void show(String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

}
