package com.tuya.mylibrary.service;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

/**
 * @author yangping
 */
public abstract class MicroService implements ComponentCallbacks2 {

    protected Context mBase;

    public void attachBaseContext(Context base){
        if (mBase != null){
            throw new IllegalStateException("mBase context already set");
        }
        mBase = base;
    }

    /**
     * Service onCreate
     */
    public void onCreate(){

    }

    @Override
    public void onTrimMemory(int level) {

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {

    }

    @Override
    public void onLowMemory() {

    }
}
