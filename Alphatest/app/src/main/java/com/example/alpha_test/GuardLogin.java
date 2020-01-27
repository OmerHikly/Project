package com.example.alpha_test;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GuardLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard_login);
        Toast.makeText(getApplicationContext(),"Success is coming, Guard",Toast.LENGTH_SHORT).show();

    }
}
