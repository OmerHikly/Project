package com.example.alpha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Upload_Images extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload__images);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public void AuthScreen(MenuItem item) {
        Intent t=new Intent(this,MainActivity.class);
        startActivity(t);
    }
    public void RemoveScreen(MenuItem item) {
        Intent t=new Intent(this,User_Removal.class);
        startActivity(t);

    }

    public void ImageScreen(MenuItem item) {
        Intent t=new Intent(this,Upload_Images.class);
        startActivity(t);

    }

    public void ScanScreen(MenuItem item) {
        Intent t=new Intent(this,Barcode_Scan.class);
        startActivity(t);
    }
}
