package com.example.alpha_test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.parceler.Parcels;

public class TeacherLogin extends AppCompatActivity {
    String school,phone,cls;
    Teacher teacher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);
        Toast.makeText(getApplicationContext(),"Success is coming, Teacher",Toast.LENGTH_SHORT).show();

        Parcelable parcelable=getIntent().getParcelableExtra("teacher");
        teacher= Parcels.unwrap(parcelable);

        school=teacher.getSchool();
        phone=teacher.getPhone();
        cls=teacher.getCls();
    }

    public void PassAc(View view) {
        Intent i=new Intent(this,Groups.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);
    }

    public void ExitActivity(View view) {
        Intent i=new Intent(this,Send_Permition.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionsmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void Profile(MenuItem item) {
        Intent i = new Intent(this, ChangeProfile.class);
        i.putExtra("type",1);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);
    }
}
