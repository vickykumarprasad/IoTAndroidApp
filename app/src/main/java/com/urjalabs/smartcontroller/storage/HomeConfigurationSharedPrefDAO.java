package com.urjalabs.smartcontroller.storage;

import android.content.SharedPreferences;

import com.urjalabs.smartcontroller.models.Device;
import com.urjalabs.smartcontroller.models.Room;
import com.urjalabs.smartcontroller.util.Constants;
import com.urjalabs.smartcontroller.util.SmartControllerUtil;

/**
 * DAO implementation for shared preference
 * Created by tarun on 02-11-2017.
 */

public class HomeConfigurationSharedPrefDAO implements HomeConfigurationDAO {
    private final SharedPreferences mSharedPreferences;

    public HomeConfigurationSharedPrefDAO(SharedPreferences sharedPreferences) {
        this.mSharedPreferences = sharedPreferences;
    }

    @Override
    public void saveOrUpdateRoomConfig(Room room) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(SmartControllerUtil.getRoomSharedPreferenceKey(getHomeName(), room.getFloorNo(), room.getRoomNumber()), room.getName() + Constants.CHAR_SPLIT + room.getType());
        editor.apply();
    }

    @Override
    public Room getRoomConfig(int floorNo, int roomNo) {
        String roomInfo = mSharedPreferences.getString(SmartControllerUtil.getRoomSharedPreferenceKey(getHomeName(), floorNo, roomNo), Constants.DEFAULT);
        Room room = new Room();
        if (!roomInfo.equals(Constants.DEFAULT)) {
            String roomInf[] = roomInfo.split(Constants.CHAR_SPLIT);
            room.setFloorNo(floorNo);
            room.setRoomNumber(roomNo);
            room.setName(roomInf[0]);
            room.setType(roomInf[1]);
        }
        return room;
    }

    @Override
    public int getNumberOfRooms(int floorNo) {
        return mSharedPreferences.getInt(SmartControllerUtil.getNumberOfRoomSharedPreferenceKey(floorNo), -1);
    }

    @Override
    public void saveOrUpdateNumberOfRooms(int floorNo, int numberOfRoom) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(SmartControllerUtil.getNumberOfRoomSharedPreferenceKey(floorNo), numberOfRoom);
        editor.apply();
    }

    @Override
    public void saveOrUpdateDeviceConfig(Device device) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String deviceDescriptionKey = SmartControllerUtil.getDeviceSharedPreferenceKey(getHomeName(), device.getFloorNo(), device.getRoomNo(), device.getDeviceId());
        editor.putString(deviceDescriptionKey, device.getName() + Constants.CHAR_SPLIT + device.getDeviceId());
        editor.apply();
    }

    @Override
    public Device getDeviceConfig(int floorNo, int roomNo, int deviceNo) {
        String deviceDescriptionKey = SmartControllerUtil.getDeviceSharedPreferenceKey(getHomeName(), floorNo, roomNo, deviceNo+"");
        String deviceInfo = mSharedPreferences.getString(deviceDescriptionKey, Constants.DEFAULT);
        Device device = new Device();
        if (!deviceInfo.equals(Constants.DEFAULT)) {
            String deviceInf[] = deviceInfo.split(Constants.CHAR_SPLIT);
            device.setFloorNo(floorNo);
            device.setRoomNo(roomNo);
            device.setId(deviceNo);
            device.setDescription(deviceDescriptionKey);
            device.setName(deviceInf[0]);
            device.setDeviceId(deviceInf[1]);
        }
        return device;
    }

    @Override
    public void deleteDevice(Device device) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String deviceDescriptionKey = SmartControllerUtil.getDeviceSharedPreferenceKey(getHomeName(), device.getFloorNo(), device.getRoomNo(), device.getDeviceId());
        editor.remove(deviceDescriptionKey);
        editor.apply();
    }

    @Override
    public int getNumberOfDevice(int floorNo, int roomNo) {
        return mSharedPreferences.getInt(SmartControllerUtil.getNumberOfDeviceSharedPreferenceKey(floorNo, roomNo), -1);
    }

    @Override
    public void saveOrUpdateNumberOfDevices(int floorNo, int roomNo, int numberOfDevices) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(SmartControllerUtil.getNumberOfDeviceSharedPreferenceKey(floorNo, roomNo), numberOfDevices);
        editor.apply();
    }

    @Override
    public String getHomeName() {
        return mSharedPreferences.getString(Constants.HOME_NAME, Constants.DEFAULT);
    }

    @Override
    public void saveOrUpdateHomeName(String homeName) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.HOME_NAME, homeName);
        editor.apply();
    }

    @Override
    public int getNumberOfFloor() {
        return mSharedPreferences.getInt(Constants.NUMBER_OF_FLOOR, -1);
    }

    @Override
    public void saveOrUpdateNumberOfFloor(int numberOfFloor) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(Constants.NUMBER_OF_FLOOR, numberOfFloor);
        editor.apply();
    }
}
