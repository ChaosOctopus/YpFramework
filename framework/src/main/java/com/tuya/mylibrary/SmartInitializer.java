package com.tuya.mylibrary;

import android.app.Application;

import com.tuya.mylibrary.logger.ILogger;
import com.tuya.mylibrary.logger.LogUtil;
import com.tuya.mylibrary.pipeline.SmartExecutor;
import com.tuya.mylibrary.service.MicroServiceManager;
import com.tuya.mylibrary.start.LauncherApplicationAgent;

import java.util.concurrent.Executor;

/**
 * @author yangping
 */
public class SmartInitializer {

    public static boolean isDebug = false;

    public static void init(Application application){
        init(application,null);
    }

    public static void init(Application application, ILogger logger) {
        init(application,logger,null);
    }

    public static void init(Application application, ILogger logger, Executor executor) {
        LogUtil.setILogger(logger);
        SmartExecutor.getInstance().setExecutor(executor);
        LauncherApplicationAgent.getInstance().onCreate(application);
    }

    /**
     * 是否支持服务重载
     *
     * @param enableServiceRedirect
     */
    public static void setEnableServiceRedirect(boolean enableServiceRedirect) {
        MicroServiceManager.getInstance().setEnableRedirect(enableServiceRedirect);
    }

    public static void setDebug(boolean debug) {
        isDebug = debug;
    }
}
