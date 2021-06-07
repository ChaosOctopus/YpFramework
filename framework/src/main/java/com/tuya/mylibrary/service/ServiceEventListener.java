package com.tuya.mylibrary.service;

/**
 * @author yangping
 */
public interface ServiceEventListener {
    /**
     * failed get the service by name
     * @param serviceName
     */
    void onFailed(String serviceName);
}
