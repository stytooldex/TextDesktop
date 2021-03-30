package com.textdesktop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.List;

public class top {

    public top(final MainActivity activity, View view, final int position, final List<AppInfo> packageNames, final MyAdapter adapter) {
        final PopupMenu popup = new PopupMenu(activity, view);
        popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.switchOn: {
                        String packager = packageNames.get(position).getPackageName();
                        Intent intent = activity.getPackageManager().getLaunchIntentForPackage(packager);
                        try {
                            activity.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case R.id.details: {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + packageNames.get(position).getPackageName()));
                        try {
                            activity.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case R.id.uninstall: {
                        Intent intent = new Intent(Intent.ACTION_DELETE);
                        intent.setData(Uri.parse("package:" + packageNames.get(position).getPackageName()));
                        try {
                            activity.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(activity, packageNames.get(position).getPackageName(), Toast.LENGTH_LONG).show();
                        //adapter.notifyDataSetChanged();
                        break;
                    }
                    case R.id.extractThePackage:
                        Log.e("TAG", "onMenuItemClick: ");
                        break;
                }
                return false;
            }
        });
        popup.show();

    }
}
