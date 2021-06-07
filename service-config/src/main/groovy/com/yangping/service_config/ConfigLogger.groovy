package com.yangping.service_config

import org.gradle.api.Project;

/**
 * @author yangping
 */
class ConfigLogger {
    private Project project
    private boolean openDebug
    ConfigLogger(Project project){
        this.project = project
        if (project.hasProperty("debugConfig")){
            openDebug = project.debugConfig
        }
    }

    def log(String log){
        if (openDebug){
            project.logger.warn("***可忽略，只是debug*** " + log)
        }
    }
}
