package com.tuya.mylibrary.start;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tuya.mylibrary.util.AppUtils;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yangping
 */
public class LauncherApplicationAgent {

    private static final String TAG = LauncherApplicationAgent.class.getSimpleName();
    private Application mApplication;
    private String processName;
    private String packageName;
    private volatile String appVersion;
    // launcher timestamp
    private long startTimestamp;
    private final Handler sAppHandler = new Handler(Looper.getMainLooper());
    private final List<CrossActivityLifecycleCallback> mCrossActivityLifecycleCallbacks = new CopyOnWriteArrayList<>();
    private final AtomicInteger creationCount = new AtomicInteger();
    private final AtomicInteger startCount = new AtomicInteger();
    private WeakReference<Activity> mWeakReference;

    private static volatile LauncherApplicationAgent sLauncherApplicationAgent;
    private volatile boolean created = false;

    private LauncherApplicationAgent(){

    }

    public static LauncherApplicationAgent getInstance() {
        if (sLauncherApplicationAgent == null) {
            synchronized (LauncherApplicationAgent.class) {
                if (sLauncherApplicationAgent == null) {
                    sLauncherApplicationAgent = new LauncherApplicationAgent();
                }
            }
        }
        return sLauncherApplicationAgent;
    }

    public void onCreate(Application application){
        if (created){
            Log.e(TAG, "onCreate has already been init" );
        }
        startTimestamp = System.currentTimeMillis();
        created = true;
        this.mApplication = application;
        processName = AppUtils.getProcessName(application);
        packageName = application.getPackageName();
        application.registerActivityLifecycleCallbacks(new CrossActivityLifecycleCallbacks());
    }

    /**
     * Similar to {@link Activity#runOnUiThread(Runnable)}, in
     * static manner.
     */
    public void runOnUiThread(final Runnable runnable) {
        sAppHandler.post(runnable);
    }

    /**
     * 如果获取到的进程名为空也认为是主进程
     * @return
     */
    public boolean isMainProcess() {
        return TextUtils.equals(processName, packageName) || TextUtils.isEmpty(processName);
    }

    public String getPackageName() {
        return packageName;
    }

    public String getProcessName() {
        return processName;
    }

    public String getAppVersion() {
        if (appVersion == null) {
            appVersion = AppUtils.getAppVersionName(getApplication());
        }
        return appVersion;
    }

    public String getAppName() {
        return AppUtils.getApplicationName(getApplication(), packageName);
    }

    public Application getApplication() {
        if (mApplication == null) {
            throw new RuntimeException("Must call onCreate(application) first");
        }
        return mApplication;
    }

    public long getStartTimestamp(){
        return startTimestamp;
    }

    public void registerCrossActivityLifecycleCallback(final CrossActivityLifecycleCallback callback) {
        if (callback == null) {
            RuntimeException here = new RuntimeException(
                    "callback must not be null");
            here.fillInStackTrace();
            Log.w(TAG, "Called: " + this, here);
            return;
        }

        mCrossActivityLifecycleCallbacks.add(callback);

        /* do the sticky cross life callback */
        if (creationCount.get() > 0) {
            sAppHandler.post(new StickCrossRunnable(callback, "onCreated"));
        }

        if (startCount.get() > 0) {
            sAppHandler.post(new StickCrossRunnable(callback, "onStarted"));
        }
    }

    public void unregisterCrossActivityLifecycleCallback(
            final CrossActivityLifecycleCallback callback) {
        mCrossActivityLifecycleCallbacks.remove(callback);
    }


    public interface CrossActivityLifecycleCallback {
        /**
         * First activity within this application is created
         *
         */
        void onCreated(Activity activity);

        /**
         * First activity within this application is started
         */
        void onStarted(Activity activity);

        /**
         * All activities within this application are stopped
         */
        void onStopped(Activity activity);

        /**
         * All activities within this application are destroyed
         */
        void onDestroyed(Activity activity);
    }

    private class CrossActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks{

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            mWeakReference = new WeakReference<>(activity);
            if (creationCount.getAndIncrement() == 0
                    && !mCrossActivityLifecycleCallbacks.isEmpty()) {
                for (final CrossActivityLifecycleCallback callback : mCrossActivityLifecycleCallbacks) {
                    callback.onCreated(activity);
                }
            }
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            if (startCount.getAndIncrement() == 0 && !mCrossActivityLifecycleCallbacks.isEmpty()) {
                for (final CrossActivityLifecycleCallback callback : mCrossActivityLifecycleCallbacks) {
                    callback.onStarted(activity);
                }
            }
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            if (creationCount.decrementAndGet() == 0
                    && !mCrossActivityLifecycleCallbacks.isEmpty()) {
                for (final CrossActivityLifecycleCallback callback : mCrossActivityLifecycleCallbacks) {
                    callback.onDestroyed(activity);
                }
            }
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            if (startCount.decrementAndGet() == 0 && !mCrossActivityLifecycleCallbacks.isEmpty()) {
                for (final CrossActivityLifecycleCallback callback : mCrossActivityLifecycleCallbacks) {
                    callback.onStopped(activity);
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    }

    private class StickCrossRunnable implements Runnable{

        private CrossActivityLifecycleCallback callback;
        private String method;

        public StickCrossRunnable(CrossActivityLifecycleCallback callback, String method) {
            this.callback = callback;
            this.method = method;
        }



        @Override
        public void run() {
            if (mWeakReference != null){
                Activity activity = mWeakReference.get();
                if (activity != null && callback != null){
                    if ("onCreated".equals(method)) {
                        callback.onCreated(activity);
                    } else if ("onStarted".equals(method)) {
                        callback.onStarted(activity);
                    }
                }
            }
            callback = null;
            method = null;
        }
    }
}
