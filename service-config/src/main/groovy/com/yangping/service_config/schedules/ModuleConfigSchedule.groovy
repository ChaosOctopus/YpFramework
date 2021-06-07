package com.yangping.service_config.schedules

import com.google.gson.Gson
import com.yangping.service_config.api.AssetsConfigSchedule
import org.gradle.api.Project;

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
    String appendAssetsDir() {
        return tmp_module_path
    }
}
