package com.yangping.service_config;

/**
 * @author yangping
 */
final class CompactGradle {
    static def providerGetVoid(def target, String name) {
        def method = target.metaClass.getMetaMethod("{$name}Provider", null)
        if (method != null) {
            return method.invoke(target,null).get()
        }
        return target.metaClass.getMetaMethod(name,null).invoke(target,null)
    }
    //MergeSourceSetFolders.getOutputDir(),maybe return File or Provider<Directory>
    static File getMergeAssetsTaskOutputDir(def task){
        def outputTmp = task.getOutputDir()
        if (outputTmp instanceof File){
            return outputTmp
        }else{
            return outputTmp.get().getAsFile()
        }
    }
}
