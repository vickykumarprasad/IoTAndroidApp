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

public class CustomRoomListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final List<String> itemNames;
    private final List<String> itemTypes;
    private final List<Integer> itemImages;
    public CustomRoomListAdapter(Activity context, List<String> itemNames,List<String> itemTypes,List<Integer> itemImages) {
        super(context, R.layout.room_list, itemNames);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemNames=itemNames;
        this.itemTypes=itemTypes;
        this.itemImages=itemImages;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.room_list, null,true);
        TextView txtTitle =  rowView.findViewById(R.id.item_name);
        TextView txtType =  rowView.findViewById(R.id.item_type);
        ImageView imageView =  rowView.findViewById(R.id.icon_room_list);
        txtTitle.setText(itemNames.get(position));
        txtType.setText("Type: "+itemTypes.get(position));
        imageView.setImageResource(itemImages.get(position));
        return rowView;

    }
}
