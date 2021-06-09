package com.yangping.service_config.bean


import org.gradle.api.logging.Logger

/**
 * 合并配置处理工具
 */
class ConfigBeanMergeUtil {
    /**
     * 1. 处理 key 值冲突问题，若有冲突则抛出异常
     * 2. 合并到主配置当中
     * @param from
     * @param into
     */
    static void merge(ConfigBean from, ConfigBean into, Logger logger) {
        // module map
        Set<String> rootKeys = new HashSet<>(into.getModuleMap().keySet())
        Set<String> childKeys = new HashSet<>(from.getModuleMap().keySet())
        rootKeys.retainAll(childKeys)
        if (rootKeys.size() > 0) {
            Iterator<String> iterator = rootKeys.iterator()
            while (iterator.hasNext()) {
//                logger.error()
                throw new Exception("the module app name : " + iterator.next() + " has been registered !!")
            }
        } else {
            into.getModuleMap().putAll(from.getModuleMap())
            //校验 value,也不能重复
            HashMap<String, String> values2Key = new HashMap<>()
            into.getModuleMap().each { vk ->
                List<String> innerValues = vk.value
                if (innerValues.size() > 0) {
                    innerValues.each { v ->
                        if (values2Key.containsKey(v)) {
                            throw new Exception("the module app value name : " + v + " in ${vk.key}, has been registered by " + values2Key.get(v))
                        } else {
                            values2Key.put(v, vk.key)
                        }
                    }
                }
            }
        }

        // serviceName
        rootKeys = new HashSet<>(into.getServiceMap().keySet())
        childKeys = new HashSet<>(from.getServiceMap().keySet())
        rootKeys.retainAll(childKeys)
        if (rootKeys.size() > 0) {
            Iterator<String> iterator = rootKeys.iterator()
            while (iterator.hasNext()) {
                throw new Exception("the  Service name : " + iterator.next() + " has been registered !!")
            }
        } else {
            into.getServiceMap().putAll(from.getServiceMap())
        }

        //PIPE_LINE_APPLICATION_ASYNC
        List<String> rootPipelineKeys = new LinkedList<>(into.getPIPE_LINE_APPLICATION_ASYNC())
        List<String> childPipelineKeys = new LinkedList<>(from.getPIPE_LINE_APPLICATION_ASYNC())
        rootPipelineKeys.retainAll(childPipelineKeys)
        if (rootPipelineKeys.size() > 0) {
            Iterator<String> iterator = rootPipelineKeys.iterator()
            while (iterator.hasNext()) {
                throw new Exception("the PIPE_LINE_APPLICATION_ASYNC class " + iterator.next() + " has existed !!")
            }
        } else {
            into.getPIPE_LINE_APPLICATION_ASYNC().addAll(from.getPIPE_LINE_APPLICATION_ASYNC())
        }

        //PIPE_LINE_TAB_LAUNCHER_STARTED
        rootPipelineKeys = new LinkedList<>(into.getPIPE_LINE_TAB_LAUNCHER_STARTED())
        childPipelineKeys = new LinkedList<>(from.getPIPE_LINE_TAB_LAUNCHER_STARTED())
        rootPipelineKeys.retainAll(childPipelineKeys)
        if (rootPipelineKeys.size() > 0) {
            Iterator<String> iterator = rootPipelineKeys.iterator()
            while (iterator.hasNext()) {
                throw new Exception("the PIPE_LINE_TAB_LAUNCHER_STARTED class " + iterator.next() + " has existed !!")
            }
        } else {
            into.getPIPE_LINE_TAB_LAUNCHER_STARTED().addAll(from.getPIPE_LINE_TAB_LAUNCHER_STARTED())
        }
        //PIPE_LINE_APPLICATION_SYNC
        List<ConfigBean.AppSyncBean> rootSyncKeys = new LinkedList<>(into.getPIPE_LINE_APPLICATION_SYNC())
        List<ConfigBean.AppSyncBean> childSyncKeys = new LinkedList<>(from.getPIPE_LINE_APPLICATION_SYNC())
        rootSyncKeys.retainAll(childSyncKeys)
        if (rootSyncKeys.size() > 0) {
            Iterator<ConfigBean.AppSyncBean> iterator = rootSyncKeys.iterator()
            while (iterator.hasNext()) {
                throw new Exception("the PIPE_LINE_APPLICATION_SYNC class " + iterator.next().name + " has existed !!")
            }
        } else {
            into.getPIPE_LINE_APPLICATION_SYNC().addAll(from.getPIPE_LINE_APPLICATION_SYNC())
        }

        // PIPE_LINE_BUSINESS
        Map<String, List<String>> mergePipes = new HashMap<>(into.getPIPE_LINE_BUSINESS())

        from.getPIPE_LINE_BUSINESS().each { scence, value ->
            List<String> pipes = mergePipes.get(scence)
            if(null == pipes){
                pipes = new ArrayList<>()
                mergePipes.put(scence, pipes)
            }
            pipes.addAll(value)
        }

        // pipeline 依赖树的合并
        Map<String, List<String>> mergePipeDeps = new HashMap<>(into.getPIPE_LINE_DEPS());
        from.getPIPE_LINE_DEPS().each { pipe, deps ->
            if(null != deps && !deps.isEmpty()){
                List<String> depList = mergePipeDeps.get(pipe);
                if(null == depList){
                    depList = new ArrayList<>();
                    mergePipeDeps.put(pipe, depList);
                }
                depList.addAll(deps);
            }
        }

        into.getPIPE_LINE_BUSINESS().putAll(mergePipes)
        into.getPIPE_LINE_DEPS().putAll(mergePipeDeps)

        //TYEvent
        if (from.getEventMap().size() > 0) {
            from.getEventMap().each { k, v ->
                List<BaseConfigBean.TYEventEntryBean> toList = into.getEventMap().get(k)
                if (toList == null) {
                    into.getEventMap().put(k, new LinkedList<BaseConfigBean.TYEventEntryBean>(v))
                } else {
                    toList.addAll(v)
                }
            }
        }
    }

    static def sort(List<String> dst, List<ConfigBean.AppSyncBean> pipeLineItemList) {
        Collections.sort(pipeLineItemList, new Comparator<ConfigBean.AppSyncBean>() {
            @Override
            int compare(ConfigBean.AppSyncBean o1, ConfigBean.AppSyncBean o2) {
                println "compare: " + o1.getPriority() + o1.name
                return Integer.compare(o1.getPriority(), o2.getPriority())
            }
        })

        for (ConfigBean.AppSyncBean item : pipeLineItemList) {
            dst.add(item.name)
            println "add " + item.name
        }
    }




}
