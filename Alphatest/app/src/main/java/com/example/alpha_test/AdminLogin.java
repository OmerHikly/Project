package com.example.alpha_test;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        Toast.makeText(getApplicationContext(),"Success is coming, Admin",Toast.LENGTH_SHORT).show();

    }
}
