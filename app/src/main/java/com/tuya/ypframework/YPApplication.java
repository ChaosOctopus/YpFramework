package com.tuya.ypframework;

import android.app.Application;

import com.tuya.mylibrary.SmartInitializer;

/**
 * @author yangping
 */
public class YPApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // maybe need a plugin to support it
        SmartInitializer.init(this);
    }
}
