package com.urjalabs.smartcontroller.mqtt;

import android.content.Context;
import android.util.Log;

import com.urjalabs.smartcontroller.models.Message;
import com.urjalabs.smartcontroller.models.MqttServerProperties;
import com.urjalabs.smartcontroller.storage.MQTTServerPropertyDAO;
import com.urjalabs.smartcontroller.storage.MQTTServerPropertySharedPrefDAOImpl;
import com.urjalabs.smartcontroller.util.Constants;
import com.urjalabs.smartcontroller.util.SmartControllerUtil;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * MQTT connection
 * Created by tarun on 12-11-2017.
 */

public class MQTTConnection {
    private static String TAG = MQTTConnection.class.getSimpleName();
    private static MQTTConnection sMqttConnection = null;
    private static List<String> mTopics = new ArrayList<>();
    private String clientId;
    private static int retryCount = 0;
    private MqttAndroidClient mqttAndroidClient;
    private List<MqttConnectionListener> connectionListeners = new ArrayList<>();
    private MQTTServerPropertyDAO serverPropertyDAO;

    public String getServer() {
        return server;
    }

    private String server;

    private MQTTConnection() {

    }

    public static MQTTConnection getInstance(Context context, List<String> topics) {
        if (null == sMqttConnection) {
            sMqttConnection = new MQTTConnection();
            mTopics.clear();
            mTopics.addAll(topics);
            sMqttConnection.serverPropertyDAO = new MQTTServerPropertySharedPrefDAOImpl(context);
            sMqttConnection.init(sMqttConnection.getURI(true), context);
        }
        return sMqttConnection;
    }

    private void subscribeToTopic(final String subscriptionTopic) {

        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "subscribe onSuccess");
                    mTopics.add(subscriptionTopic);

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Failed to subscribe");
                }
            });

        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    //should be called very first time
    private void init(String uri, Context context) {
        setClientId(context);
        connectToBroker(uri, context);
    }

    private void connectToBroker(final String uri, final Context context) {
        sendResponseToConnectionListener(Constants.CONNECTING);
        mqttAndroidClient = new MqttAndroidClient(context.getApplicationContext(), uri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    for (String topic : mTopics)
                        subscribeToTopic(topic);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        MqttServerProperties serverProperties = serverPropertyDAO.getServerProperties();
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        if (serverProperties.getAuthReq()) {
            mqttConnectOptions.setUserName(serverProperties.getUserName());
            mqttConnectOptions.setPassword(serverProperties.getPassword().toCharArray());
        }
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setAutomaticReconnect(true);
        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "connect onSuccess");
                    sendResponseToConnectionListener(Constants.CONNECTED+" to "+server);
                    retryCount = 0;
                    for (String topic : mTopics)
                        subscribeToTopic(topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Failed to connect to: " + uri);
                   sendResponseToConnectionListener("Failed to connect");
                    if (retryCount < 1) {
                        connectToBroker(getURI(false), context);
                        retryCount++;
                    }
                }
            });


        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    private void setClientId(Context context) {
        String srNo = SmartControllerUtil.getAppPreference(context).getString(Constants.MQTT_CLIENT_ID, Constants.DEFAULT);
        if (srNo.equals(Constants.DEFAULT)) {
            srNo = MqttClient.generateClientId();
            SmartControllerUtil.getAppPreference(context).edit().putString(Constants.MQTT_CLIENT_ID, srNo).apply();
        }
        clientId = srNo;
    }

    private String getURI(boolean isPrimary) {
        MqttServerProperties serverProperties = serverPropertyDAO.getServerProperties();
        if (isPrimary) {
            server = serverProperties.getPrimaryServer();
        } else {
            server = serverProperties.getSecondaryServer();
        }
        int portNo = serverProperties.getPortNo();
        return "tcp://" + server + ":" + portNo;
    }

    private void reconnectToBroker(String uri, Context context) {
        try {
            if (sMqttConnection != null) {
                disconnect();
                connectToBroker(uri, context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publish(Message publishMessage, Context context) {

        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(publishMessage.getMessage().getBytes());
            mqttAndroidClient.publish(publishMessage.getTopic(), message);
            Log.d(TAG, "Message Published, Topic : " + publishMessage.getTopic() + "Message: " + message);
            /*if(!mqttAndroidClient.isConnected()){
                Log.d(TAG, mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
            }*/
        } catch (MqttException e) {
            Log.d(TAG, "Error Publishing: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.d(TAG, "Error Publishing: " + e.getMessage());
            if (mqttAndroidClient == null) {
                init(getURI(true), context);
            }
        }
    }

    public void unSubscribe(final String topic) {
        Log.d(TAG, "unSubscribing: " + topic);
        try {
            IMqttToken unsubToken = mqttAndroidClient.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscription could successfully be removed from the client
                    Log.d(TAG, "unSubscribe onSuccess: " + topic);
                    mTopics.remove(topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.d(TAG, "unSubscribe onFailure" + topic);
                    // some error occurred, this is very unlikely as even if the client
                    // did not had a subscription to the topic the unsubscribe action
                    // will be successfully
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        Log.d(TAG, "disconnect");
        sendDisconnectedConnectionListener(Constants.DISCONNECTING);
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            try {
                IMqttToken disconnectToken = mqttAndroidClient.disconnect();
                disconnectToken.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // we are now successfully disconnected
                        Log.d(TAG, "disconnect onSuccess");
                        sendDisconnectedConnectionListener(Constants.DISCONNECTED);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken,
                                          Throwable exception) {
                        Log.d(TAG, "disconnect onFailure");
                        sendDisconnectedConnectionListener("Disconnected with error");
                        // something went wrong, but probably we are disconnected anyway
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private String getClientId(Context context) {
        if (clientId == null) {
            setClientId(context);
        }
        return clientId;
    }

    public void reconnect(Context context) {
        reconnectToBroker(getURI(true), context);
    }

    public void sendMessage(Message message, Context context) {
        publish(message, context);
    }

    public void subscribeTopic(String topic) {
        subscribeToTopic(topic);
    }

    public boolean isConnected() {
        return mqttAndroidClient != null && mqttAndroidClient.isConnected();
    }

    private void sendResponseToConnectionListener(String response) {
        for (MqttConnectionListener connectionListener : connectionListeners) {
            connectionListener.onConnectionAttemptResponse(response);
        }
    }

    private void sendDisconnectedConnectionListener(String response) {
        for (MqttConnectionListener connectionListener : connectionListeners) {
            connectionListener.onDisconnectionAttempt(response);
        }
    }

    public void addConnectionListener(MqttConnectionListener toAdd) {
        connectionListeners.add(toAdd);
    }
}
