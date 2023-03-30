package com.example.guitartraina.services;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.os.Bundle;

public class MyActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    private int activityCount = 0;
    private final Service service;

    public MyActivityLifecycleCallbacks(Service service) {
        this.service = service;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        activityCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        activityCount--;
        if (activityCount == 0) {
            service.stopSelf();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
