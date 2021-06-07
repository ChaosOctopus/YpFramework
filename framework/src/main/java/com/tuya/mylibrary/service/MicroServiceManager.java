package com.tuya.mylibrary.service;

/**
 * @author yangping
 * 微服务管理类
 */
public abstract class MicroServiceManager {

    public static MicroServiceManager getInstance() {
        return MicroServiceManagerImpl.getInstance();
    }

    public abstract void setEnableRedirect(boolean enableRedirect);

    public abstract void registerServiceEventListener(ServiceEventListener listener);

    public abstract <T extends MicroService> T findServiceByInterface(String serviceName);

    public abstract <T extends MicroService> T findServiceByInterface(String serviceName,boolean allowRedirect);
}
