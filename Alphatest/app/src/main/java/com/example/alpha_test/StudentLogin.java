package com.example.alpha_test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.parceler.Parcels;

public class StudentLogin extends AppCompatActivity {
    Student student;

    String school,phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        Parcelable parcelable=getIntent().getParcelableExtra("student");
        student= Parcels.unwrap(parcelable);

        school=student.getSchool();
        phone=student.getPhone();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionsmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void Profile(MenuItem item) {
        Intent i = new Intent(this, ChangeProfile.class);
        i.putExtra("type",0);
        Parcelable parcelable= Parcels.wrap(student);
        i.putExtra("student", parcelable);
        startActivity(i);
    }

    public void AskPermition(View view) {
        Intent i = new Intent(this, AskExit.class);
        Parcelable parcelable= Parcels.wrap(student);
        i.putExtra("student", parcelable);
        startActivity(i);
    }

    public void ViewPermition(View view) {
        Intent i = new Intent(this, PermitionsRecived.class);
        Parcelable parcelable= Parcels.wrap(student);
        i.putExtra("student", parcelable);
        startActivity(i);

    }
}
