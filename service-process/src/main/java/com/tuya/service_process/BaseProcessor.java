package com.tuya.service_process;

import com.google.auto.service.AutoService;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * @author yangping
 */
public  abstract class BaseProcessor extends AbstractProcessor {
    protected Filer mFiler;
    protected Messager messager;
    protected Elements mElements;
    protected Types mTypes;
    protected Map<String,String> options;
    protected ConfigUtil mConfigUtil;
    protected ConfigBean mConfigBean;

    public BaseProcessor(ConfigBean configBean,ConfigUtil configUtil) {
        mConfigUtil = configUtil;
        mConfigBean = configBean;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        mElements = processingEnv.getElementUtils();
        mTypes = processingEnv.getTypeUtils();
        options = processingEnv.getOptions();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations != null && !annotations.isEmpty()){
            Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(getSupportAnnotationClass());
            if (elementsAnnotatedWith != null){
                for (Element element : elementsAnnotatedWith) {
                    if (checkElementKind(element)){
                        processConfig(element);
                    }
                }
            }
        }
        return false;
    }

    abstract void processConfig(Element element);

    /**
     * 检验注解所标记的对象类型是否正确，默认为 class
     *
     * @param element 要校验的元素
     * @return 检验是否成功
     * @see ElementKind#CLASS
     */
    protected boolean checkElementKind(Element element) {
        return element.getKind() == ElementKind.CLASS;
    }

    /**
     * 提供注解类
     *
     * @return
     */
    protected abstract Class<? extends Annotation> getSupportAnnotationClass();

    public boolean isAssignFromClass(TypeElement element, String parent) {
        TypeElement parentType = mElements.getTypeElement(parent);
        if (parentType == null) {
            messager.printMessage(Diagnostic.Kind.WARNING, "Cannot find class: " + parent);
            return false;
        }
        return mTypes.isAssignable(element.asType(), parentType.asType());
    }

    public <T extends Annotation> T getAnnotation(Element e) {
        return (T) e.getAnnotation(getSupportAnnotationClass());
    }
}
