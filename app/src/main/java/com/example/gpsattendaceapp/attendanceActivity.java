package com.example.gpsattendaceapp;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class attendanceActivity extends AppCompatActivity {

    ListView listAttendance;

    SQLiteHelper helper;

    ArrayList<String> attendanceList;
    ArrayList<Integer> attendanceIds;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        listAttendance = findViewById(R.id.listAttendance);

        helper = new SQLiteHelper(this);

        attendanceList = new ArrayList<>();
        attendanceIds = new ArrayList<>();

        loadAttendance();
    }

    private void loadAttendance() {

        attendanceList.clear();
        attendanceIds.clear();

        Cursor cursor = helper.getAttendance();
        if(cursor.getCount()==0){

            Toast.makeText(this,
                    "No Attendance Found",
                    Toast.LENGTH_SHORT).show();

        }else{

            while(cursor.moveToNext()){

                attendanceIds.add(cursor.getInt(0));

                String record =
                        "ID : " + cursor.getInt(0) +
                                "\nEmail : " + cursor.getString(1) +
                                "\nDate : " + cursor.getString(2) +
                                "\nTime : " + cursor.getString(3) +
                                "\nLatitude : " + cursor.getString(4) +
                                "\nLongitude : " + cursor.getString(5);

                attendanceList.add(record);

            }

        }

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                attendanceList
        );

        listAttendance.setAdapter(adapter);

        listAttendance.setOnItemLongClickListener((parent, view, position, id) -> {

            android.widget.PopupMenu popup =
                    new android.widget.PopupMenu(this, view);

            popup.getMenu().add("Update");
            popup.getMenu().add("Delete");
            popup.setOnMenuItemClickListener(item -> {

                if(item.getTitle().equals("Update")){

                    String newTime = new java.text.SimpleDateFormat(
                            "hh:mm a",
                            java.util.Locale.getDefault()
                    ).format(new java.util.Date());

                    helper.updateAttendanceTime(
                            attendanceIds.get(position),
                            newTime
                    );

                    loadAttendance();

                    Toast.makeText(
                            this,
                            "Attendance Updated",
                            Toast.LENGTH_SHORT
                    ).show();

                }

                if(item.getTitle().equals("Delete")){

                    new androidx.appcompat.app.AlertDialog.Builder(this)
                            .setTitle("Delete Attendance")
                            .setMessage("Delete this attendance record?")
                            .setPositiveButton("Yes", (dialog, which) -> {

                                helper.deleteAttendance(
                                        attendanceIds.get(position)
                                );

                                loadAttendance();

                                Toast.makeText(
                                        this,
                                        "Deleted Successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                            })
                            .setNegativeButton("No", null)
                            .show();

                }

                return true;

            });

            popup.show();

            return true;

        });

        cursor.close();
    }
}