package com.urjalabs.smartcontroller.mqtt;

/**
 * Created by tarun on 12-11-2017.
 */

public interface MqttConnectionListener {
    void onConnectionAttemptResponse(String response);
    void onDisconnectionAttempt(String response);
}
