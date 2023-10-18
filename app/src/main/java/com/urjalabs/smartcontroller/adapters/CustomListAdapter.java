package com.urjalabs.smartcontroller.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.urjalabs.smartcontroller.R;

import java.util.List;

/**
 * Created by tarun on 28-10-2017.
 */

public class CustomListAdapter extends BaseAdapter {
    private final Context  context;
    private final List<String> itemNames;
    LayoutInflater inflater;
    public CustomListAdapter(Context context, List<String> itemNames) {
        this.context = context;
        this.itemNames = itemNames;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public boolean isEnabled(int position) {
        if (position == 0) {
            // Disable the first item from Spinner
            // First item will be use for hint
            return false;
        } else {
            return true;
        }
    }
    @Override
    public int getCount() {
        return itemNames.size();
    }

    @Override
    public Object getItem(int i) {
        return itemNames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView,
                                ViewGroup parent) {
        View rowView=inflater.inflate(R.layout.spinner_item, null);
        TextView txtTitle = rowView.findViewById(R.id.item_text);
        txtTitle.setText(itemNames.get(position));
        if (position == 0) {
            // Set the hint text color gray
            txtTitle.setTextColor(Color.GRAY);
        } else {
            txtTitle.setTextColor(Color.BLACK);
        }
        return rowView;
    }

}
