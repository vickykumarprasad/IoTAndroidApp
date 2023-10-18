package com.urjalabs.smartcontroller.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.urjalabs.smartcontroller.models.Device;

import java.util.ArrayList;
import java.util.List;

/**
 * load switch dao db implementation
 * Created by tarun on 03-11-2017.
 */

public class DeviceDBDAO implements DeviceDAO {
    private static final String TAG = " LoadSwitchDB ";
    private static final String WHERE = " WHERE ";
    private static final String EQUAL = " = ";
    private static final String SELECT_STAR = "Select * from ";
    private static final String INVERTED_COMMA = "\"";
    private SQLiteDatabase database;

    DeviceDBDAO(SQLiteDatabase database) {
        this.database = database;
    }

    void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    @Override
    public int createDevice(Device device) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.DEVICE_KEY_NAME, device.getName());
        values.put(DatabaseHelper.DEVICE_KEY_DEVICE_ID, device.getDeviceId());
        values.put(DatabaseHelper.DEVICE_KEY_DESCRIPTION, device.getDescription());
        values.put(DatabaseHelper.DEVICE_KEY_FLOOR_NO, device.getFloorNo());
        values.put(DatabaseHelper.DEVICE_KEY_ROOM_NO, device.getRoomNo());
        Log.v(TAG, "Inserted record in device db");
        return (int)database.insert(DatabaseHelper.TABLE_DEVICE, null, values);
    }

    @Override
    public Device getDevice(int id) {
        return null;
    }

    @Override
    public boolean deleteDevice(Device device) {
        long id = device.getId();
        Log.v(TAG, "Deleting device with id: " + id);
        return database.delete(DatabaseHelper.TABLE_DEVICE, DatabaseHelper.DEVICE_KEY_ID
                + " = " + id, null) > 0;
    }

    @Override
    public List<Device> getDevicesInRoom(int floorNo, int roomNo) {
        String selectQuery = SELECT_STAR + DatabaseHelper.TABLE_DEVICE + WHERE
                + DatabaseHelper.DEVICE_KEY_FLOOR_NO + EQUAL + INVERTED_COMMA + floorNo + INVERTED_COMMA + " AND "
                + DatabaseHelper.DEVICE_KEY_ROOM_NO + EQUAL + INVERTED_COMMA + roomNo + INVERTED_COMMA;
        Log.v(TAG, selectQuery);
        List<Device> deviceList = new ArrayList<>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Device device = new Device();
                device.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DEVICE_KEY_ID)));
                device.setDeviceId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_KEY_DEVICE_ID)));
                device.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_KEY_NAME)));
                device.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_KEY_DESCRIPTION)));
                device.setFloorNo(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DEVICE_KEY_FLOOR_NO)));
                device.setRoomNo(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DEVICE_KEY_ROOM_NO)));
                deviceList.add(device);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return deviceList;
    }
}
