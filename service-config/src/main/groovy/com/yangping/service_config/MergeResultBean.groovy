package com.yangping.service_config

import com.yangping.service_config.bean.BaseConfigBean
import com.yangping.service_config.bean.ConfigBean


/**
 * 最终输出结果
 */
//{
//    "serviceMap": {
//    "serviceName2": "com.smart.tuya.tymodule_compiler.ServiceTest2",
//    "serviceName": "com.smart.tuya.tymodule_compiler.ServiceTest",
//    "FooServiceTestbibi": "com.smart.tuya.testarr.MyService",
//    "serviceName3": "com.smart.tuya.tymodule_compiler.ServiceTest3"
//},
//    "pipeLine": {
//    "PIPE_LINE_TAB_LAUNCHER_STARTED": [
//            "com.smart.tuya.tymodule_compiler.TestB"
//    ],
//    "PIPE_LINE_APPLICATION_SYNC": [
//            "com.smart.tuya.tymodule_compiler.TestC",
//            "com.smart.tuya.tymodule_compiler.TestD"
//    ],
//    "PIPE_LINE_APPLICATION_ASYNC": [
//            "com.smart.tuya.tymodule_compiler.TestA"
//    ],
//    "PIPE_LINE_BUSINESS_com.smart.tuya.testarr.LoginScenarioType": [
//      "com.smart.tuya.testarr.TestC",
//      "com.smart.tuya.testarr.TestCustomPipeLine",
//      "com.smart.tuya.testarr.TestCustomPipeline2"
//    ],
//    "PIPE_LINE_BUSINESS_com.smart.tuya.pipeline.ScenarioType1": [
//      "com.smart.tuya.testarr.pipeline.PipelineTask7",
//      "com.smart.tuya.testarr.pipeline.PipelineTask1",
//      "com.smart.tuya.testarr.pipeline.PipelineTask9",
//      "com.smart.tuya.testarr.pipeline.PipelineTask8",
//      "com.smart.tuya.testarr.pipeline.PipelineTask4",
//      "com.smart.tuya.testarr.pipeline.PipelineTask2",
//      "com.smart.tuya.testarr.pipeline.PipelineTask3",
//      "com.smart.tuya.testarr.pipeline.PipelineTask5",
//      "com.smart.tuya.testarr.pipeline.PipelineTask6"
//    ],
//},
// "pipeLineDeps": {
//    "com.smart.tuya.testarr.TestCustomPipeline2": [
//      "com.smart.tuya.testarr.TestCustomPipeLine"
//    ],
//    "com.smart.tuya.testarr.pipeline.PipelineTask6": [
//      "com.smart.tuya.testarr.pipeline.PipelineTask5"
//    ],
//    "com.smart.tuya.testarr.pipeline.PipelineTask5": [
//      "com.smart.tuya.testarr.pipeline.PipelineTask2"
//    ],
//    "com.smart.tuya.testarr.pipeline.PipelineTask3": [
//      "com.smart.tuya.testarr.pipeline.PipelineTask2"
//    ],
//    "com.smart.tuya.testarr.pipeline.PipelineTask2": [
//      "com.smart.tuya.testarr.pipeline.PipelineTask1",
//      "com.smart.tuya.testarr.pipeline.PipelineTask4"
//    ]
//  },
//    "moduleMap": {
//    "com.smart.tuya.tymodule_compiler.ModuleTest": [
//            "c",
//            "a"
//    ]
//}
//}
class MergeResultBean extends BaseConfigBean{
    private HashMap<String,List<String>> pipeLine
    private HashMap<String, List<String>> pipeLineDeps;

    HashMap<String, List<String>> getPipeLine() {
        if (pipeLine == null){
            pipeLine = new HashMap<>()
        }
        return pipeLine
    }

    HashMap<String, List<String>> getPipeLineDeps(){
        if(null == pipeLineDeps){
            pipeLineDeps = new HashMap<>();
        }
        return pipeLineDeps;
    }

    void copy(ConfigBean bean){
        this.getModuleMap().putAll(bean.getModuleMap())
        this.getServiceMap().putAll(bean.getServiceMap())
        this.getEventMap().putAll(bean.getEventMap())
        List<String> appSyncList = new LinkedList<>()
        ConfigBeanMergeUtil.sort(appSyncList,bean.getPIPE_LINE_APPLICATION_SYNC())
        this.getPipeLine().put("PIPE_LINE_TAB_LAUNCHER_STARTED",bean.getPIPE_LINE_TAB_LAUNCHER_STARTED())
        this.getPipeLine().put("PIPE_LINE_APPLICATION_ASYNC",bean.getPIPE_LINE_APPLICATION_ASYNC())
        this.getPipeLine().put("PIPE_LINE_APPLICATION_SYNC",appSyncList)


        List<String> applicationStartPipes = new LinkedList<>();
        applicationStartPipes.addAll(bean.getPIPE_LINE_APPLICATION_ASYNC())
        applicationStartPipes.addAll(appSyncList)
        // 补充应用启动的排序任务
        bean.getPIPE_LINE_BUSINESS().put(ConfigConstants.PIPELINE_APPLICATION_START, applicationStartPipes)
        // 对pipeline任务进行拓扑排序, 并重新聚合
        bean.getPIPE_LINE_BUSINESS().forEach{ business, businessPipes ->
            this.getPipeLine().put("PIPE_LINE_BUSINESS_" + business, TopoSortUtils.topoSort(business, businessPipes, bean.getPIPE_LINE_DEPS()));
        }

        this.getPipeLineDeps().putAll(bean.getPIPE_LINE_DEPS())
    }

}
