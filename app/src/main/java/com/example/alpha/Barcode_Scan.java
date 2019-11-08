package com.example.alpha;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alpha.Model.QRGeoModel;
import com.example.alpha.Model.QRUrlModel;
import com.example.alpha.Model.QRVcardModel;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Barcode_Scan extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode__scan);

        scannerView=(ZXingScannerView)findViewById(R.id.scan);
        tv=findViewById(R.id.txt_result);

        Dexter.withActivity(this)
               .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        scannerView.setResultHandler(Barcode_Scan.this);
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getApplicationContext(),"The application won't work without a camera permission",Toast.LENGTH_LONG);

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
//A construction that checks which types of qr code text we get and remove their firet letters
    //Works with Vcard,Vevent,Url,,Geo and text
    private void ProcessRawResult(String text) {
        if (text.startsWith("BEGIN:")) {
            String[] tokens = text.split("\n");
            QRVcardModel qrVcardModel = new QRVcardModel();
//those are all the prewords or letters that come from scanning qr codes
            for (int i = 0; i < tokens.length; i++) {
                if (tokens[i].startsWith("BEGIN:")) {
                    qrVcardModel.setType(tokens[i].substring("BEGIN:".length()));//Remove Begin to get type
                } else if (tokens[i].startsWith("N:")) {
                    qrVcardModel.setName(tokens[i].substring("N:".length()));

                } else if (tokens[i].startsWith("ORG:")) {
                    qrVcardModel.setOrg(tokens[i].substring("N:".length()));
                } else if (tokens[i].startsWith("TEL:")) {
                    qrVcardModel.setTel(tokens[i].substring("TEL:".length()));
                } else if (tokens[i].startsWith("URL:")) {
                    qrVcardModel.setUrl(tokens[i].substring("URL:".length()));
                } else if (tokens[i].startsWith("EMAIL:")) {
                    qrVcardModel.setEmail(tokens[i].substring("EMAIL:".length()));
                } else if (tokens[i].startsWith("ADR:")) {
                    qrVcardModel.setAddress(tokens[i].substring("ADR:".length()));
                } else if (tokens[i].startsWith("NOTE:")) {
                    qrVcardModel.setNote(tokens[i].substring("NOTE:".length()));
                } else if (tokens[i].startsWith("SUMMARY:")) {
                    qrVcardModel.setSummary(tokens[i].substring("SUMMARY:".length()));
                } else if (tokens[i].startsWith("DTSTART:")) {
                    qrVcardModel.setDtstart(tokens[i].substring("DTSTART:".length()));
                } else if (tokens[i].startsWith("DTEND:")) {
                    qrVcardModel.setDtend(tokens[i].substring("DTEND:".length()));
                }
                tv.setText(qrVcardModel.getType());
            }
        }if ((text.startsWith("http://")) || text.startsWith("https://") || text.startsWith("www.")) {
                QRUrlModel qrUrlModel = new QRUrlModel(text);
                tv.setText(qrUrlModel.getUrl());

            } else if (text.startsWith("geo:")) {
                QRGeoModel qrGeoModel = new QRGeoModel();
                String delims = "[ , ?q= ]+";
                String tokens[] = text.split(delims);

            for (int i = 0; i < tokens.length; i++) {
                if (tokens[i].startsWith("geo")) {
                    qrGeoModel.setLat(tokens[i].substring("geo".length()));
                }
                }
            qrGeoModel.setLat(tokens[0].substring("geo".length()));
            qrGeoModel.setLng(tokens[1]);
            qrGeoModel.setGeo_place(tokens[2]);
            tv.setText(qrGeoModel.getLat()+"/"+qrGeoModel.getLng());
        }
else{
    tv.setText(text);
        }
        scannerView.resumeCameraPreview(Barcode_Scan.this);

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
        Intent t=new Intent(this,Upload_Images.class);
        startActivity(t);

    }

    public void ImageScreen(MenuItem item) {
        Intent t=new Intent(this,User_Removal.class);
        startActivity(t);

    }

    public void ScanScreen(MenuItem item) {
        Intent t=new Intent(this,Barcode_Scan.class);
        startActivity(t);
    }
}
