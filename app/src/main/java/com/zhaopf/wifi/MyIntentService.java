package com.zhaopf.wifi;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

/**
 * @Author: 赵鹏飞
 * @Github: https://github.com/zhao-pf
 * @Date: 2021/2/19 10:01
 * @Description: TODO
 */
public class MyIntentService extends IntentService {
    public static String TYPE_TOAST = "Toast";

    public MyIntentService() {
        super("MyIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("server", "开启服务");
        while (true) {
            if (MainActivity.IS_FIRST) {
                firstStart();
            } else {
                secendStart();
            }
        }


    }

    private void secendStart() {
        while (MainActivity.TIME != 0) {
            EventBus.getDefault().post((MainActivity.TIME--) + "");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        EventBus.getDefault().post(TYPE_TOAST);
        MainActivity.TIME = MainActivity.FOR_TIME;
    }

    private void firstStart() {
        MainActivity.TIME = MainActivity.FIRST_TIME;
        while (MainActivity.TIME != 0) {
            EventBus.getDefault().post((MainActivity.TIME--) + "");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        EventBus.getDefault().post(TYPE_TOAST);
        MainActivity.TIME = MainActivity.FOR_TIME;
        MainActivity.IS_FIRST = false;

    }
}