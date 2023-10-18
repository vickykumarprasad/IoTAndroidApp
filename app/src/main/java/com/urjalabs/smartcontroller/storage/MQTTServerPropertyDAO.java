package com.urjalabs.smartcontroller.storage;

import com.urjalabs.smartcontroller.models.MqttServerProperties;

/**
 * Created by tarun on 13-11-2017.
 */

public interface MQTTServerPropertyDAO {
    void addServerProperties(MqttServerProperties serverProperties);
    MqttServerProperties getServerProperties();
}
