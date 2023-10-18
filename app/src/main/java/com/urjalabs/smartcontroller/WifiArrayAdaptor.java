package com.urjalabs.smartcontroller;


import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class WifiArrayAdaptor extends ArrayAdapter<ScanResult> {
    private final Context context;
    private final ArrayList<ScanResult> values;

    public WifiArrayAdaptor(Context context, ArrayList<ScanResult> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position);
    }

    public View getCustomView(int position){
        LayoutInflater flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textView = (TextView) flater.inflate(R.layout.wifi_spinner_item, null, false);

        textView.setText(values.get(position).SSID);
        return textView;
    }
}
