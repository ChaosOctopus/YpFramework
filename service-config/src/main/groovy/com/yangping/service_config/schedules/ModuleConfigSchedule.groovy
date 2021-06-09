package com.yangping.service_config.schedules

import com.google.gson.Gson
import com.yangping.service_config.CompactGradle
import com.yangping.service_config.MergeResultBean
import com.yangping.service_config.api.AssetsConfigSchedule
import com.yangping.service_config.bean.ConfigBean
import com.yangping.service_config.bean.ConfigBeanMergeUtil
import com.yangping.service_config.utils.FeatureUtils
import org.gradle.api.Project
import org.gradle.api.Task;

/**
 * @author yangping
 */
class ModuleConfigSchedule extends AssetsConfigSchedule{

    public static final String NAME = "moduleConfig"
    private static final String TMP_PATH_KEY = "moduleTmpPath"
    static final String SWAP_DIR_NAME = "tymoduleswap"
    static final String MODULE_SUFFIX = "_app_module.json"
    private String tmp_module_path
    private Gson mGson = new Gson()

    ModuleConfigSchedule(Project project, Object android, boolean isApp) {
        super(project, android, isApp)
        tmp_module_path = project.getBuildDir().path + File.separator + "temp" + File.separator + "tuyamodile"
    }

    @Override
    Map<String, String> annotationArguments(Map<String, String> args) {
        if (args.containsKey(TMP_PATH_KEY)) {
            project.logger.warn("The key " + TMP_PATH_KEY + " has already existed in annotation arguments ")
        }
        HashMap<String, String> ret = new HashMap<>()
        ret.put(TMP_PATH_KEY, tmp_module_path)
        return ret
    }

    @Override
    String annotationProcessor() {
        if (closeAnnotationProcessor()){
            return null
        }

    }

    @Override
    void doLastActionAtMergeAssets(Object variant, Task task) {
        if (!isApp) return
        //合并子项目并生成主配置
        modifyAssetsConfig(getMergeAssetsFile(variant))
    }

    @Override
    String appendAssetsDir() {
        return tmp_module_path
    }

    @Override
    String generateSwapPath(String name) {
        return project.getBuildDir().getAbsolutePath() + File.separator + "tmp" + File.separator + SWAP_DIR_NAME + File.separator + name
    }

    @Override
    String moduleFileSuffix() {
        return MODULE_SUFFIX
    }

    def modifyAssetsConfig(File assetsIntermediatesDir) {
        File assetDir = assetsIntermediatesDir
        println assetDir
        File[] files = collectedFiles != null ? collectedFiles : assetDir.listFiles(new FileFilter() {
            @Override
            boolean accept(File file) {
                return file.name.endsWith(MODULE_SUFFIX)
            }
        })

//        List<Map<String, String>> tabConfigList = new ArrayList<>()

        ConfigBean allConfigs = new ConfigBean()

        for (File s : files) {

            project.logger.info("module config: " + s.getName())
            ConfigBean childConfig = mGson.fromJson(s.text, ConfigBean.class)
            project.logger.info("module content: " + s.text)
            ConfigBeanMergeUtil.merge(childConfig, allConfigs, project.logger)

        }

        MergeResultBean mergeResultBean = new MergeResultBean()
        //有个排序的处理，以及pipeline汇聚
        mergeResultBean.copy(allConfigs)

        File config = new File(assetDir, "module_app.json")
        config.text = mGson.toJson(mergeResultBean)

    }

    @Override
    void configAfterMergeAssetsTask(Object variant, Task task) {
        if (!isApp || FeatureUtils.useIgnoreAssets(project)) return
        //将子项目配置转移到swap目录,copy and delete
        String swap_path = generateSwapPath(variant.name)
        task.doFirst {
            File swap_file = new File(swap_path)
            if (swap_file.exists()) {
                project.delete(swap_file.listFiles())
            }
            File assetsInterDir = CompactGradle.getMergeAssetsTaskOutputDir(CompactGradle.providerGetVoid(variant, "getMergeAssets"))
            File[] moveFiles = assetsInterDir.listFiles(new FileFilter() {
                @Override
                boolean accept(File file) {
                    return file.getName().endsWith(MODULE_SUFFIX)
                }
            })
            if (moveFiles != null && moveFiles.length > 0) {
                project.copy {
                    from moveFiles
                    if (!swap_file.exists()) {
                        swap_file.mkdirs()
                    }
                    into swap_file
                }
                project.delete(moveFiles)
                project.getLogger().debug("move module swap files finished !!")
            }

        }
    }
}
