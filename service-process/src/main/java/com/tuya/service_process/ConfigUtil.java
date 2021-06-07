package com.tuya.service_process;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;

/**
 * @author yangping
 */
public final class ConfigUtil {
    public static final String MODULE_NAME_KEY = "moduleName";
    public static final String MODULE_TEMP_PATH = "moduleTmpPath";
    public static final String CFG_BEAN_SUFFIX = "_app_module.json";
    private String tempPath = "";
    private String moduleName = "";
    private Gson gson;
    private ConfigBean cfgBean;
    private Messager messager;

    private ConfigUtil(String moduleName, String tempPath, Messager messager) {
        gson = new Gson();
        this.moduleName = moduleName;
        this.tempPath = tempPath;
        this.messager = messager;
    }

    public boolean checkModule() {
        return !StringUtils.isEmpty(moduleName) && !StringUtils.isEmpty(tempPath);
    }

    public String getModuleName() {
        return moduleName;
    }

    public ConfigBean loadData() {
        if (checkModule()) {
            if (cfgBean == null) {
                cfgBean = new ConfigBean();
            }
        }
        return cfgBean;
    }

    public static ConfigUtil create(Map<String, String> options, Messager messager) {
        if (options == null) return null;
        return new ConfigUtil(options.get(MODULE_NAME_KEY), options.get(MODULE_TEMP_PATH), messager);
    }

    public void flushConfigBean(ConfigBean bean, Filer filer){
        if (bean != null && checkModule()){
            File mTempDir = new File(tempPath);
            if (!mTempDir.exists()){
                mTempDir.mkdir();
            }
            File target = new File(mTempDir.getAbsolutePath() + File.separator + moduleName + CFG_BEAN_SUFFIX);
            if (target.exists()) target.delete();
            try {
                String plainCfg = gson.toJson(bean);
                target.createNewFile();
                if (plainCfg != null && !plainCfg.trim().equals("")){
                    FileWriter fileWriter = new FileWriter(target);
                    fileWriter.write(plainCfg);
                    fileWriter.flush();
                    fileWriter.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }
}
