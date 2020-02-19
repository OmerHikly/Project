package com.example.alpha_test;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Teacher_Manage extends AppCompatActivity {
String school,phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher__manage);
        Intent gi=getIntent();
        school=gi.getStringExtra("n");
        phone=gi.getStringExtra("nn");
    }


}
