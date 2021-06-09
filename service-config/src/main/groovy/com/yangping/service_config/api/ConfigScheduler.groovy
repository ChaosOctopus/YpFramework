package com.yangping.service_config.api

import com.yangping.service_config.ConfigLogger
import com.yangping.service_config.utils.FeatureUtils
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskState;
import static com.yangping.service_config.CompactGradle.providerGetVoid

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
            Map<Task,ActionRegister> registers = new HashMap<>()
            Map.Entry<Task, ActionRegister> findRegister
            mProject.gradle.getTaskGraph().whenReady { graph ->
                findRegister = registers.find {
                    graph.hasTask(it.key)
                }
            }

            mProject.gradle.getTaskGraph().afterTask { Task markedTask, TaskState state ->
                if ((!FeatureUtils.useIgnoreAssets(mProject)) && findRegister && markedTask.getDependsOn().contains(findRegister.key)){
                    findRegister.value.doAction(mProject)
                }
            }

            installIgnoreAssetsPattern(android)
            android.applicationVariants.all { variant ->
                mSchedules.each { schedule ->
                    schedule.scheduleApplicationVariants(variant)
                }
                Task mergeAssetsTask = providerGetVoid(variant, "getMergeAssets")

                Task javaCompilerTask = providerGetVoid(variant, "getJavaCompiler")

                if (!closeJavaDepMergeAssets && isJavaCompilerFirst)
                    mergeAssetsTask.dependsOn javaCompilerTask
                scheduleTaskAction(variant,mergeAssetsTask,javaCompilerTask)
                Task afterPackageTask = mProject.task("mark" + capitalize(variant.name))
                afterPackageTask.setDescription("标记某个打包任务assemble，或者bundle")
                // 参考文档，Registers a task to be executed before any main output tasks
                // like the assemble or bundle tasks are invoked
                //通过 register以后，这任务被assemble和bundle 依赖，我们在gradle.getTaskGraph().afterTask里
                //找到该任务就可以知道它执行完我进行一些特定操作，所以afterPackageTask是标记用的
                def params = new Class[1]
                params[0] = Task.class
                def registerMethod = variant.metaClass.getMetaMethod("register", params)
                if (registerMethod) {//新版 android plugin
                    variant.register(afterPackageTask)//generate apk ,aab file
                    //makeApkFromBundleFor name from com.android.build.gradle.internal.tasks.BundleToApkTask
                    //generate bundle.apks file
                    Task extraApk = mProject.tasks.findByName("makeApkFromBundleFor${capitalize(variant.name)}")
                    if (extraApk != null) {
                        extraApk.dependsOn(afterPackageTask)
                    }
                } else { //旧版本的，也就是没有bundle功能，按老的逻辑依赖packageapplication
                    Task packageTask = providerGetVoid(variant, "getPackageApplication")
                    packageTask.dependsOn(afterPackageTask)
                }

                ActionRegister register = new ActionRegister(afterPackageTask)
                registers.put(afterPackageTask, register)
//                packageTask.finalizedBy afterPackageTask
                mSchedules.each { schedule -> schedule.configAfterPackageOrBundleTask(variant, register) }
            }


        }else{
            android.libraryVariants.all { variant ->
                mLog.log("开始调度 libraryVariants-> " + variant.name)
                mSchedules.each { schedule ->
                    schedule.scheduleLibraryVariants(variant)
                }

                Task mergeAssetsTask = providerGetVoid(variant, "getMergeAssets")
                Task javaCompilerTask = providerGetVoid(variant, "getJavaCompiler")

                if (!closeJavaDepMergeAssets && isJavaCompilerFirst)
                    mergeAssetsTask.dependsOn javaCompilerTask
                scheduleTaskAction(variant, mergeAssetsTask, javaCompilerTask)
            }
        }


    }

    def scheduleTaskAction(def variant, Task merge,Task compiler){
        merge.doFirst {
            mSchedules.each {schedule ->
                 schedule.doFirstActionAtMergeAssets(variant, merge)
            }
        }

        merge.doLast {
            mSchedules.each { schedule -> schedule.doLastActionAtMergeAssets(variant, merge) }
        }
        compiler.doLast {
            mSchedules.each { schedule -> schedule.doLastActionAtCompilerJava(variant, compiler) }
        }

        Task afterMergeTask = project.task("after" + merge.name)
        merge.finalizedBy afterMergeTask
        mSchedules.each { schedule -> schedule.configAfterMergeAssetsTask(variant, afterMergeTask) }
    }

    void installIgnoreAssetsPattern(def android){
        mProject.tasks.whenObjectAdded { Task t ->
            if (t.name == "checkoutAppShellBranch" && FeatureUtils.useIgnoreAssets(mProject)){
                String oldPattern = android.aaptOptions.ignoreAssets ?: ""
                StringBuilder newPatternBuilder = new StringBuilder(oldPattern)
                FeatureUtils.ignoreAssetsPattern().each {
                    if (!oldPattern.contains(it)) {
                        newPatternBuilder.append(":$it")
                    }
                }
                mSchedules.each {
                    String pattern = "<file>*${it.moduleFileSuffix()}"
                    if (!oldPattern.contains(pattern))
                        newPatternBuilder.append(":$pattern")
                }

                String newPattern = newPatternBuilder.toString()
                if (newPattern.startsWith(":"))
                    newPattern = newPattern.substring(1)
                android.aaptOptions.setIgnoreAssets(newPattern)
            }
        }
    }

    static String capitalize(String string) {
        StringBuilder sb = new StringBuilder()
        sb.append(string.substring(0, 1).toUpperCase(Locale.US)).append(string.substring(1))
        return sb.toString()
    }
}
