package com.example.alpha_test;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;

import org.parceler.Parcels;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static com.example.alpha_test.FirebaseHelper.refSchool;

public class PermitionsRecived extends AppCompatActivity {
ImageView iv;
String TAG="GenerateQrCode";
Bitmap bitmap;
QRGEncoder qrgEncoder;

    Student student;

    String school,phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permitions_recived);
        iv=findViewById(R.id.Barcode);

        Parcelable parcelable=getIntent().getParcelableExtra("student");
        student= Parcels.unwrap(parcelable);

        school=student.getSchool();
        phone=student.getPhone();
        GenerateAndShow();
    }

    private void GenerateAndShow() {
        refSchool.child(school).child("Student").child(phone).child("QR_Info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    String d=dataSnapshot.getValue().toString();
                    Continue(d);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Continue(String QrInfo) {
        WindowManager manager= (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display=manager.getDefaultDisplay();
        Point point=new Point();
        int width=point.x;
        int height=point.y;
        int smallerdemension=width<height ? width:height;
        smallerdemension=smallerdemension*3/4;

        QRGEncoder qrgEncoder=new QRGEncoder(QrInfo,null, QRGContents.Type.TEXT,smallerdemension);
        try{
            bitmap=qrgEncoder.encodeAsBitmap();
            iv.setImageBitmap(bitmap);
        }
        catch (WriterException e){
            Log.e(TAG, e.getMessage());

        }
    }


}
