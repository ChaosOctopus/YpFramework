package com.tuya.mylibrary.logger;

/**
 * @author yangping
 */
public class LogUtil {
    private static ILogger sLogger = new DefaultLogger();

    public static void setILogger(ILogger iLogger){
        if (iLogger == null) return;
        sLogger  = iLogger;
    }

    public static void v(String tag, String message) {
        sLogger.v(tag, message);
    }

    public static void i(String tag, String message) {
        sLogger.i(tag, message);
    }

    public static void d(String tag, String message) {
        sLogger.d(tag, message);
    }

    public static void w(String tag, String message) {
        sLogger.w(tag, message);
    }

    public static void e(String tag, String message) {
        sLogger.e(tag, message);
    }

    public static void w(String tag, String message, Throwable t) {
        sLogger.w(tag, message, t);
    }

    public static void e(String tag, String message, Throwable t) {
        sLogger.e(tag, message, t);
    }
}
