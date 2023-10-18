package com.urjalabs.smartcontroller.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.urjalabs.smartcontroller.models.MqttServerProperties;
import com.urjalabs.smartcontroller.util.Constants;
import com.urjalabs.smartcontroller.util.SmartControllerUtil;

/**
 * Created by tarun on 13-11-2017.
 */

public class MQTTServerPropertySharedPrefDAOImpl implements MQTTServerPropertyDAO {
    Context mContext;

    public MQTTServerPropertySharedPrefDAOImpl(Context context) {
        mContext = context;
    }

    @Override
    public void addServerProperties(MqttServerProperties serverProperties) {
        SharedPreferences preferences = SmartControllerUtil.getAppPreference(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.MQTT_SERVER_PRIMARY, serverProperties.getPrimaryServer());
        editor.putString(Constants.MQTT_SERVER_SECONDARY, serverProperties.getSecondaryServer());
        editor.putInt(Constants.MQTT_PORT, serverProperties.getPortNo());
        editor.putBoolean(Constants.MQTT_IS_CRED_REQ, serverProperties.getAuthReq());
        editor.putString(Constants.MQTT_USER, serverProperties.getUserName());
        editor.putString(Constants.MQTT_PASSWORD, serverProperties.getPassword());
        editor.apply();
    }

    @Override
    public MqttServerProperties getServerProperties() {
        SharedPreferences preferences = SmartControllerUtil.getAppPreference(mContext);
        String primaryServer = preferences.getString(Constants.MQTT_SERVER_PRIMARY, Constants.DEFAULT);
        String secondaryServer = preferences.getString(Constants.MQTT_SERVER_SECONDARY, Constants.DEFAULT);
        int portNo = preferences.getInt(Constants.MQTT_PORT, 1883);
        MqttServerProperties mqttServerProperties = new MqttServerProperties(primaryServer, secondaryServer, portNo);
        mqttServerProperties.setAuthReq(preferences.getBoolean(Constants.MQTT_IS_CRED_REQ, false));
        mqttServerProperties.setUserName(preferences.getString(Constants.MQTT_USER, Constants.DEFAULT));
        mqttServerProperties.setPassword(preferences.getString(Constants.MQTT_PASSWORD, Constants.DEFAULT));
        return mqttServerProperties;
    }
}
