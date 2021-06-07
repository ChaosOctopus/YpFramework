package com.tuya.mylibrary.logger;

import android.util.Log;

import com.tuya.mylibrary.SmartInitializer;

/**
 * @author yangping
 */
public class DefaultLogger implements ILogger {
    @Override
    public void v(String tag, String message) {
        if (SmartInitializer.isDebug) {
            Log.v(tag, message);
        }
    }

    @Override
    public void i(String tag, String message) {
        if (SmartInitializer.isDebug) {
            Log.i(tag, message);
        }
    }

    @Override
    public void d(String tag, String message) {
        if (SmartInitializer.isDebug) {
            Log.d(tag, message);
        }
    }

    @Override
    public void w(String tag, String message) {
        if (SmartInitializer.isDebug) {
            Log.w(tag, message);
        }
    }

    @Override
    public void e(String tag, String message) {
        if (SmartInitializer.isDebug) {
            Log.e(tag, message);
        }
    }

    @Override
    public void w(String tag, String message, Throwable t) {
        if (SmartInitializer.isDebug) {
            Log.w(tag, message,t);
        }
    }

    @Override
    public void e(String tag, String message, Throwable t) {
        if (SmartInitializer.isDebug) {
            Log.e(tag, message,t);
        }
    }
}
