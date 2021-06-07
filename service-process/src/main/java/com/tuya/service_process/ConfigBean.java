package com.tuya.service_process;

import java.util.HashMap;

/**
 * @author yangping
 */
public class ConfigBean {
    private HashMap<String,String> serviceMap;

    public HashMap<String, String> getServiceMap() {
        if (serviceMap == null) {
            serviceMap = new HashMap<>();
        }
        return serviceMap;
    }
}
