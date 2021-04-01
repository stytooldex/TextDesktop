package com.textdesktop;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.textdesktop.utilsX.Tools;
import com.textdesktop.widget.ClearEditText;

import java.lang.ref.WeakReference;
import java.util.List;

import static android.provider.MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA;
import static com.textdesktop.utilsX.DangerousUtils.reboot;
import static com.textdesktop.utilsX.DangerousUtils.reboot2Bootloader;
import static com.textdesktop.utilsX.DangerousUtils.reboot2Recovery;
import static com.textdesktop.utilsX.DangerousUtils.shell;
import static com.textdesktop.utilsX.DangerousUtils.shutdown;

public class MainActivity extends ListActivity {
    private ClearEditText editText;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        KeyboardUtils.fixAndroidBug5497(this);
        KeyboardUtils.fixSoftInputLeaks(this);
        editText = findViewById(R.id.editTextPhone);
        editText.setShakeAnimation();
        new DownTask(MainActivity.this).execute(Tools.getApp());
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (editText.getText().toString().equals("")) {
            String packager = Tools.getApp().get(position).getPackageName();
            Intent intent = getPackageManager().getLaunchIntentForPackage(packager);
            startActivity(intent);
        } else {
            MyApplication app = (MyApplication) getApplication();
            String packager = app.getName().get(position).getPackageName();
            Intent aPackage = getPackageManager().getLaunchIntentForPackage(packager);
            startActivity(aPackage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Shell");
        menu.add(1, 1, 1, "关机(Root)");
        menu.add(2, 2, 2, "重启(Root)");
        menu.add(3, 3, 3, "重启到 recovery(Root)");
        menu.add(4, 4, 4, "重启到 bootloader(Root)");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                final ClearEditText text = new ClearEditText(this);
                FrameLayout layout = new FrameLayout(this);
                layout.setPadding(16, 0, 16, 0);
                layout.addView(text);
                new AlertDialog.Builder(this)
                        .setTitle("请输入")
                        .setIcon(R.mipmap.ic_launcher)
                        .setView(layout)
                        .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!text.getText().toString().equals("")) {
                                    shell(text.getText().toString());
                                }
                            }
                        })
                        .show();

                break;
            case 1:
                shutdown();
                break;
            case 2:
                reboot();
                break;
            case 3:
                reboot2Recovery();
                break;
            case 4:
                reboot2Bootloader();
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (editText.getText().toString().equals("")) {
            final String[] items = {"拨号", "短信", "微信", "QQ", "相机"};
            new AlertDialog.Builder(this)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0:
                                    try {
                                        startActivity(new Intent(Intent.ACTION_CALL_BUTTON));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case 1:
                                    Intent intent = new Intent(Intent.ACTION_MAIN);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setType("vnd.android-dir/mms-sms");
                                    try {
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case 2:
                                    try {
                                        startActivity(getPackageManager().getLaunchIntentForPackage("com.tencent.mm"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case 3:
                                    try {
                                        startActivity(getPackageManager().getLaunchIntentForPackage("com.tencent.mobileqq"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case 4:
                                    try {
                                        startActivity(new Intent().setAction(INTENT_ACTION_STILL_IMAGE_CAMERA));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                            }
                        }
                    }).create().show();
        } else {
            editText.setText("");
        }

    }

    private final BroadcastReceiver installedReceiver = new BroadcastReceiver() {

        @SuppressWarnings("unchecked")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
                String packageName = intent.getDataString();
                System.out.println("安装了:" + packageName + "包名的程序");

                new DownTask(MainActivity.this).execute();
            }
            if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
                String packageName = intent.getDataString();
                System.out.println("卸载了:" + packageName + "包名的程序");
                new DownTask(MainActivity.this).execute();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addDataScheme("package");
        registerReceiver(installedReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(installedReceiver);
        super.onDestroy();
    }

    @SuppressWarnings("deprecation")
    private static class DownTask extends AsyncTask<List<AppInfo>, Integer, List<AppInfo>> {
        private final WeakReference<MainActivity> activityWeakReference;
        private MyAdapter myAdapter;

        DownTask(MainActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @SafeVarargs
        @Override
        protected final List<AppInfo> doInBackground(List<AppInfo>... lists) {
            for (final AppInfo i : lists[0]) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(lists[0].indexOf(i));
            }
            return lists[0];
        }

        @Override
        protected void onPostExecute(List<AppInfo> result) {
            super.onPostExecute(result);
            myAdapter = new MyAdapter(result, activityWeakReference.get(), activityWeakReference.get().getApplication());
            activityWeakReference.get().setListAdapter(myAdapter);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            activityWeakReference.get().setTitle(activityWeakReference.get().getString(R.string.app_name) + "(" + values[0] + ")");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activityWeakReference.get().editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    myAdapter.getFilter().filter(cs);
                }

                @Override
                public void beforeTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                }

                @Override
                public void afterTextChanged(Editable arg0) {
                }
            });
            activityWeakReference.get().getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if (activityWeakReference.get().editText.getText().toString().equals("")) {
                        new top(activityWeakReference.get(), view, position, Tools.getApp(), myAdapter);
                    } else {
                        MyApplication application = (MyApplication) activityWeakReference.get().getApplication();
                        new top(activityWeakReference.get(), view, position, application.getName(), myAdapter);
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (KeyboardUtils.isShouldHideKeyboard(v, ev)) {
                KeyboardUtils.hideSoftInput(this);
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}