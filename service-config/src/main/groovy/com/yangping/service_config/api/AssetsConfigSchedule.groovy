package com.yangping.service_config.api

import org.gradle.api.Project

/**
 * 抽象出了apt生成的配置文件合并到assets的过程
 */
abstract class AssetsConfigSchedule {
    protected Project project
    protected def android
    protected boolean isApp
    protected File[] collectedFiles

    AssetsConfigSchedule(Project project, android, boolean isApp) {
        this.project = project
        this.android = android
        this.isApp = isApp
    }

    boolean closeAnnotationProcessor() {
        return isApp
    }

    /**
     * 是否需要JavaCompiler先于mergeAssets 运行
     * apt要先于mergeAssets运行
     * @return
     */
    boolean isJavaCompilerFirst() {
        return true
    }

    /**
     * 返回注解处理器要输入的参数
     * @params args为目前注解处理器中已经有的参数，可以校验是否和别的冲突了
     * 目前项目名称不需要传，外面默认传递
     * @return
     */
    abstract Map<String,String> annotationArguments(Map<String,String> args)

    /**
     * 返回注解处理器依赖，比如 "com.tuya.android.module:tymodule-process:0.0.4"
     * @return
     */
    abstract String annotationProcessor()

    /**
     * 返回非空的路径将加入到assets的拷贝目录中，效果等同于src/main/assets
     * @return
     */
    String appendAssetsDir() {
        return ""
    }

    /**
     * 遍历 applicationVariants 时的回调 for app
     * @param variant
     */
    void scheduleApplicationVariants(def variant) {}
    /**
     * 遍历 libraryVariants 时的回调 for android library
     * @param variant
     */
    void scheduleLibraryVariants(def variant) {}




}