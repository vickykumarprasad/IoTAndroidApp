package com.urjalabs.smartcontroller.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Database helper
 * Created by tarun on 04-11-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    // switch table name
    static final String TABLE_LOAD = "switchLoads";
    static final String TABLE_DEVICE = "device";
    static final String TABLE_USER = "user";
    static final String TABLE_OTP = "otp";
    // Load Table Columns names
    static final String KEY_ID = "id";
    static final String KEY_NAME = "name";
    public static final String KEY_SWITCH_ID = "switch_id";
    static final String KEY_DEVICE_ID = "device_id";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_SCHEDULE = "schedule";
    static final String KEY_STATUS = "status";
    static final String KEY_TYPE = "type";
    static final String KEY_FLOOR_NO = "floor_no";
    static final String KEY_ROOM_NAME = "room_name";
    static final String KEY_DEVICE_NAME = "device_name";
     static final String KEY_TOPIC_NAME = "topic_name";
    //device table
    static final String DEVICE_KEY_ID = "id";
    static final String DEVICE_KEY_FLOOR_NO = "floor_no";
    static final String DEVICE_KEY_ROOM_NO = "room_no";
    static final String DEVICE_KEY_DEVICE_ID = "device_id";
    static final String DEVICE_KEY_DESCRIPTION = "device_desc";
    static final String DEVICE_KEY_NAME = "device_name";
    //User table
    static final String USER_KEY_ID="id";
    static final String USER_KEY_NAME="name";
    static final String USER_KEY_EMAIL="email";
    static final String USER_KEY_PHONE="phone";
    static final String USER_KEY_HOUSE_NO="house_no";
    static final String USER_KEY_STREET="street";
    static final String USER_KEY_LOCATION="location";
    static final String USER_KEY_CITY="city";
    static final String USER_KEY_STATE="state";
    static final String USER_KEY_COUNTRY="country";
    static final String USER_KEY_PASSWORD="password";
    //OTP table
    static final String OTP_KEY_EMAIL= "email";
    static final String OTP_KEY_OTP= "otp";
    static final String OTP_KEY_TIME= "time";



    // All Static variables
    // Logcat tag
    private static final String LOG = "DatabaseHelper";
    // Database Version
    private static final int DATABASE_VERSION = 3;
    // Database Name
    private static final String DATABASE_NAME = "smartControllerDB";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_LOAD_TABLE = "CREATE TABLE " + TABLE_LOAD + "("
                + KEY_ID + " INTEGER PRIMARY KEY autoincrement," + KEY_NAME + " TEXT,"
                + KEY_SWITCH_ID + " INTEGER not null unique," + KEY_DEVICE_ID + " TEXT,"
                + KEY_DESCRIPTION + " TEXT," + KEY_TOPIC_NAME + " TEXT not null unique,"+ KEY_SCHEDULE + " TEXT," + KEY_STATUS + " TEXT,"
                + KEY_TYPE + " TEXT," + KEY_FLOOR_NO + " INTEGER," + KEY_ROOM_NAME + " TEXT,"
                + KEY_DEVICE_NAME + " TEXT" + ")";
        String CREATE_DEVICE_TABLE = "CREATE TABLE " + TABLE_DEVICE + "("
                + DEVICE_KEY_ID + " INTEGER PRIMARY KEY autoincrement," + DEVICE_KEY_NAME + " TEXT,"
                + DEVICE_KEY_DEVICE_ID+ " TEXT not null unique,"
                + DEVICE_KEY_DESCRIPTION + " TEXT," + DEVICE_KEY_FLOOR_NO + " INTEGER,"
                + DEVICE_KEY_ROOM_NO + " INTEGER" + ")";
        String CREATE_USER_TABLE =  "CREATE TABLE "+ TABLE_USER + "("
                + USER_KEY_ID + " INTEGER PRIMARY KEY autoincrement," + USER_KEY_NAME + " TEXT,"
                + USER_KEY_EMAIL + " TEXT not null unique,"
                + USER_KEY_PHONE + " TEXT not null unique,"
                + USER_KEY_HOUSE_NO + " TEXT,"
                + USER_KEY_STREET + " TEXT,"
                + USER_KEY_LOCATION + " TEXT,"
                + USER_KEY_CITY + " TEXT,"
                + USER_KEY_STATE + " TEXT,"
                + USER_KEY_COUNTRY + " TEXT,"
                + USER_KEY_PASSWORD + " TEXT" + ")";
        String CREATE_OTP_TABLE = "CREATE TABLE " + TABLE_OTP + "(" + OTP_KEY_EMAIL + " TEXT PRIMARY KEY, "
                + OTP_KEY_OTP + " INTEGER, " + OTP_KEY_TIME + " TEXT)";

        sqLiteDatabase.execSQL(CREATE_LOAD_TABLE);
        sqLiteDatabase.execSQL(CREATE_DEVICE_TABLE);
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(CREATE_OTP_TABLE);
        Log.v(LOG,"database created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOAD);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_OTP);
        Log.v(LOG,"database updated successfully");
        // Create tables again
        onCreate(sqLiteDatabase);
    }
}
