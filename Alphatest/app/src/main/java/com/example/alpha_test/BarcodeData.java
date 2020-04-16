package com.example.alpha_test;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.alpha_test.FirebaseHelper.refSchool;

public class BarcodeData extends AppCompatActivity {
    ImageView iv;
    TextView name,cls,teacher,cause,timeOut,notes,date;
    Toolbar toolbar;

    Student student;

    String data,school;
    String Name,Cls,Id,teacherName,Phone,TimeOut,Notes,Cause,TimeAndDate;

    Context ctx = this;
    StorageReference mStorageRef;
    public static StorageReference Ref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        iv=findViewById(R.id.Passport);
        name=findViewById(R.id.UserName);
        cls=findViewById(R.id.UserClass);
        teacher=findViewById(R.id.Educator);
        cause=findViewById(R.id.ExitCause);
        timeOut=findViewById(R.id.ExitTime);
        notes=findViewById(R.id.Notes);
        date=findViewById(R.id.Date);

        toolbar=findViewById(R.id.tb);

        toolbar.setTitle("נתוני יציאה");
        setSupportActionBar(toolbar);

        Intent gi=getIntent();
        data=gi.getStringExtra("data");
        school=gi.getStringExtra("school");

        mStorageRef = FirebaseStorage.getInstance().getReference();


        String Splitted[]=data.split(";");
         Phone=Splitted[0];
        Ref = mStorageRef.child("Students").child( Phone+ "Profile");
        DownloadImg();


        teacherName=Splitted[1];
        Cause=Splitted[2];
        Notes=Splitted[3];
        TimeOut=Splitted[5]+"-"+Splitted[6];

         String myDate=new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
        Calendar rightNow = Calendar.getInstance();
        int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
        int minutes = rightNow.get(Calendar.MINUTE);

        String time = currentHour + ":" + minutes;


        TimeAndDate= myDate+", "+time;

        teacher.setText(teacher.getText()+" "+teacherName);
        cause.setText(cause.getText()+" "+Cause);
        notes.setText(notes.getText()+" "+Notes);
        timeOut.setText(timeOut.getText()+" "+TimeOut);
        date.setText(TimeAndDate);
        getStudent();





    }

    private void getStudent() {
        refSchool.child(school).child("Student").child(Phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                student=new Student();
                student= dataSnapshot.getValue(Student.class);
                Name=student.getName()+" "+student.getSecondName();
                Cls=student.getCls();
                Id=student.getId();


                name.setText(name.getText()+" "+Name);
                cls.setText(cls.getText()+" "+Cls);

                refSchool.child(school).child("Student").child(Phone).child("QR_Info").removeValue();

                String out=Name+"; "+Cls+"; "+Id+"; "+teacherName+"; "+Phone+"; "+Cause+"; "+Notes+"; "+TimeOut+"; "+TimeAndDate;

                String st= String.valueOf(System.currentTimeMillis());
                refSchool.child(school).child("AAMonitor").child(st+"_"+Phone+"_"+TimeAndDate).setValue(out);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DownloadImg() {// a method that downloads the url of the last added image
        Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(ctx).load(uri).fit().centerCrop().into(iv);
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "הורדת התמונה נכשלה", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
