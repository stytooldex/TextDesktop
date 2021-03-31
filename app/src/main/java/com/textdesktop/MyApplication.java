package com.textdesktop;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.List;

public class MyApplication extends Application {
    private List<AppInfo> name;
    private static WeakReference<Context> context;

    @Override
    public void onCreate() {
        super.onCreate();
        setName(null);

        //if (getContext() == null) {
            context = new WeakReference<>(getApplicationContext());
        //}
    }

    public static Context getContext() {
        return context.get();
    }

    public List<AppInfo> getName() {
        return name;
    }

    public void setName(List<AppInfo> name) {
        this.name = name;
    }
}
