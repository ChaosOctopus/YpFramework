package com.tuya.service_process;

import com.yangpingservice_annotation.YPService;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * @author yangping
 */
public class YPServiceProcess extends BaseProcessor{

    static final String PARENT_CLASS = "com.tuya.mylibrary.service.MicroService";

    public YPServiceProcess(ConfigBean configBean, ConfigUtil configUtil) {
        super(configBean, configUtil);
    }

    @Override
    void processConfig(Element element) {
        TypeElement eType = (TypeElement) element;
        String clazzName = eType.getQualifiedName().toString();
        if (isAssignFromClass(eType,clazzName)){
            YPService service = getAnnotation(eType);
            if (mConfigBean.getServiceMap().containsKey(service.value())) {
                messager.printMessage(Diagnostic.Kind.ERROR, "The " + service.value() + " has been registered by " + mConfigBean.getServiceMap().get(service.value()));
            } else {
                mConfigBean.getServiceMap().put(service.value(), clazzName);
            }
        }else{
            messager.printMessage(Diagnostic.Kind.ERROR, "The " + clazzName + " must extends from " + PARENT_CLASS);
        }
    }

    @Override
    protected Class<? extends Annotation> getSupportAnnotationClass() {
        return YPService.class;
    }
}
