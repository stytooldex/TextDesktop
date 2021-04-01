package com.textdesktop.utilsX;

import android.content.Intent;
import android.widget.Toast;

import com.textdesktop.AppInfo;
import com.textdesktop.AppInfoUtil;
import com.textdesktop.MyApplication;

import java.util.List;

public class Tools {
    public static List<AppInfo> getApp() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        return AppInfoUtil.getInstance(MyApplication.getContext()).getAppInfoByIntent(intent);
    }

    public static void toast(String string) {
        Toast.makeText(MyApplication.getContext(), string, Toast.LENGTH_LONG).show();
    }

}
