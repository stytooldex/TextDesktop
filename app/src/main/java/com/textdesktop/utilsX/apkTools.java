package com.textdesktop.utilsX;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.textdesktop.MainActivity;

import java.lang.ref.WeakReference;

public class apkTools {
    public static void ApkToo(MainActivity activity, String packageName) {
        new DownTask(activity).execute(packageName);
    }

    private static class DownTask extends AsyncTask<String, Integer, String> {
        private final WeakReference<MainActivity> activityWeakReference;
        private ProgressDialog dialog;
        private int data = 0;

        @SuppressWarnings("deprecation")
        DownTask(MainActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected final String doInBackground(String... lists) {
            data = lists[0].hashCode();
            for (String i : lists) {
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialog.setProgress(values[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(activityWeakReference.get());
            dialog.setCancelable(false);
            dialog.setMax(data);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setIndeterminate(false);
            dialog.show();
        }
    }
}
