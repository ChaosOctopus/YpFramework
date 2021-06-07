package com.tuya.mylibrary.logger;

/**
 * @author yangping
 */
public interface ILogger {
    void v(String tag, String message);

    void i(String tag, String message);

    void d(String tag, String message);

    void w(String tag, String message);

    void e(String tag, String message);

    void w(String tag, String message, Throwable t);

    void e(String tag, String message, Throwable t);
}
