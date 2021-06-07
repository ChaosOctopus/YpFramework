package com.tuya.service_process;

import com.google.auto.service.AutoService;

import net.ltgt.gradle.incap.IncrementalAnnotationProcessor;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * @author yangping
 */
@AutoService(Processor.class)
@SupportedOptions({ConfigUtil.MODULE_NAME_KEY,ConfigUtil.MODULE_TEMP_PATH})
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes(
        {"com.yangpingservice_annotation.YPService"}
)
public class ModuleProcessor extends AbstractProcessor {
    private Messager mMessager;
    private Map<String,String> options;
    private ConfigUtil mConfigUtil;
    private ConfigBean mConfigBean;
    private ProcessingEnvironment mEnv;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        options = processingEnv.getOptions();
        mEnv = processingEnv;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        mMessager.printMessage(Diagnostic.Kind.WARNING,"get in");
        long start = System.currentTimeMillis();
        if (set != null && set.size() > 0){
            if (mConfigUtil == null){
                mConfigUtil = ConfigUtil.create(options,mMessager);
                if (mConfigUtil == null || !mConfigUtil.checkModule()){
                    mMessager.printMessage(Diagnostic.Kind.WARNING,"path empty");
                    mConfigUtil = null;
                    return false;
                }
                mConfigBean = mConfigUtil.loadData();
            }

            List<BaseProcessor> ps = new ArrayList<>();
            ps.add(new YPServiceProcess(mConfigBean,mConfigUtil));
            for (BaseProcessor p : ps) {
                p.init(mEnv);
                p.process(set, roundEnv);
            }
            mConfigUtil.flushConfigBean(mConfigBean,mEnv.getFiler());
            mMessager.printMessage(Diagnostic.Kind.WARNING,"cost time " + (System.currentTimeMillis() - start));
            return true;
        }
        return false;
    }
}
