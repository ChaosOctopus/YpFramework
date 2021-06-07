package com.yangping.service_config

import com.yangping.service_config.api.AssetsConfigSchedule
import com.yangping.service_config.api.ConfigScheduler
import com.yangping.service_config.schedules.ModuleConfigSchedule
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author yangping
 */
class ModuleConfigPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        println("=======begin module config work")
        boolean isApp = project.plugins.hasPlugin("com.android.application")
        boolean isLibrary = project.plugins.hasPlugin("com.android.library")
        if (!isApp && !isLibrary) return
        def android = project.extensions.getByName("android")
        def isKotlinProject = project.plugins.findPlugin("kotlin-android") != null
        def isKotlinKapt = project.plugins.findPlugin("kotlin-kapt") != null
        if (isKotlinKapt && isKotlinProject) project.plugins.apply("kotlin-kapt")

        //自定义属性 动态配置化插件能力
        List<String> excludes = new ArrayList<>()
        if (project.hasProperty("excludeProcessorFeatures")) {
            excludes.addAll(new groovy.json.JsonSlurper().parseText(project.excludeProcessorFeatures))
        }

        // 加入调度器
        List<AssetsConfigSchedule> schedules = new LinkedList<>()
        if (!excludes.contains(ModuleConfigSchedule.NAME)){
            schedules.add(new ModuleConfigSchedule(project,android,isApp))
        }

        //执行调度器
        new ConfigScheduler(schedules,project,isApp,isKotlinProject).performSchedule()
    }
}
