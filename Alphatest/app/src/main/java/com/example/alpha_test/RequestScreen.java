package com.example.alpha_test;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.alpha_test.FirebaseHelper.refSchool;

public class RequestScreen extends AppCompatActivity {

    ImageView iv;
    TextView name,clss,ID,cause,timeOut,notes,date;

    String Name,Cls,Id,Notes,Cause,TimeOut,Datee,Time;
    String ex,re;

    Student student;

    String sphone;
    Teacher teacher;
    String school,phone,cls;

    Context ctx=this;
    String ref;

    DatabaseReference RequestRef;
    DatabaseReference refBarcode;

    StorageReference mStorageRef;
    public static StorageReference Ref;
    String Dol;

    long millis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_screen);


        iv=findViewById(R.id.Passport);
        name=findViewById(R.id.UserName);
        clss=findViewById(R.id.UserClass);
        ID=findViewById(R.id.ID);
        cause=findViewById(R.id.ExitCause);
        timeOut=findViewById(R.id.ExitTime);
        notes=findViewById(R.id.Notes);
        date=findViewById(R.id.Date);

        mStorageRef = FirebaseStorage.getInstance().getReference();


        Intent gi=getIntent();
        sphone=gi.getStringExtra("sphone");
        sphone=sphone.substring(1);
        Toast.makeText(getApplicationContext(),sphone,Toast.LENGTH_SHORT).show();

        Parcelable parcelable=getIntent().getParcelableExtra("teacher");
        teacher= Parcels.unwrap(parcelable);

        school=teacher.getSchool();
        phone=teacher.getPhone();
        cls=teacher.getCls();

        Ref = mStorageRef.child("Students").child( sphone+ "Profile");
        DownloadProfileImg();



        RequestRef=  refSchool.child(school).child("Teacher").child(phone).child("Requests").child(sphone);


    RequestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String req=dataSnapshot.getValue().toString();
                make(req);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });











    }

    private void make(String request) {
        String Splitted[]=request.split(";");
        Name=Splitted[0];
        Cls=Splitted[1];
        Datee=Splitted[2]+" "+Splitted[3];
        Id=Splitted[5];
        Cause=Splitted[6];
        Notes=Splitted[7];
        ex=Splitted[8];
        re=Splitted[9];
        Time=Splitted[10];
        Dol=Splitted[11];
        TimeOut=ex+"-"+re;
        if(Time!="null"&&Time!=null) {
             ref=Time.substring(1);
        }


        name.setText(name.getText()+" "+Name);
        clss.setText(clss.getText()+" "+Cls);

        ID.setText(ID.getText()+" "+Id);
        cause.setText(cause.getText()+" "+Cause);
        notes.setText(notes.getText()+" "+Notes);
        timeOut.setText(timeOut.getText()+" "+TimeOut+"    "+Dol);
        date.setText(Datee);





        SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        String Splitt[]=Dol.split("/");
        String year=Splitt[2];
        String month=Splitt[1];
        String day=Splitt[0];

        String myDate = year + "/" + month + "/" + day + " "  + ex  + ":" + "00";

        Date date = null;
        try {
            date = sd.parse(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        millis = date.getTime();

    }

    private void DownloadProfileImg() {// a method that downloads the url of the last added image
        Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(ctx).load(uri).fit().centerCrop().into(iv);
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();

            }
        });

    }




    public void disapprove(View view) {
        RequestRef.removeValue();
        Toast.makeText(getApplicationContext(),"הבקשה נדחתה בהצלחה",Toast.LENGTH_SHORT).show();
        Intent i=new Intent(this,Requests.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);

    }

    public void approve(View view) {
        Name=teacher.getName()+" "+teacher.getSecondName();
        String dataStore = sphone + "; "+Name + "; " + Cause + "; " + Notes + "; " + Id+"; "+ex + "; " + re+"; "+millis;
        refBarcode= refSchool.child(school).child("Student").child(sphone).child("QR_Info");
       refBarcode.setValue(dataStore);
        final long maxDurationInMilliseconds = 30 * 60 * 1000;


        Timer timer = new Timer();
        timer.schedule( new TimerTask(){
            public void run() {
                refBarcode.removeValue();
            }
        },  maxDurationInMilliseconds );

        Toast.makeText(getApplicationContext(),"אישור נשלח לתלמיד",Toast.LENGTH_SHORT).show();
        RequestRef.removeValue();
        Intent i=new Intent(this,Requests.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);
    }


    public void ShowParentsPermit(View view) {
        Intent i=new Intent(this,ParentsPermit.class);
        i.putExtra("refrence",ref);
        startActivity(i);
    }
}

