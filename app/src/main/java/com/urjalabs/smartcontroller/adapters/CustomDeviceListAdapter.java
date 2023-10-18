package com.urjalabs.smartcontroller.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.urjalabs.smartcontroller.R;
import com.urjalabs.smartcontroller.models.Device;

import java.util.List;

/** custom adapter device list
 * Created by tarun on 28-10-2017.
 */

public class CustomDeviceListAdapter extends ArrayAdapter<Device> {
    private final Activity context;
    private List<Device> mDeviceList;

    public CustomDeviceListAdapter(Activity context, List<Device> deviceList) {
        super(context, R.layout.device_list, deviceList);
        this.context = context;
        this.mDeviceList = deviceList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Device device = mDeviceList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.device_list, null, true);
            viewHolder.txtTitle = convertView.findViewById(R.id.item_name);
            viewHolder.txtType = convertView.findViewById(R.id.item_device_id);
            viewHolder.imageView = convertView.findViewById(R.id.icon_device_list);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtTitle.setText(device.getName());
        viewHolder.txtType.setText("Device ID: " + device.getDeviceId());
        viewHolder.imageView.setImageResource(R.drawable.if_ic_devices_48);
        return convertView;

    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtTitle;
        TextView txtType;
        ImageView imageView;
    }
}
