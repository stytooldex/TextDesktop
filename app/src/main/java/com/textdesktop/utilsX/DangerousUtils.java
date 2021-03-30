package com.textdesktop.utilsX;

import android.widget.Toast;

import com.textdesktop.MyApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DangerousUtils {
    public static void shutdown() {/*关机*/
        shell("reboot -p");
    }

    public static void reboot() {/*重启*/
        shell("reboot");
    }

    public static void reboot2Recovery() {/*重启到 recovery*/
        shell("reboot recovery");
    }

    public static void reboot2Bootloader() {/*重启到 bootloader*/
        shell("reboot bootloader");
    }

    public static void shell(String command) {
        try {
            if (ShellUtils.checkRootPermission()) {
                Process su = Runtime.getRuntime().exec(command);
            } else {
                tipTop();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void tipTop() {
        Toast.makeText(MyApplication.getContext(), "需要Root", Toast.LENGTH_LONG).show();
    }

    public void shellExec() {
        Runtime mRuntime = Runtime.getRuntime();
        try {
            //Process中封装了返回的结果和执行错误的结果
            Process mProcess = mRuntime.exec("adb version");
            BufferedReader mReader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
            StringBuilder mRespBuff = new StringBuilder();
            char[] buff = new char[1024];
            int ch = 0;
            while ((ch = mReader.read(buff)) != -1) {
                mRespBuff.append(buff, 0, ch);
            }
            mReader.close();
            System.out.print(mRespBuff.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
