package com.tuya.mylibrary.config;

import android.util.JsonReader;

import com.alibaba.fastjson.JSONReader;
import com.tuya.mylibrary.logger.LogUtil;
import com.tuya.mylibrary.start.MicroContext;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * @author yangping
 * 加载注解处理器生成配置
 */
public class ModuleConfigLoader {

    private static final String TAG = ModuleConfigLoader.class.getSimpleName();
    private static final String APP_MODULE_FILE = "build_app_module.json";
    private static volatile ModuleConfigLoader sModuleConfigLoader;
    private ModuleConfigModel mSchemeModel;

    public static ModuleConfigLoader getInstance(){
        if (sModuleConfigLoader == null){
            synchronized (ModuleConfigLoader.class){
                if (sModuleConfigLoader == null){
                    sModuleConfigLoader = new ModuleConfigLoader();
                }
            }
        }
        return sModuleConfigLoader;
    }

    public Map<String, List<String>> getModuleApps() {
        return mSchemeModel == null ? null : mSchemeModel.moduleMap;
    }

    public Map<String, String> getServiceMap() {
        return mSchemeModel == null ? null : mSchemeModel.serviceMap;
    }

    public Map<String, List<String>> getPipeLineMap() {
        return mSchemeModel == null ? null : mSchemeModel.pipeLine;
    }

    public Map<String, List<EventModule>> getEventMap() {
        return mSchemeModel == null ? null : mSchemeModel.eventMap;
    }

    private ModuleConfigLoader() {
        long start = System.currentTimeMillis();
        JSONReader reader = null;
        try {
            InputStream is = MicroContext.getApplication().getAssets().open(APP_MODULE_FILE);
            reader = new JSONReader(new InputStreamReader(is, "utf-8"));
            mSchemeModel = reader.readObject(ModuleConfigModel.class);
            LogUtil.d(TAG, "ModuleConfigLoader init take: " + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            LogUtil.d(TAG, "module_app.json parser failed: " + e);
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    //do nothing
                }
            }
        }
    }
}
