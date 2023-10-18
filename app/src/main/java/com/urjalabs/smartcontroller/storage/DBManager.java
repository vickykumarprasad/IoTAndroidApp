package com.urjalabs.smartcontroller.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**azdfgc
 * Created by tarun on 07-11-2017.
 */

public class DBManager {
    // Database fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private LoadSwitchDBDAO loadSwitchDBDAO;
    private UserDBDAO userDBDAO;
    private OTPDBDAO otpdbdao;

    public DeviceDBDAO getDeviceDBDAO() {
        return deviceDBDAO;
    }

    private DeviceDBDAO deviceDBDAO;
    public DBManager(Context context) {

        dbHelper = new DatabaseHelper(context);
        loadSwitchDBDAO = new LoadSwitchDBDAO(database);
        deviceDBDAO = new DeviceDBDAO(database);
        userDBDAO = new UserDBDAO(database);
        otpdbdao = new OTPDBDAO(database);
    }

    public LoadSwitchDBDAO getLoadSwitchDBDAO() {
        return loadSwitchDBDAO;
    }

    public UserDBDAO getUserDBDAO() {
        return userDBDAO;
    }

    public OTPDBDAO getOtpdbdao() {
        return otpdbdao;
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
        loadSwitchDBDAO.setDatabase(database);
        deviceDBDAO.setDatabase(database);
        userDBDAO.setDatabase(database);
        otpdbdao.setDatabase(database);
    }

    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}
