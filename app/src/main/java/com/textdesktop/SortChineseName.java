package com.textdesktop;

import android.content.Context;
import android.content.pm.ResolveInfo;

import java.text.Collator;
import java.util.Comparator;

class SortChineseName implements Comparator<ResolveInfo> {
    private final Context mContext;

    public SortChineseName(Context mContext) {
        this.mContext = mContext;
    }

    Collator cmp = Collator.getInstance(java.util.Locale.CHINA);

    @Override
    public int compare(ResolveInfo info1, ResolveInfo info2) {

        String apkName1 = info1.loadLabel(mContext.getPackageManager()).toString();
        String apkName2 = info2.loadLabel(mContext.getPackageManager()).toString();
        if (cmp.compare(apkName1, apkName2) > 0) {
            return 1;
        } else if (cmp.compare(apkName1, apkName2) < 0) {
            return -1;
        }
        return 0;
    }
}