package com.example.alpha_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TeacherLogin extends AppCompatActivity {
    String school,phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);
        Toast.makeText(getApplicationContext(),"Success is coming, Teacher",Toast.LENGTH_SHORT).show();
        Intent gi=getIntent();
        school=gi.getStringExtra("n");
        phone=gi.getStringExtra("nn");
    }

    public void PassAc(View view) {
        Intent i=new Intent(this,Teacher_Manage.class);
        i.putExtra("n", school);
        i.putExtra("nn",phone);
        startActivity(i);
    }
}
