package com.tuya.mylibrary.service;

import android.content.Context;
import android.os.Bundle;

import com.tuya.mylibrary.router.UrlBuilder;
import com.tuya.mylibrary.service.MicroService;

/**
 * @author yangping
 */
public abstract class SchemeService extends MicroService {

    public abstract void setScheme(String scheme);

    public abstract String getAppScheme();

    public abstract boolean isSchemeSupport(String scheme);

    public abstract void execute(Context context, String url, Bundle bundle,int requestCode);

    public abstract void execute(final UrlBuilder urlBuilder);

    public abstract void sendEvent(String eventName,Bundle bundle);

    public abstract String getModuleClassByTarget(String target);

    public abstract void registerRouteEventListener();
}
