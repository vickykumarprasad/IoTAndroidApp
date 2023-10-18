package com.urjalabs.smartcontroller.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.urjalabs.smartcontroller.R;

/**
 * Created by tarun on 28-10-2017.
 */

public class CustomFloorListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] itemName;

    public CustomFloorListAdapter(Activity context, String[] itemName) {
        super(context, R.layout.floor_list, itemName);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemName=itemName;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.floor_list, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon_home_list);
        txtTitle.setText(itemName[position]);
        imageView.setImageResource(R.drawable.home_48);
        return rowView;

    };
}
