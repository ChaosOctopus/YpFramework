package com.tuya.mylibrary.config;

import com.tuya.mylibrary.model.ApiModel;

import java.util.List;
import java.util.Map;

/**
 * @author yangping
 */
public class ModuleConfigModel extends ApiModel {
    public Map<String, List<String>> moduleMap;
    public Map<String,String> serviceMap;
    public Map<String,List<String>> pipeLine;
    public Map<String,List<EventModule>> eventMap;
}
