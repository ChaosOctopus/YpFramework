package com.tuya.mylibrary.util;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tuya.mylibrary.start.MicroContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author yangping
 */
public class AppUtils {
    public static final String TAG = AppUtils.class.getSimpleName();

    public static String getAppVersionName(Context context){
        String version = "0";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionName;
            if (TextUtils.isEmpty(version)) version = "0";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static String getApplicationName(Context context, String packageName) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            return (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (Exception ex) {
            Log.e(TAG, "getApplicationName error", ex);
            return "";
        }
    }

    public static String getProcessName(Application application) {
        int id = Process.myPid();
        String name = readProcNameFromService(application, id);
        if (TextUtils.isEmpty(name)) {
            name = readProcNameFromCmd(id);
        }
        return name;
    }


    /**
     * 从 android runtime 中读出进程名称
     *
     * @param context
     * @param myPid
     * @return
     */
    private static String readProcNameFromService(@NonNull Context context, int myPid) {
        try {
            ActivityManager mActivityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            if (mActivityManager == null) {
                return "";
            }
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                    .getRunningAppProcesses()) {
                if (appProcess.pid == myPid) {
                    return appProcess.processName;
                }

            }
        } catch (Exception e) {
        }

        return "";
    }

    /**
     * 从内核中读取进程名称
     *
     * @param myPid
     * @return
     */
    private static String readProcNameFromCmd(int myPid) {
        BufferedReader reader = null;
        try {
            File file = new File("/proc/" + myPid + "/" + "cmdline");
            reader = new BufferedReader(new FileReader(file));
            return reader.readLine().trim();
        } catch (Throwable e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ClassLoader getClassLoader() {
        return MicroContext.getApplication().getClassLoader();
    }
}
