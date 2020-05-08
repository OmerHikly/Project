package com.example.safeentrance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static com.example.safeentrance.FBref.refSchool;

public class PermitionsRecived extends AppCompatActivity {

    ImageView iv;
    Toolbar toolbar;
    String TAG="GenerateQrCode";
    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    DatabaseReference refBarcode;
    Student student;

    String school,phone;



    long current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permitions_recived);
        iv=findViewById(R.id.Barcode);
        toolbar=findViewById(R.id.tb);

        toolbar.setTitle("ברקוד יציאה");
        setSupportActionBar(toolbar);

        Parcelable parcelable=getIntent().getParcelableExtra("student");
        student= Parcels.unwrap(parcelable);

        school=student.getSchool();
        phone=student.getPhone();

        refBarcode= refSchool.child(school).child("Student").child(phone).child("Permit").child("qr_Info");

        //המרתת הזמן הנוכחי לאלפיות השניה



        GenerateAndShow();
    }

    private void GenerateAndShow() {
        refBarcode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String d = dataSnapshot.getValue().toString();
                    String Splitted[] = d.split("; ");
                    String t = Splitted[0];
                    long sendTime = Long.parseLong(t);
                    long Untill = sendTime + 12 * 60 * 60 * 1000;// 12 שעות במילישניות
                    current=System.currentTimeMillis();

                    String st = String.valueOf(current);
                    Toast.makeText(getApplicationContext(),st,Toast.LENGTH_LONG);



                    if ((sendTime < current)&&(current<Untill)) {
                        Continue(d);
                        } else {
                            if (current > Untill) {
                                refBarcode.removeValue();
                                Toast.makeText(getApplicationContext(), "זמן היציאה חלף", Toast.LENGTH_SHORT).show();
                            } else if (current < sendTime) {
                                Toast.makeText(getApplicationContext(), "נא לחכות עד זמן היציאה", Toast.LENGTH_SHORT).show();
                            }
                        }

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
