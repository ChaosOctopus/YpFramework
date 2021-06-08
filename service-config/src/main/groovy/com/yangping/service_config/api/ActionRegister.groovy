package com.yangping.service_config.api

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task;

/**
 * @author yangping
 */
class ActionRegister {
    Task mark

    ActionRegister(Task mark) {
        this.mark = mark
    }

    List<Action<Project>> mActions = new LinkedList<>()

    void register(Action<Project> action){
        if (!mActions.contains(action)){
            mActions.add(action)
        }
    }

    void doAction(Project project){
        for (Action<Project> act : mActions){
            act.execute(project)
        }
    }
}
