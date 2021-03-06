package com.textdesktop;

import android.annotation.SuppressLint;
import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends BaseAdapter implements Filterable {

    private final Application apply;
    private List<AppInfo> infoList;
    private final MainActivity activity;
    private final Object mLock = new Object();
    private ArrayList<AppInfo> mOriginalValues;
    private ArrayFilter mFilter;

    public MyAdapter(List<AppInfo> list, MainActivity mainActivity, Application application) {
        infoList = list;
        activity = mainActivity;
        apply = application;
    }

    @Override
    public int getCount() {
        return /*packageNames.size()*/Math.max(infoList.size(), 0);
    }

    @Override
    public Object getItem(int position) {
        return infoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(infoList.get(position).getAppName());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private MyApplication app;

    private class ArrayFilter extends Filter {
        //????????????
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();//???????????????
            //???????????????????????????????????????????????????????????????
            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<>(infoList);
                }
            }
            //?????????????????????
            if (prefix == null || prefix.length() == 0) {
                ArrayList<AppInfo> list;
                synchronized (mLock) {//????????????????????????????????????
                    list = new ArrayList<>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();//???????????????results???????????????????????????????????????
            } else {
                String prefixString = prefix.toString().toLowerCase();//???????????????

                ArrayList<AppInfo> values;
                synchronized (mLock) {//????????????????????????????????????
                    values = new ArrayList<>(mOriginalValues);
                }
                final int count = values.size();
                final ArrayList<AppInfo> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    final AppInfo value = values.get(i);//???List<User>?????????User??????
//                    final String valueText = value.toString().toLowerCase();
                    final String valueText = value.getAppName().toLowerCase();//User?????????name???????????????????????????
                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString) || valueText.contains(prefixString)) {//???????????????????????????
                        newValues.add(value);//?????????item????????????????????????
                    } else {//????????????????????????
                        final String[] words = valueText.split(" ");
                        final int wordCount = words.length;
                        // Start at index 0, in case valueText starts with space(s)
                        for (String word : words) {
                            if (word.startsWith(prefixString)) {//????????????????????????break?????????for??????
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }
                results.values = newValues;//?????????results??????????????????List<User>??????
                results.count = newValues.size();
            }
            return results;
        }

        //????????????
        @Override
        protected void publishResults(CharSequence prefix, FilterResults results) {
            //noinspection unchecked
            infoList = (List<AppInfo>) results.values;//?????????Adapter???????????????????????????Results
            if (results.count > 0) {
                notifyDataSetChanged();
                //for (AppInfo info : infoList) {
                if (infoList != null) {
                    //Log.e("TAG", info.getAppName() + "\n" + infoList.size());
                    app = (MyApplication) apply; //???????????????????????????MyApplication
                    app.setName(infoList);  //OK??????????????????????????????
                }
                //}
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    private static class ViewHolder {
        TextView textView;
    }
}
