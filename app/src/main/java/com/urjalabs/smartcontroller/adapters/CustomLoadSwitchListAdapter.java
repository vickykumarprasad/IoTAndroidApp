package com.urjalabs.smartcontroller.adapters;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.urjalabs.smartcontroller.R;
import com.urjalabs.smartcontroller.models.Load;
import com.urjalabs.smartcontroller.models.Message;
import com.urjalabs.smartcontroller.mqtt.MQTTConnection;
import com.urjalabs.smartcontroller.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tarun on 28-10-2017.
 */

public class CustomLoadSwitchListAdapter extends ArrayAdapter<Load> {
    private final Activity context;
    private final List<Load> itemList;
    private final MQTTConnection mqttConnection;

    public CustomLoadSwitchListAdapter(Activity context, List<Load> itemList) {
        super(context, R.layout.load_list_switch, itemList);
        this.context = context;
        this.itemList = itemList;
        mqttConnection = MQTTConnection.getInstance(context, new ArrayList<String>());
    }

    public View getView(@NonNull final int position, View convertView, ViewGroup parent) {
        final Load load = itemList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.load_list_switch, null, true);
            viewHolder.txtTitle = convertView.findViewById(R.id.dash_load_name);
            viewHolder.txtType = convertView.findViewById(R.id.dash_load_loc);
            viewHolder.imageView = convertView.findViewById(R.id.icon_dash_load_list);
            viewHolder.switchStatus = convertView.findViewById(R.id.switch_status);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (load.getStatus() == Constants.SWITCH_ON) {
            viewHolder.switchStatus.setChecked(true);
        } else {
            viewHolder.switchStatus.setChecked(false);
        }
        viewHolder.txtTitle.setText(load.getName());
        viewHolder.txtType.setText("Location: " + load.getDescription());
        viewHolder.imageView.setImageResource(loadImageByType(load.getType()));
        viewHolder.switchStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // updating mDataSource
                if (isChecked) {
                    Toast.makeText(context, "Switched On", Toast.LENGTH_SHORT).show();
                    Message message=new Message(load.getTopicName(),Constants.SWITCH_ON);
                    mqttConnection.publish(message,context);
                } else {
                    Toast.makeText(context, "Switched Off", Toast.LENGTH_SHORT).show();
                    Message message=new Message(load.getTopicName(),Constants.SWITCH_OFF);
                    mqttConnection.publish(message,context);
                }
            }
        });
        return convertView;

    }

    private int loadImageByType(String loadType) {
        int image;
        switch (loadType) {
            case ("Bulb"):
                image = R.drawable.if_bulb_48;
                break;
            case ("Tubelight"):
                image = R.drawable.if_tubelight_48;
                break;
            case ("Fan"):
                image = R.drawable.if_fan_48;
                break;
            case ("AC"):
                image = R.drawable.if_ac_48;
                break;
            case ("Television"):
                image = R.drawable.if_tv_48;
                break;
            case ("Refrigerator"):
                image = R.drawable.if_refrigerator_48;
                break;
            case ("Washing Machine"):
                image = R.drawable.if_washing_machine_48;
                break;
            case ("Mixer/Juicer"):
                image = R.drawable.if_mixer_48;
                break;
            case ("Coffee Machine"):
                image = R.drawable.if_cofee;
                break;
            default:
                image = R.drawable.if_other_48;
                break;
        }
        return image;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtTitle;
        TextView txtType;
        ImageView imageView;
        Switch switchStatus;
    }
}
