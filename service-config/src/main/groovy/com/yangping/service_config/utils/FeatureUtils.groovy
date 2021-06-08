package com.yangping.service_config.utils

import org.gradle.api.Project;

/**
 * @author yangping
 */
class FeatureUtils {

    static boolean useIgnoreAssets(Project project){
        final String key = "useIgnoreAssets"
        return project.ext.has(key) ? project.ext[key] : false
    }

    static String[] ignoreAssetsPattern(){
        "!.svn:!.git:!.ds_store:!*.scc:.*:<dir>_*:!CVS:!thumbs.db:!picasa.ini:!*~".split(":")
    }
}
