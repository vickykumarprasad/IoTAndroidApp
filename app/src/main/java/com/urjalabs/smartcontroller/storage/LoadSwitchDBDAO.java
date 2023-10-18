package com.urjalabs.smartcontroller.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.urjalabs.smartcontroller.models.Load;

import java.util.ArrayList;
import java.util.List;

/**
 * load switch dao db implementation
 * Created by tarun on 03-11-2017.
 */

public class LoadSwitchDBDAO implements LoadSwitchDAO {
    private static final String TAG = " LoadSwitchDB ";
    private static final String WHERE = " WHERE ";
    private static final String EQUAL = " = ";
    private static final String SELECT_STAR = "Select * from ";
    private static final String INVERTED_COMMA = "\"";

    void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    private SQLiteDatabase database;
    LoadSwitchDBDAO(SQLiteDatabase database) {
        this.database = database;
    }

    @Override
    public int addLoad(Load load) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_NAME, load.getName());
        values.put(DatabaseHelper.KEY_SWITCH_ID, load.getSwitchId());
        values.put(DatabaseHelper.KEY_DEVICE_ID, load.getDeviceId());
        values.put(DatabaseHelper.KEY_DESCRIPTION, load.getDescription());
        values.put(DatabaseHelper.KEY_SCHEDULE, load.getSchedule());
        values.put(DatabaseHelper.KEY_STATUS, load.getStatus());
        values.put(DatabaseHelper.KEY_TYPE, load.getType());
        values.put(DatabaseHelper.KEY_FLOOR_NO, load.getFloorNo());
        values.put(DatabaseHelper.KEY_ROOM_NAME, load.getRoomName());
        values.put(DatabaseHelper.KEY_DEVICE_NAME, load.getDeviceName());
        values.put(DatabaseHelper.KEY_TOPIC_NAME, load.getTopicName());
       return  (int)database.insert(DatabaseHelper.TABLE_LOAD, null, values);
    }

    @Override
    public Load getLoad(int id) {
        return null;
    }

    @Override
    public List<Load> getLoadsByType(String type) {
        String selectQuery = SELECT_STAR + DatabaseHelper.TABLE_LOAD + WHERE
                + DatabaseHelper.KEY_TYPE + EQUAL + INVERTED_COMMA + type + INVERTED_COMMA;
        Log.v(TAG, selectQuery);
        List<Load> loadList = new ArrayList<>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Load load = new Load();
                load.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_ID)));
                load.setName((cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_NAME))));
                load.setSwitchId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_SWITCH_ID)));
                load.setDeviceId((cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_DEVICE_ID))));
                load.setDescription((cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_DESCRIPTION))));
                load.setSchedule((cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_SCHEDULE))));
                load.setStatus((cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_STATUS))));
                load.setType((cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_TYPE))));
                load.setFloorNo((cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_FLOOR_NO))));
                load.setRoomName((cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_ROOM_NAME))));
                load.setDeviceName((cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_DEVICE_NAME))));
                load.setTopicName((cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_TOPIC_NAME))));
                // adding to  list
                loadList.add(load);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return loadList;
    }

    @Override
    public List<Load> getLoadsByDeviceID(String deviceID) {
        String selectQuery = SELECT_STAR + DatabaseHelper.TABLE_LOAD + WHERE
                + DatabaseHelper.KEY_DEVICE_ID + EQUAL + INVERTED_COMMA + deviceID + INVERTED_COMMA;
        Log.v(TAG, selectQuery);
        List<Load> loadList = new ArrayList<>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Load load = new Load();
                load.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_ID)));
                load.setName((cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_NAME))));
                load.setSwitchId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_SWITCH_ID)));
                load.setDeviceId((cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_DEVICE_ID))));
                load.setDescription((cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_DESCRIPTION))));
                load.setSchedule((cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_SCHEDULE))));
                load.setStatus((cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_STATUS))));
                load.setType((cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_TYPE))));
                load.setFloorNo((cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_FLOOR_NO))));
                load.setRoomName((cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_ROOM_NAME))));
                load.setDeviceName((cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_DEVICE_NAME))));
                load.setTopicName((cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_TOPIC_NAME))));
                // adding to list
                loadList.add(load);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return loadList;
    }

    @Override
    public int getTotalLoadCount() {
        String selectQuery = "SELECT COUNT(*) FROM "
                + DatabaseHelper.TABLE_LOAD;
        Log.v(TAG, selectQuery);
        // return count
        return (int) DatabaseUtils.longForQuery(database, selectQuery, null);
    }

    @Override
    public int getLoadCountByType(String type) {
        String selectQuery = "SELECT COUNT(*) FROM " +
                DatabaseHelper.TABLE_LOAD + WHERE + DatabaseHelper.KEY_TYPE + EQUAL
                + INVERTED_COMMA + type + INVERTED_COMMA;
        Log.v(TAG, selectQuery);
        int count = (int) DatabaseUtils.longForQuery(database, selectQuery, null);
        Log.v(TAG, "Count " + count);
        return count;
    }

    @Override
    public int getLoadCountByDeviceID(String deviceId) {
        String selectQuery = "SELECT COUNT(*) FROM " +
                DatabaseHelper.TABLE_LOAD + WHERE + DatabaseHelper.KEY_DEVICE_ID + EQUAL
                + INVERTED_COMMA + deviceId + INVERTED_COMMA;
        Log.v(TAG, selectQuery);
        int count = (int) DatabaseUtils.longForQuery(database, selectQuery, null);
        Log.v(TAG, "Count by device id" + count);
        return count;
    }

    @Override
    public int updateLoad(Load load) {
        return 0;
    }

    @Override
    public boolean deleteLoad(Load load) {
        long id = load.getId();
        Log.v(TAG, "Deleting load with id: " + id);
        return database.delete(DatabaseHelper.TABLE_LOAD, DatabaseHelper.KEY_ID
                + " = " + id, null) > 0;
    }

    @Override
    public int deleteLoadByDeviceID(String deviceId) {
        return database.delete(DatabaseHelper.TABLE_LOAD,
                DatabaseHelper.KEY_DEVICE_ID + " = " + INVERTED_COMMA+deviceId+INVERTED_COMMA, null);
    }

    @Override
    public boolean isValueExistInLoad(String columnName, String columnValue) {
        String Query = SELECT_STAR + DatabaseHelper.TABLE_LOAD + WHERE + columnName + EQUAL
                + INVERTED_COMMA + columnValue + INVERTED_COMMA;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    @Override
    public List<String> getAllTopics() {
        String selectQuery = SELECT_STAR + DatabaseHelper.TABLE_LOAD;
        Log.v(TAG, selectQuery);
        List<String> topics = new ArrayList<>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                // adding to  list
                topics.add((cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_TOPIC_NAME))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return topics;
    }
}
