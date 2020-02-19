package com.example.alpha_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminLogin extends AppCompatActivity {
    String school;
    String phone;

    Button man;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        Toast.makeText(getApplicationContext(),"Success is coming, Admin",Toast.LENGTH_SHORT).show();


        man=findViewById(R.id.manage_account);
        Intent gi=getIntent();
        school=gi.getStringExtra("n");
        phone=gi.getStringExtra("nn");
    }

    public void ManageAccounts(View view) {
        Intent i=new Intent(this,ManageAcc.class);
        i.putExtra("n", school);
        i.putExtra("nn",phone);
        startActivity(i);
    }

    public void WatchData(View view) {
        Intent i=new Intent(this,DataView.class);
        i.putExtra("n", school);
        i.putExtra("nn",phone);
        startActivity(i);

    }
}
