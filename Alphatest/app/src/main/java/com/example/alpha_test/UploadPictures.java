package com.example.alpha_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class UploadPictures extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pictures);
    }





























































    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void AuthScreen (MenuItem item){
        Intent t = new Intent(this, MainActivity.class);
        startActivity(t);
    }

    public void RemoveScreen (MenuItem item){
        Intent t = new Intent(this, AddData.class);
        startActivity(t);

    }

    public void ImageScreen (MenuItem item){
        Intent t = new Intent(this, UploadPictures.class);
        startActivity(t);

    }

    public void ScanScreen (MenuItem item){
        Intent t = new Intent(this, BarcodeScan.class);
        startActivity(t);
    }
}
