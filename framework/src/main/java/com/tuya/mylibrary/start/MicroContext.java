package com.tuya.mylibrary.start;

import android.app.Application;

import com.tuya.mylibrary.pipeline.SmartExecutor;
import com.tuya.mylibrary.service.MicroService;
import com.tuya.mylibrary.service.MicroServiceManager;

import java.util.concurrent.Executor;

/**
 * @author yangping
 */
public class MicroContext {

    public static MicroServiceManager getServiceManager() {
        return MicroServiceManager.getInstance();
    }

    public static <T extends MicroService> T findServiceByInterface(String serviceName) {
        return MicroServiceManager.getInstance().findServiceByInterface(serviceName);
    }

    public static Application getApplication() {
        return LauncherApplicationAgent.getInstance().getApplication();
    }

    public static LauncherApplicationAgent getLauncherApplicationAgent() {
        return LauncherApplicationAgent.getInstance();
    }

    public static Executor getExecutor() {
        return SmartExecutor.getInstance().getExecutor();
    }
}
