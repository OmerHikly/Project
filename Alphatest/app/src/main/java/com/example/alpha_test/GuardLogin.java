package com.example.alpha_test;

import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.parceler.Parcels;

public class GuardLogin extends AppCompatActivity {
    Guard guard;
    String school,phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard_login);
        Toast.makeText(getApplicationContext(),"Success is coming, Guard",Toast.LENGTH_SHORT).show();


        Parcelable parcelable=getIntent().getParcelableExtra("guard");
        guard= Parcels.unwrap(parcelable);

        school=guard.getSchool();
        phone=guard.getPhone();
    }
}
