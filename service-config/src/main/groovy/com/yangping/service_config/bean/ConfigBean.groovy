package com.yangping.service_config.bean

/**
 * 用于代表配置文件对象 实例：
 * {
 * "moduleMap": {
 * "com.tuya.demo.DemoModuleApp": [
 * "demo","xxx"
 * ]
 * },
 * "serviceMap": {
 * "com.tuya.smart.api.service.TestService": "com.tuya.demo.service.TestServiceImpl"
 * },
 * "PIPE_LINE_APPLICATION_ASYNC": [
 * "com.tuya.demo.AppStartPipeLine"
 * ],
 * "PIPE_LINE_APPLICATION_SYNC": [
 * {
 * "name": "com.tuya.demo.DemoAppSyncPipeLine",
 * "priority": 2
 * }
 * ],
 * "PIPE_LINE_TAB_LAUNCHER_STARTED": [
 * "com.tuya.demo.AppTabStartPipeLine"
 * ]
 * }
 */
class ConfigBean extends BaseConfigBean{
    private List<AppSyncBean> PIPE_LINE_APPLICATION_SYNC

   private List<String> PIPE_LINE_APPLICATION_ASYNC

   private List<String> PIPE_LINE_TAB_LAUNCHER_STARTED

    private HashMap<String, List<String>> PIPE_LINE_BUSINESS;

    // pipeline依赖关系表
    private HashMap<String, List<String>> PIPE_LINE_DEPS;

    public HashMap<String, List<String>> getPIPE_LINE_DEPS(){
        if(null == PIPE_LINE_DEPS){
            PIPE_LINE_DEPS = new HashMap<>();
        }
        return PIPE_LINE_DEPS;
    }

    HashMap<String, List<String>> getPIPE_LINE_BUSINESS() {
        if(null == PIPE_LINE_BUSINESS){
            PIPE_LINE_BUSINESS = new HashMap<>();
        }
        return PIPE_LINE_BUSINESS;
    }

    List<String> getPIPE_LINE_APPLICATION_ASYNC() {
       if (PIPE_LINE_APPLICATION_ASYNC == null) {
           PIPE_LINE_APPLICATION_ASYNC = new LinkedList<>()
       }
       return PIPE_LINE_APPLICATION_ASYNC
   }


    List<AppSyncBean> getPIPE_LINE_APPLICATION_SYNC() {
       if (PIPE_LINE_APPLICATION_SYNC == null) {
           PIPE_LINE_APPLICATION_SYNC = new LinkedList<>()
       }
       return PIPE_LINE_APPLICATION_SYNC
   }

    List<String> getPIPE_LINE_TAB_LAUNCHER_STARTED() {
       if (PIPE_LINE_TAB_LAUNCHER_STARTED == null) {
           PIPE_LINE_TAB_LAUNCHER_STARTED = new LinkedList<>()
       }
       return PIPE_LINE_TAB_LAUNCHER_STARTED
   }
    static class AppSyncBean {
        String name
        int priority = 100

        AppSyncBean(String name, int priority) {
           this.name = name
           this.priority = priority < 0 || priority > 100 ? 100 : priority
       }

        int getPriority() {
            priority = priority < 0 || priority > 100 ? 100 : priority
            return priority
        }

        @Override
        int hashCode() {
            return name.hashCode()
        }

        @Override
        boolean equals(Object obj) {
            if (obj instanceof AppSyncBean) {
                return name == obj.name
            }
            return false
        }
    }
}
