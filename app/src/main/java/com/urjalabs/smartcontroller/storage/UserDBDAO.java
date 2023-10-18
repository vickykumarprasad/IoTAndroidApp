package com.urjalabs.smartcontroller.storage;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.urjalabs.smartcontroller.models.UserCredential;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserDBDAO implements UserDAO {
    private SQLiteDatabase database;
    private static final String WHERE = " WHERE ";
    private static final String EQUAL = " = ";
    private static final String SELECT_STAR = "Select * from ";
    private static final String INVERTED_COMMA = "\"";

    public UserDBDAO(SQLiteDatabase database) {
        this.database = database;
    }
    void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    @Override
    public int addUser(UserCredential userCredential) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.USER_KEY_NAME, userCredential.getName());
        values.put(DatabaseHelper.USER_KEY_EMAIL, userCredential.getEmail());
        values.put(DatabaseHelper.USER_KEY_PHONE, userCredential.getPhone());
        values.put(DatabaseHelper.USER_KEY_HOUSE_NO, userCredential.getHouse_no());
        values.put(DatabaseHelper.USER_KEY_STREET, userCredential.getStreet());
        values.put(DatabaseHelper.USER_KEY_STATE, userCredential.getState());
        values.put(DatabaseHelper.USER_KEY_COUNTRY, userCredential.getCountry());
        values.put(DatabaseHelper.USER_KEY_PASSWORD, get_SHA_256_SecurePassword(userCredential.getPassword()));
        values.put(DatabaseHelper.USER_KEY_LOCATION, userCredential.getLocation());
        values.put(DatabaseHelper.USER_KEY_CITY, userCredential.getCity());
        return (int) database.insert(DatabaseHelper.TABLE_USER,null,values);
    }

    @SuppressLint("Range")
    @Override
    public UserCredential getUser(String email) {
        String selectQuery = SELECT_STAR + DatabaseHelper.TABLE_USER + WHERE
                + DatabaseHelper.USER_KEY_EMAIL + EQUAL + INVERTED_COMMA + email + INVERTED_COMMA;
        UserCredential userCredential = new UserCredential();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            userCredential.setName((cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_KEY_NAME))));
            userCredential.setEmail((cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_KEY_EMAIL))));
            userCredential.setPhone((cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_KEY_PHONE))));
            userCredential.setHouse_no((cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_KEY_HOUSE_NO))));
            userCredential.setStreet((cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_KEY_STREET))));
            userCredential.setLocation((cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_KEY_LOCATION))));
            userCredential.setCity((cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_KEY_CITY))));
            userCredential.setState((cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_KEY_STATE))));
            userCredential.setCountry((cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_KEY_COUNTRY))));
            userCredential.setPassword((cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_KEY_PASSWORD))));
        }
        cursor.close();

        return userCredential;
    }
    public static String get_SHA_256_SecurePassword(String passwordToHash
                                                     ) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update("Secure".getBytes());
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16)
                        .substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public int updatePassword(String email, String password){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.USER_KEY_PASSWORD, get_SHA_256_SecurePassword(password));
        return database.update(DatabaseHelper.TABLE_USER,contentValues,DatabaseHelper.USER_KEY_EMAIL + " = '" +email+"'",null);

    }
}
