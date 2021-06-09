package com.yangping.service_config.api

import com.yangping.service_config.CompactGradle
import com.yangping.service_config.utils.FeatureUtils
import org.gradle.api.Project
import org.gradle.api.Task

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
     *  返回临时存储文件的目录
     *  name 建议是 variant.name
     * @param name
     * @return
     */
    abstract String generateSwapPath(String name)

    /**
     * 在 Merge doFirst action的时候用于得到指定结尾的文件
     * @return
     */
    abstract String moduleFileSuffix()

    void configAfterPackageOrBundleTask(def variant, ActionRegister register) {
        if (FeatureUtils.useIgnoreAssets(project)) return
        def targetFile = CompactGradle.getMergeAssetsTaskOutputDir(CompactGradle.providerGetVoid(variant, "getMergeAssets"))
        String swap_path = generateSwapPath(variant.name)
        register.register {
            File swap_file = new File(swap_path)
            if (swap_file.exists()) {
                project.logger.debug(" ======== begin to return to " + targetFile)
                project.copy {
                    from swap_file
                    into targetFile
                }
            }
        }
        register.register {
            File swap_file = new File(swap_path)
            if (swap_file.exists())
                project.delete {
                    delete swap_file
                }
        }
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

    void doFirstActionAtMergeAssets(def variant, Task task) {
        if (FeatureUtils.useIgnoreAssets(project)) {
            //input 里的文件不会被过滤掉，而在lastAciton时的output里，这些文件已经被过滤了，所以不能在最后的action里进行结合操作。
            List<File> collected = new LinkedList<>()
            task.getInputs().files.each { File assertDir ->
                File[] results = assertDir.listFiles(new FileFilter() {
                    @Override
                    boolean accept(File file) {
                        return file.name.endsWith(moduleFileSuffix())
                    }
                })
                if (results?.length > 0)
                    collected.addAll(results)
            }
            collectedFiles = collected.toArray()
        }
    }

    void doLastActionAtMergeAssets(def variant, Task task) {}

    void doLastActionAtCompilerJava(def variant, Task task) {}

    void configAfterMergeAssetsTask(def variant, Task task) {}

    protected File getMergeAssetsFile(def variant) {
        return getMergeAssetsTaskOutputDir(providerGetVoid(variant, "getMergeAssets"))
    }
}