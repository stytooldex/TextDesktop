package com.textdesktop;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import java.util.List;

public class MyApplication extends Application {
    private List<AppInfo> name;
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        setName(null);

        if (getContext() == null) {
            context = getApplicationContext();
        }
    }

    public static Context getContext() {
        return context;
    }

    public List<AppInfo> getName() {
        return name;
    }

    public void setName(List<AppInfo> name) {
        this.name = name;
    }
}
