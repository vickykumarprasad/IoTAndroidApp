package com.urjalabs.smartcontroller.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * smart controller utility class
 * Created by tarun on 29-10-2017.
 */

public final class SmartControllerUtil {

    /**
     * @param floorNo number of the floor
     * @return key for number of room on a floor
     */
    public static String getNumberOfRoomSharedPreferenceKey(int floorNo) {
        return Constants.FLOOR + floorNo + Constants.CHAR_SLASH + Constants.NUMBER_OF_ROOMS;
    }

    /**
     * @param homeName house name
     * @param floorNo  floor number
     * @param roomNo   room number
     * @return return key for room detail
     */
    public static String getRoomSharedPreferenceKey(String homeName, int floorNo, int roomNo) {
        return homeName + Constants.CHAR_SLASH + Constants.FLOOR + floorNo + Constants.CHAR_SLASH + Constants.ROOM + roomNo;
    }

    /**
     * @param floorNo floor number
     * @param roomNo  room number
     * @return key to get number of device in a room
     */
    public static String getNumberOfDeviceSharedPreferenceKey(int floorNo, int roomNo) {
        return Constants.FLOOR + floorNo + Constants.CHAR_SLASH + Constants.ROOM_NO + roomNo + Constants.CHAR_SLASH + Constants.NUMBER_OF_DEVICES;
    }

    /**
     * @param homeName home name
     * @param floorNo  floor number
     * @param roomNo   room number
     * @return key to get device detail
     */
    public static String getDeviceSharedPreferenceKey(String homeName, int floorNo, int roomNo, String deviceID) {
        return homeName + Constants.CHAR_SLASH + Constants.FLOOR + floorNo + Constants.CHAR_SLASH + Constants.ROOM + roomNo + Constants.CHAR_SLASH + deviceID;
    }

    /**
     * @param floorNo  floor number
     * @param roomNo   room number
     * @param deviceNo number of device
     * @return key to get number of loads on a device
     */
    public static String getNumberOfLoadsSharedPreferenceKey(int floorNo, int roomNo, int deviceNo) {
        return Constants.FLOOR + floorNo + Constants.CHAR_SLASH + Constants.ROOM_NO + roomNo + Constants.CHAR_SLASH + Constants.DEVICE + deviceNo + Constants.CHAR_SLASH + Constants.NUMBER_OF_LOADS;
    }

    /**
     * @param homeName home name
     * @param floorNo  floor number
     * @param roomNo   room number
     * @param deviceNo device number
     * @return key to get load info
     */
    public static String getLoadSharedPreferenceKey(String homeName, int floorNo, int roomNo, int deviceNo, int loadNo) {
        return homeName + Constants.CHAR_SLASH + Constants.FLOOR + floorNo + Constants.CHAR_SLASH + Constants.ROOM + roomNo + Constants.CHAR_SLASH + Constants.DEVICE + deviceNo + Constants.CHAR_SLASH + Constants.LOAD + loadNo;
    }

    /**
     * @param string string to be tested
     * @return false
     */
    public static boolean isEmpty(String string) {
        if (string != null && !string.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public static AlertDialog createAlertDialog(Activity activity, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return alertDialog;
    }

    /**
     * @param activity
     * @return true if internet connection is there
     */
    public static boolean isConnected(Context activity) {
        ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public static String getLoadDescriptionKey(String homeName, int floorNo, String roomName, String device, String loadName) {
        return homeName + Constants.CHAR_SLASH + Constants.FLOOR + floorNo + Constants.CHAR_SLASH + roomName + Constants.CHAR_SLASH +device + Constants.CHAR_SLASH + loadName;
    }

    public static SharedPreferences getAppPreference(Context context){
       return context.getSharedPreferences(Constants.MY_HOME_PRFRENCE, Context.MODE_PRIVATE);
    }
}
