package com.example.alpha_test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionsmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void Profile(MenuItem item) {
        Intent i = new Intent(this, ChangeProfile.class);
        i.putExtra("type",2);
        Parcelable parcelable= Parcels.wrap(guard);
        i.putExtra("guard", parcelable);
        startActivity(i);
    }
}
