package com.tuya.mylibrary.service;

import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.tuya.mylibrary.config.ModuleConfigLoader;
import com.tuya.mylibrary.logger.LogUtil;
import com.tuya.mylibrary.start.MicroContext;
import com.tuya.mylibrary.util.AppUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yangping
 */
public class MicroServiceManagerImpl extends MicroServiceManager{

    public static final String TAG = MicroServiceManagerImpl.class.getSimpleName();
    private volatile static MicroServiceManager sMicroServiceManager;
    private Map<String,String> serviceClassMap = new ConcurrentHashMap<>();
    private Map<String,MicroService> serviceInstanceMap = new ConcurrentHashMap<>();
    private Object serviceInstanceLock = new Object();
    private boolean enableRedirect = false;
    private ServiceEventListener serviceEventListener;

    public static MicroServiceManager getInstance() {
        if (sMicroServiceManager == null) {
            synchronized (MicroServiceManager.class) {
                if (sMicroServiceManager == null) {
                    sMicroServiceManager = new MicroServiceManagerImpl();
                }
            }
        }
        return sMicroServiceManager;
    }

    private MicroServiceManagerImpl(){
        Map<String,String> temp = ModuleConfigLoader.getInstance().getServiceMap();
        if (temp != null && !temp.isEmpty()) {
            serviceClassMap.putAll(temp);
        }
        MicroContext.getApplication().registerComponentCallbacks(mComponentCallbacks2);

    }

    @Override
    public void setEnableRedirect(boolean enableRedirect) {
        this.enableRedirect = enableRedirect;
    }

    @Override
    public void registerServiceEventListener(ServiceEventListener listener) {
        serviceEventListener = listener;
    }

    @Override
    public <T extends MicroService> T findServiceByInterface(String serviceName) {
        return  findServiceByInterface(serviceName, enableRedirect);
    }

    @Override
    public <T extends MicroService> T findServiceByInterface(String serviceName, boolean allowRedirect) {
        return findServiceByInterface(serviceName, allowRedirect, true);
    }

    private <T extends MicroService> T findServiceByInterface(String serviceName, boolean allowRedirect, boolean notifyEvent) {
        T microService = (T) serviceInstanceMap.get(serviceName);
        if (microService != null) {
            return microService;
        }

        String service = serviceClassMap.get(serviceName);
        if (TextUtils.isEmpty(service)) {
            LogUtil.e(TAG, "no service found:" + serviceName);
            if (notifyEvent && serviceEventListener != null) {
                serviceEventListener.onFailed(serviceName);
            }
            return null;
        }

        try {
            synchronized (serviceInstanceLock){
                microService = (T) serviceInstanceMap.get(serviceName);
                if (microService != null) return microService;
                microService = (T) AppUtils.getClassLoader().loadClass(service).newInstance();
                microService.attachBaseContext(MicroContext.getApplication());
                microService.onCreate();
                serviceInstanceMap.put(serviceName,microService);
            }
        }catch (Exception e){
            if (notifyEvent && serviceEventListener != null) {
                serviceEventListener.onFailed(serviceName);
            }
            LogUtil.e(TAG, "new service failed: " + serviceName, e);
        }
        return microService;
    }

    private ComponentCallbacks2 mComponentCallbacks2 = new ComponentCallbacks2() {
        @Override
        public void onTrimMemory(int level) {
            Collection<MicroService> values = serviceInstanceMap.values();
            for (MicroService microService : values) {
                microService.onTrimMemory(level);
            }
        }

        @Override
        public void onConfigurationChanged(@NonNull Configuration newConfig) {
            Collection<MicroService> serviceCollections = serviceInstanceMap.values();
            for (MicroService microService : serviceCollections) {
                microService.onConfigurationChanged(newConfig);
            }
        }

        @Override
        public void onLowMemory() {
            Collection<MicroService> serviceCollections = serviceInstanceMap.values();
            for (MicroService microService : serviceCollections) {
                microService.onLowMemory();
            }
        }
    };
}
