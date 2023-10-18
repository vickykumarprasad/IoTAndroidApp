package com.urjalabs.smartcontroller.util;

/**
 * constant class having constant related to app
 * Created by tarun on 24-10-2017.
 */

public final class Constants {
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    public static final String[] DUMMY_CREDENTIALS = new String[]{
            "demo@urjalabs.com:admin", "9999999999:admin"
    };
    public static final String HTTP="http";
    public static final String COLON =":";
    public static final String HOST_NAME="www.bijlicontrol4u.com";
    public static final String SWITCH_ON="Y";
    public static final String SWITCH_OFF="N";
    public static final String MY_HOME_PRFRENCE="com.urjalabs.smartcontroller.home";
    public static final String NUMBER_OF_FLOOR="NUMBER_OF_FLOOR";
    public static final String NUMBER_OF_ROOMS="NUMBER_OF_ROOMS";
    public static final String NUMBER_OF_DEVICES="NUMBER_OF_DEVICES";
    public static final String NUMBER_OF_LOADS="NUMBER_OF_LOADS";
    public static final String TOTAL_NUMBER_OF_LOADS="NUMBER_OF_LOADS";
    public static final String FLOOR_NO="FLOOR_NO";
    public static final String ROOM_NO="ROOM_NO";
    public static final String DEVICE_NO="DEVICE_NO";
    public static final String LOAD_NO="LOAD_NO";
    public static final String HOME_NAME="HOME_NAME";
    public static final String FLOOR="Floor";
    public static final String ROOM="Room";
    public static final String CHAR_SPLIT=",";
    public static final String CHAR_SLASH="/";
    public static final String DEVICE="Device";;
    public static final String LOAD="load";
    public static final String LOAD_TYPE="LOAD_TYPE";
    public static final String DEVICE_ID="deviceId";
    public static final String IS_CALLED_DASH="isFromDash";
    public static final String DEFAULT="default";
    public static final String HOME_LABEL="House Name: ";
    public static final String SELECT_FLOOR="Select Floor";
    public static final String SELECT_ROOM="Select Room";
    public static final String SELECT_DEVICE="Select Device";

    //urls
    public  static final String PATH_ADD_DEVICE="urjalabs/device/v1/savedevice";
    public  static final String PATH_DEL_DEVICE="urjalabs/device/v1/deletedevice";
    public  static final String PATH_LOGIN="urjalabs/login";
    public  static final String PATH_ADD_LOAD="urjalabs/switch/v1/saveswitch";
    public  static final String PATH_DEL_LOAD="urjalabs/switch/v1/deleteswitch";
    //response
    public  static final String STATUS_SUCCESS="SUCCESS";
    public  static final int STATUS_CODE_SUCCESS=200;
    //settings
    public static final String MQTT_CLIENT_ID="mqttClientID";
    public static final String MQTT_SERVER_PRIMARY="mqttPrimaryServer";
    public static final String MQTT_SERVER_SECONDARY="mqttSecondaryServer";
    public static final String MQTT_PORT="mqttPort";
    public static final String MQTT_IS_CRED_REQ="mqttCredReq";
    public static final String MQTT_USER="mqttUser";
    public static final String MQTT_PASSWORD="mqttPassword";
    public static final String MQTT_SSL_ENABLED="mqttSSLEnabled";
    public static final String CONNECTED="Connected";
    public static final String CONNECTING="Connecting";
    public static final String DISCONNECTED="Disconnected";
    public static final String DISCONNECTING="Disconnecting";
    public static  final  String SEPARATOR="SEPARATOR";
}
