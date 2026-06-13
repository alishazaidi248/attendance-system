package com.example.gpsattendaceapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(Context context) {
        super(context, "Attendance.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE Attendance(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "email TEXT," +
                "date TEXT," +
                "time TEXT," +
                "latitude TEXT," +
                "longitude TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS Attendance");

        onCreate(db);

    }

    public void insertAttendance(String email,
                                 String date,
                                 String time,
                                 String latitude,
                                 String longitude){

        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();

        values.put("email",email);
        values.put("date",date);
        values.put("time",time);
        values.put("latitude",latitude);
        values.put("longitude",longitude);

        db.insert("Attendance",null,values);

    }

    public Cursor getAttendance(){

        SQLiteDatabase db=this.getReadableDatabase();

        return db.rawQuery("SELECT * FROM Attendance",null);

    }


    public void deleteAttendance(int id){

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(
                "Attendance",
                "id=?",
                new String[]{String.valueOf(id)}
        );

    }
    public void updateAttendanceTime(int id, String newTime){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("time", newTime);

        db.update(
                "Attendance",
                values,
                "id=?",
                new String[]{String.valueOf(id)}
        );

    }



}