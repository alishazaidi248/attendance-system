package com.example.gpsattendaceapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class loginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView txtRegister;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(view -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(loginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            Toast.makeText(loginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(loginActivity.this, homeActivity.class));
                            finish();

                        } else {

                            Toast.makeText(loginActivity.this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }

                    });

        });

        txtRegister.setOnClickListener(view -> {
            startActivity(new Intent(loginActivity.this, registerActivity.class));
        });

    }
}