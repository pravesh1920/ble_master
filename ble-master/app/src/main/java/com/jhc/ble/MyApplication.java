package com.jhc.ble;

import android.app.Application;

import com.jhc.ble.utils.CrashHandler;

/**
 * @author：Chao
 * @date：2023/9/23
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        System.exit(0);
    }
}
