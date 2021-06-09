package com.yangping.service_config.bean

class BaseConfigBean {
    protected HashMap<String, List<String>> moduleMap
    protected HashMap<String, String> serviceMap
    protected HashMap<String,List<TYEventEntryBean>> eventMap

    HashMap<String, List<TYEventEntryBean>> getEventMap() {
        if (eventMap == null){
            eventMap = new LinkedList<>()
        }
        return eventMap
    }

    HashMap<String, List<String>> getModuleMap() {
        if (moduleMap == null) {
            moduleMap = new HashMap<>()
        }
        return moduleMap
    }

    HashMap<String, String> getServiceMap() {
        if (serviceMap == null) {
            serviceMap = new HashMap<>()
        }
        return serviceMap
    }
     static class TYEventEntryBean {
        public String name
        boolean thread = false
    }
}
