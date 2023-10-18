package com.urjalabs.smartcontroller.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.urjalabs.smartcontroller.R;

import java.util.List;

/**
 * Created by tarun on 28-10-2017.
 */

public class CustomDashLoadListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final List<String> itemNames;
    private final List<Integer> itemCount;
    private final List<Integer> itemImages;

    public CustomDashLoadListAdapter(Activity context, List<String> itemNames, List<Integer> itemCount, List<Integer> itemImages) {
        super(context, R.layout.dash_load_list, itemNames);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.itemNames = itemNames;
        this.itemCount = itemCount;
        this.itemImages = itemImages;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.dash_load_list, null, true);
        TextView txtTitle = rowView.findViewById(R.id.dash_load_name);
        TextView txtCount = rowView.findViewById(R.id.dash_load_count);
        ImageView imageView = rowView.findViewById(R.id.icon_dash_load_list);
        txtTitle.setText(itemNames.get(position));
        txtCount.setText("Count: " + itemCount.get(position));
        imageView.setImageResource(itemImages.get(position));
        return rowView;

    }
}
