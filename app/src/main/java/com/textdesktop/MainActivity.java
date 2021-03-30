package com.textdesktop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

import com.textdesktop.widget.ClearEditText;

import java.util.List;

import static android.provider.MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA;
import static com.textdesktop.utilsX.DangerousUtils.reboot;
import static com.textdesktop.utilsX.DangerousUtils.reboot2Bootloader;
import static com.textdesktop.utilsX.DangerousUtils.reboot2Recovery;
import static com.textdesktop.utilsX.DangerousUtils.shell;
import static com.textdesktop.utilsX.DangerousUtils.shutdown;

public class MainActivity extends Activity {
    private MyAdapter adapter;
    private MyApplication app;
    private ClearEditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {/**/
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        KeyboardUtils.fixAndroidBug5497(this);
        KeyboardUtils.fixSoftInputLeaks(this);
        editText = findViewById(R.id.editTextPhone);
        editText.setShakeAnimation();
        GridView gridView = findViewById(R.id.gridView);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        final List<AppInfo> list = AppInfoUtil.getInstance(this).getAppInfoByIntent(intent);
        adapter = new MyAdapter(list, this, getApplication());
        gridView.setTextFilterEnabled(true);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (editText.getText().toString().equals("")) {
                    String packager = list.get(position).getPackageName();
                    Intent intent = getPackageManager().getLaunchIntentForPackage(packager);
                    startActivity(intent);
                } else {
                    app = (MyApplication) getApplication();
                    String packager = app.getName().get(position).getPackageName();
                    Intent aPackage = getPackageManager().getLaunchIntentForPackage(packager);
                    startActivity(aPackage);
                }
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (editText.getText().toString().equals("")) {
                    new top(MainActivity.this, view, position, list, adapter);
                } else {
                    app = (MyApplication) getApplication();
                    new top(MainActivity.this, view, position, app.getName(), adapter);
                }
                return true;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user change the text
                adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
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
                final ClearEditText editText1 = new ClearEditText(this);
                new AlertDialog.Builder(this)
                        .setTitle("请输入")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(editText1)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                shell(editText1.getText().toString());
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
        //super.onBackPressed();
        if (editText.getText().toString().equals("")) {
            final String[] items = {"拨号", "短信", "微信", "QQ", "相机"};
            new AlertDialog.Builder(this)
                    /*.setIcon(R.mipmap.ic_launcher)
                    .setTitle("列表dialog")*/
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