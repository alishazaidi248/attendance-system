package com.example.gpsattendaceapp;
import android.app.AlertDialog;
import android.content.res.Configuration;
import java.util.Locale;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupMenu;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class homeActivity extends AppCompatActivity {

    TextView txtWelcome, txtLatitude, txtLongitude, txtStatus;

    Button btnLocation,btnMore,btnLanguage,btnProfile;
    Button btnAttendance;
    Button btnViewAttendance;

    FusedLocationProviderClient fusedLocationClient;

    FirebaseFirestore db;
    FirebaseAuth auth;

    SQLiteHelper helper;

    double latitude = 0.0;
    double longitude = 0.0;

    ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtWelcome = findViewById(R.id.txtWelcome);
        txtLatitude = findViewById(R.id.txtLatitude);
        txtLongitude = findViewById(R.id.txtLongitude);
        txtStatus = findViewById(R.id.txtStatus);
        btnMore = findViewById(R.id.btnMore);
        btnLocation = findViewById(R.id.btnLocation);
        btnAttendance = findViewById(R.id.btnAttendance);
        btnViewAttendance = findViewById(R.id.btnViewAttendance);
        btnLanguage = findViewById(R.id.btnLanguage);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        btnProfile = findViewById(R.id.btnProfile);

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });
        helper = new SQLiteHelper(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (auth.getCurrentUser() != null) {
            txtWelcome.setText(getString(R.string.welcome) + "\n" + auth.getCurrentUser().getEmail());        }

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {

                    if (isGranted) {
                        getLocation();
                    } else {
                        Toast.makeText(homeActivity.this,
                                "Location Permission Denied",
                                Toast.LENGTH_SHORT).show();
                    }

                });
        btnLanguage.setOnClickListener(v -> {

            String[] languages = {
                    "English",
                    "Hindi",
                    "Marathi",
                    "Tamil",
                    "Malayalam"
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose Language");

            builder.setItems(languages, (dialog, which) -> {

                String langCode = "en";

                switch (which) {
                    case 0: langCode = "en"; break;
                    case 1: langCode = "hi"; break;
                    case 2: langCode = "mr"; break;
                    case 3: langCode = "ta"; break;
                    case 4: langCode = "ml"; break;
                }

                setLocale(langCode);
            });

            builder.show();
        });
        btnLocation.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(
                    homeActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                getLocation();

            } else {

                permissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION);

            }

        });

        btnAttendance.setOnClickListener(v -> {

            if (latitude == 0 || longitude == 0) {

                Toast.makeText(homeActivity.this,
                        "Please get location first",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            String date = new SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()).format(new Date());

            String time = new SimpleDateFormat(
                    "hh:mm a",
                    Locale.getDefault()).format(new Date());

            HashMap<String, Object> attendance = new HashMap<>();

            attendance.put("uid", auth.getCurrentUser().getUid());
            attendance.put("email", auth.getCurrentUser().getEmail());            attendance.put("date", date);
            attendance.put("time", time);
            attendance.put("latitude", latitude);
            attendance.put("longitude", longitude);
            attendance.put("status", "Present");

            db.collection("Attendance")
                    .add(attendance)
                    .addOnSuccessListener(documentReference -> {

                        helper.insertAttendance(
                                auth.getCurrentUser().getEmail(),
                                date,
                                time,
                                String.valueOf(latitude),
                                String.valueOf(longitude)
                        );

                        txtStatus.setText("Attendance Status : Present");

                        Toast.makeText(
                                homeActivity.this,
                                "Attendance Marked Successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(
                                homeActivity.this,
                                e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();

                    });

        });

        btnViewAttendance.setOnClickListener(v -> {

            Intent intent = new Intent(
                    homeActivity.this,
                    attendanceActivity.class);

            startActivity(intent);

        });
        btnMore.setOnClickListener(v -> {

            PopupMenu popup = new PopupMenu(homeActivity.this, btnMore);

            popup.getMenu().add("About");
            popup.getMenu().add("Logout");
            popup.getMenu().add("Call");
            popup.getMenu().add("Email");
            popup.getMenu().add("Share");

            popup.setOnMenuItemClickListener(item -> {

                switch (item.getTitle().toString()) {

                    case "About":

                        Toast.makeText(homeActivity.this,
                                "GPS Attendance System",
                                Toast.LENGTH_SHORT).show();
                        break;

                    case "Logout":

                        new AlertDialog.Builder(homeActivity.this)
                                .setTitle("Logout")
                                .setMessage("Are you sure?")
                                .setPositiveButton("Yes", (dialog, which) -> {

                                    FirebaseAuth.getInstance().signOut();

                                    startActivity(new Intent(
                                            homeActivity.this,
                                            loginActivity.class));

                                    finish();

                                })
                                .setNegativeButton("No", null)
                                .show();

                        break;

                    case "Call":

                        startActivity(new Intent(Intent.ACTION_DIAL));

                        break;

                    case "Email":

                        Intent email = new Intent(Intent.ACTION_SEND);

                        email.setType("text/plain");

                        email.putExtra(Intent.EXTRA_EMAIL,
                                new String[]{"teacher@gmail.com"});

                        startActivity(email);

                        break;

                    case "Share":

                        Intent share = new Intent(Intent.ACTION_SEND);

                        share.setType("text/plain");

                        share.putExtra(Intent.EXTRA_TEXT,
                                "GPS Attendance App");

                        startActivity(Intent.createChooser(
                                share,
                                "Share"));

                        break;

                }

                return true;

            });

            popup.show();

        });

    }

    private void getLocation() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location != null) {

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        txtLatitude.setText(
                                "Latitude : " + latitude);

                        txtLongitude.setText(
                                "Longitude : " + longitude);

                    } else {

                        Toast.makeText(
                                homeActivity.this,
                                "Unable to get location.\nTurn ON GPS and try again.",
                                Toast.LENGTH_SHORT
                        ).show();

                    }

                });

    }
    private void setLocale(String langCode) {

        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        recreate(); // refresh activity
    }

}