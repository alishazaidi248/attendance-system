package com.example.gpsattendaceapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    TextView txtEmail;
    EditText etName, etStudentID;
    Button btnUpdate, btnDelete;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtEmail = findViewById(R.id.txtEmail);
        etName = findViewById(R.id.etName);
        etStudentID = findViewById(R.id.etStudentID);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        String uid = auth.getCurrentUser().getUid();
        String email = auth.getCurrentUser().getEmail();

        txtEmail.setText("Email: " + email);

        // 🔥 FIREBASE READ
        db.collection("Students").document(uid)
                .get()
                .addOnSuccessListener(document -> {

                    if (document.exists()) {
                        etName.setText(document.getString("name"));
                        etStudentID.setText(document.getString("studentID"));
                    }

                });

        // 🔥 UPDATE PROFILE
        btnUpdate.setOnClickListener(v -> {

            String name = etName.getText().toString().trim();
            String studentID = etStudentID.getText().toString().trim();

            if (name.isEmpty() || studentID.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("studentID", studentID);

            db.collection("Students").document(uid)
                    .update(map)
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(this,
                                "Profile Updated Successfully",
                                Toast.LENGTH_SHORT).show();

                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(this,
                                "Update Failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();

                    });

        });

        btnDelete.setOnClickListener(v -> {

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage("This action cannot be undone")

                    .setPositiveButton("Yes", (dialog, which) -> {

                        db.collection("Students").document(uid)
                                .delete()
                                .addOnSuccessListener(unused -> {

                                    auth.getCurrentUser().delete()
                                            .addOnSuccessListener(aVoid -> {

                                                Toast.makeText(this,
                                                        "Account Deleted",
                                                        Toast.LENGTH_SHORT).show();

                                                startActivity(new Intent(this, loginActivity.class));
                                                finish();

                                            });

                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this,
                                                "Delete Failed: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show()
                                );

                    })

                    .setNegativeButton("No", null)
                    .show();

        });

    }
}