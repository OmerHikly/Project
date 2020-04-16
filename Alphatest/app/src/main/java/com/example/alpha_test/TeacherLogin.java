package com.example.alpha_test;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.parceler.Parcels;

public class TeacherLogin extends AppCompatActivity {
    TextView stv;
    Toolbar toolbar;

    String school,phone,cls;
    Teacher teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);
        toolbar=findViewById(R.id.tb);
        stv=findViewById(R.id.stv);
        Parcelable parcelable=getIntent().getParcelableExtra("teacher");
        teacher= Parcels.unwrap(parcelable);

        school=teacher.getSchool();
        phone=teacher.getPhone();
        cls=teacher.getCls();

        toolbar.setTitle("ByeCode");

        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_menu_black_24dp);
        toolbar.setOverflowIcon(drawable);
        setSupportActionBar(toolbar);


        stv.setText("שלום"+" "+teacher.getName()+"!");
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

    public void OpenRequests(View view) {
        Intent i=new Intent(this,Requests.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);
    }
}
