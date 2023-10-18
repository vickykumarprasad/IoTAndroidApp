package com.urjalabs.smartcontroller.storage;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.urjalabs.smartcontroller.models.OTP;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OTPDBDAO implements OTPDAO{
    private SQLiteDatabase database;
    private static final String WHERE = " WHERE ";
    private static final String EQUAL = " = ";
    private static final String SELECT_STAR = "Select * from ";
    private static final String INVERTED_COMMA = "\"";

    public OTPDBDAO(SQLiteDatabase database) {
        this.database = database;
    }
    void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }
    @Override
    public int addOTP(OTP otp) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.OTP_KEY_EMAIL, otp.getEmail());
        contentValues.put(DatabaseHelper.OTP_KEY_OTP, otp.getOtp());
        contentValues.put(DatabaseHelper.OTP_KEY_TIME, getDateTime());
        return (int) database.insert(DatabaseHelper.TABLE_OTP,null,contentValues);
    }

    @SuppressLint("Range")
    @Override
    public boolean verifyOTP(int otp, String email) {
        String selectQuery = SELECT_STAR + DatabaseHelper.TABLE_OTP + WHERE
                + DatabaseHelper.USER_KEY_EMAIL + EQUAL + INVERTED_COMMA + email + INVERTED_COMMA;
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            String emailFromTable =  cursor.getString(cursor.getColumnIndex(DatabaseHelper.OTP_KEY_EMAIL));
            int otpFromTable = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.OTP_KEY_OTP));
            if (otp == otpFromTable){
                cursor.close();
                return true;
            }


        }
        cursor.close();
        return false;
    }

    @Override
    public void clean() {
        String sql = "DELETE FROM " + DatabaseHelper.TABLE_OTP + " WHERE " + DatabaseHelper.OTP_KEY_TIME + " <= date('now','-10 minute')";
        database.execSQL(sql);
    }
    public void deleteOtp(String email) {
        String sql = "DELETE FROM " + DatabaseHelper.TABLE_OTP + " WHERE " + DatabaseHelper.OTP_KEY_EMAIL + " ='"+email+"'";
        database.execSQL(sql);
    }
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @SuppressLint("Range")
    public int checkEmail (String email){
        String selectQuery = SELECT_STAR + DatabaseHelper.TABLE_OTP + WHERE
                + DatabaseHelper.USER_KEY_EMAIL + EQUAL + INVERTED_COMMA + email + INVERTED_COMMA;
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            String emailFromTable =  cursor.getString(cursor.getColumnIndex(DatabaseHelper.OTP_KEY_EMAIL));
            if (emailFromTable!=null){
                int otp = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.OTP_KEY_OTP));
                cursor.close();
                return otp;
            }


        }
        cursor.close();
        return -1;
    }
}
