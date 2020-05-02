package com.example.alpha_test;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.parceler.Parcels;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarcodeScan extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;
    private TextView tv;

    Guard guard;
    String school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);

        scannerView = (ZXingScannerView) findViewById(R.id.scan);
        tv = findViewById(R.id.txt_result);


        Parcelable parcelable = getIntent().getParcelableExtra("guard");
        guard = Parcels.unwrap(parcelable);

        school = guard.getSchool();

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        scannerView.setResultHandler(BarcodeScan.this);
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getApplicationContext(), "The application won't work without a camera permission", Toast.LENGTH_LONG);

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }

    @Override
    protected void onDestroy() {
        scannerView.stopCamera();
        super.onDestroy();
    }


    @Override
    public void handleResult(Result rawResult) {
        ProcessRawResult(rawResult.getText());

    }


    private void ProcessRawResult(String text) {
        Intent i = new Intent(this, BarcodeData.class);
        i.putExtra("data", text);
        i.putExtra("school", school);
        finish();
        startActivity(i);
        tv.setText(text);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scannerView.resumeCameraPreview(BarcodeScan.this);
            }
        }, 2000);


    }


}




















