package com.tuya.mylibrary.router;

import android.content.Context;
import android.os.Bundle;

/**
 * @author yangping
 */
public class UrlBuilder {
    /**
     * params to start activity
     */
    public Bundle params = new Bundle();
    /**
     * the module activity to start
     */
    public String target;
    /**
     * context
     */
    public Context context;
    /**
     * used for onActivityResult
     */
    public int requestCode = -1;

    /**
     * The origin url, maybe null
     */
    public String originUrl;

    public UrlBuilder(Context context, String target) {
        this.context = context;
        this.target = target;
    }

    public UrlBuilder(Context context, String target, String oriUrl) {
        this.context = context;
        this.target = target;
        this.originUrl = oriUrl;
    }

    public UrlBuilder(Context context, String target, String oriUrl, int requestCode, Bundle params) {
        this.context = context;
        this.target = target;
        this.originUrl = oriUrl;
        this.requestCode = requestCode;
        this.params = params;
    }

    public UrlBuilder(UrlBuilder builder) {
        this.target = builder.target;
        this.params = new Bundle(builder.params);
        this.context = builder.context;
        this.requestCode = builder.requestCode;
        this.originUrl = builder.originUrl;
    }

    public UrlBuilder putString(String key, String value) {
        params.putString(key, value);
        return this;
    }

    public UrlBuilder putExtras(Bundle bundle) {
        if (bundle != null) {
            params.putAll(bundle);
        }
        return this;
    }

    public UrlBuilder setRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }
}
