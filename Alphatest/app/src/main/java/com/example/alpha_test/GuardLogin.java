package com.example.alpha_test;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import org.parceler.Parcels;

public class GuardLogin extends AppCompatActivity {
    Guard guard;
    String school,phone;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard_login);
        toolbar=findViewById(R.id.tb);

        Parcelable parcelable=getIntent().getParcelableExtra("guard");
        guard= Parcels.unwrap(parcelable);

        school=guard.getSchool();
        phone=guard.getPhone();

        toolbar.setTitle("ByeCode");
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_menu_black_24dp);
        toolbar.setOverflowIcon(drawable);
        setSupportActionBar(toolbar);
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

    public void BarcodeScan(View view) {
        Intent i=new Intent(this,BarcodeScan.class);
        Parcelable parcelable= Parcels.wrap(guard);
        i.putExtra("guard", parcelable);
        startActivity(i);
    }

    public void Monitor(View view) {
        Intent i=new Intent(this,MonitorScreen.class);
        Parcelable parcelable= Parcels.wrap(guard);
        i.putExtra("school", guard.getSchool());
        startActivity(i);
    }
}
