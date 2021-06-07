package com.yangping.service_config.api

import com.yangping.service_config.ConfigLogger
import org.gradle.api.Project;

/**
 * @author yangping
 */
class ConfigScheduler {
    List<AssetsConfigSchedule> mSchedules
    private Project mProject
    private boolean isApp
    private def android
    private ConfigLogger mLogger
    private boolean hasKotlin

    ConfigScheduler(List<AssetsConfigSchedule> schedules, Project project, boolean isApp, boolean hasKotlin) {
        mSchedules = new ArrayList<>(schedules)
        mProject = project
        this.isApp = isApp
        this.hasKotlin = hasKotlin
        mLogger = new ConfigLogger(project)
    }

    def performSchedule(){
        if (mSchedules.isEmpty()) return
        def kaptt
        if (hasKotlin){
            kaptt = mProject.extensions.getByName("kapt")
        }
        def closeJavaDepMergeAssets = mProject.rootProject.hasProperty("closeJDM") ? project.rootProject.properties.get("closeJDM").toBoolean() : false
        println("========== closeJDM ${closeJavaDepMergeAssets}")
        HashMap<String,String> options = new HashMap<>()
        options.put("moduleName",mProject.getName())
        ArrayList<String> assetsSrc = new ArrayList<>()

        boolean isJavaCompilerFirst
        mLogger.log("配置assets 目录，注解处理器依赖,处理器选项等")

        mSchedules.each { schedule ->
            Map<String,String> tmp = schedule.annotationArguments(options)
            if (tmp != null) options.putAll(tmp)
            String tmpPath = schedule.appendAssetsDir()
            if (tmpPath != null && tmpPath != ""){
                assetsSrc.add(tmpPath)
            }
            // 动态配置依赖
            String processor = schedule.annotationProcessor()
            if (processor != null && processor != "") {
                project.dependencies {
                    annotationProcessor processor
                }
                if (kaptt != null) {
                    project.dependencies {
                        kapt processor
                    }
                }
            }

            if (!closeJavaDepMergeAssets && !isJavaCompilerFirst) {
                isJavaCompilerFirst = schedule.isJavaCompilerFirst()
            }
        }

        if (!assetsSrc.isEmpty()){
            android.sourceSets.main.getAssets().srcDirs(assetsSrcs.toArray())
        }

        if (!options.isEmpty()) {
            android.defaultConfig.javaCompileOptions.annotationProcessorOptions {
                arguments = options
            }
            if (kaptt != null) {
                options.each { k, v ->
                    kaptt.arguments {
                        arg(k, v)
                    }
                }
            }
        }

        if (isApp){

        }
    }
}
