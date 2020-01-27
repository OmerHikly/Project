package com.example.alpha_test;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StudentLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        Toast.makeText(getApplicationContext(),"Success is coming, Student",Toast.LENGTH_SHORT).show();

    }
}
