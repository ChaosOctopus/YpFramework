package com.tuya.mylibrary.pipeline;

import android.os.Debug;
import android.util.Log;

import com.tuya.mylibrary.SmartInitializer;

import java.lang.reflect.Field;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yangping
 */
public class SmartExecutor {

    public static final String TAG = SmartExecutor.class.getSimpleName();
    private volatile static SmartExecutor sSmartExecutor;
    private LinkedBlockingQueue<Runnable> poolWorkQueue;
    private volatile Executor mExecutor;

    public static SmartExecutor getInstance(){
        if (sSmartExecutor == null){
            synchronized (SmartExecutor.class){
                if (sSmartExecutor == null){
                    sSmartExecutor = new SmartExecutor();
                }
            }
        }
        return sSmartExecutor;
    }

    private SmartExecutor(){

    }

    public Executor getExecutor() {
        return getExecutorInternal();
    }

    public void setExecutor(Executor executor) {
        if (executor == null) return;
        if (this.mExecutor == null){
            this.mExecutor = executor;
        }else{
            Log.e(TAG, "executor has been set" );
        }
    }

    private Executor getExecutorInternal() {
        if (mExecutor == null){
            synchronized (SmartExecutor.class){
                if (mExecutor == null){
                    mExecutor = initExecutor();
                }
            }
        }
        return mExecutor;
    }

    private Executor initExecutor() {
        final  int coreSize = Runtime.getRuntime().availableProcessors() + 1;
        final  int maxSize = 4 * coreSize + 1;
        final  int keepAlive = 1;
        poolWorkQueue = new LinkedBlockingQueue<Runnable>(128);
        final ThreadFactory sThreadFactory = new ThreadFactory() {

            private final AtomicInteger mCount = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,"task #"+mCount.getAndIncrement());
            }
        };
        return new ThreadPoolExecutor(coreSize,maxSize,keepAlive, TimeUnit.SECONDS,
                poolWorkQueue,sThreadFactory,new CoordinatorRejectHandler());
    }



    public void postTask(final TaggedRunnable runnable){
        Runnable timeRunnable = () -> runWithTiming(runnable);
        getExecutor().execute(timeRunnable);
    }

    public void postTasks(final TaggedRunnable... runnables) {
        for (final TaggedRunnable runnable : runnables) {
            if (runnable != null) {
                postTask(runnable);
            }
        }
    }

    public void runTask(final TaggedRunnable runnable) {
        runWithTiming(runnable);
    }

    public void runTasks(final TaggedRunnable... runnables) {
        for (final TaggedRunnable runnable : runnables) {
            if (runnable != null) {
                runWithTiming(runnable);
            }
        }
    }

    private void runWithTiming(TaggedRunnable runnable) {
        boolean failed = false;
        final boolean debug = SmartInitializer.isDebug;
        long time = 0, cputime = 0;
        if (debug) {
            time = System.nanoTime();
            cputime = Debug.threadCpuTimeNanos();
        }
        try{
            runnable.run();
        }catch (final RuntimeException e){
            failed = true;
        }finally {
            if (debug) {
                cputime = (Debug.threadCpuTimeNanos() - cputime) / 1000000;
                time = (System.nanoTime() - time) / 1000000;
                Log.i(TAG, "Timing - " + Thread.currentThread().getName() + " " + runnable.tag
                        + (failed ? " (failed): " : ": ")
                        + cputime + "ms (cpu) / " + time + "ms (real)");
            }
        }

    }

    public static abstract class TaggedRunnable implements Runnable{
        private final String tag;

        public TaggedRunnable(String tag) {
            this.tag = tag;
        }

        @Override
        public String toString() {
            return getClass().getName() + '@' + tag;
        }
    }


    public class CoordinatorRejectHandler implements RejectedExecutionHandler{

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            Object[] objects = poolWorkQueue.toArray();
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (Object a : objects) {
                if (a.getClass().isAnonymousClass()) {
                    sb.append(getOuterClass(a));
                } else {
                    sb.append(a.getClass());
                }
                sb.append(',').append(' ');
            }
            sb.append(']');

            throw new RejectedExecutionException("Task " + r.toString() +
                    " rejected from " +
                    executor.toString() + " in " + sb.toString());
        }

        private Object getOuterClass(Object inner) {

            try {
                Field outer = inner.getClass().getDeclaredField("this$0");
                outer.setAccessible(true);
                return outer.get(inner);

            } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e1) {
                e1.printStackTrace();
            }
            return inner;
        }
    }
}


