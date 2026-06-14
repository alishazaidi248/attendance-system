package com.example.gpsattendaceapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class registerActivity extends AppCompatActivity {

    EditText etName, etStudentID, etEmail, etPassword;
    Button btnRegister;
    TextView txtLogin;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etStudentID = findViewById(R.id.etStudentID);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(view -> {

            String name = etName.getText().toString().trim();
            String studentID = etStudentID.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || studentID.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(registerActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            HashMap<String, Object> user = new HashMap<>();
                            user.put("name", name);
                            user.put("studentID", studentID);
                            user.put("email", email);

                            String uid = mAuth.getCurrentUser().getUid();


                            db.collection("Students")
                                    .document(uid)
                                    .set(user);
                            Toast.makeText(registerActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(registerActivity.this, loginActivity.class));
                            finish();

                        } else {

                            Toast.makeText(registerActivity.this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }

                    });

        });

        txtLogin.setOnClickListener(view -> {
            startActivity(new Intent(registerActivity.this, loginActivity.class));
            finish();
        });

    }
}