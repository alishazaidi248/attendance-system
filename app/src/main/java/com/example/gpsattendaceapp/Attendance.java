package com.example.gpsattendaceapp;

import java.util.ArrayList;

public class Attendance {

    String date;
    String time;
    String latitude;
    String longitude;

    public Attendance() {
    }

    public Attendance(String date, String time, String latitude, String longitude) {
        this.date = date;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    ArrayList<Integer> attendanceIds;
}