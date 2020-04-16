package com.example.alpha_test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.example.alpha_test.FirebaseHelper.refSchool;

public class SpecificOut extends AppCompatActivity {
    TextView Name,Cls,Id,TeacherName,Phone,Cause,Notes,TimeOut,TimeAndDate;
    Toolbar toolbar;

    String name,cls,id,teachername,phone,cause,notes,timeout,timeanddate;
    String school;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_out);

        Name=findViewById(R.id.Name);
        Cls=findViewById(R.id.Cls);
        Id=findViewById(R.id.Id);
        TeacherName=findViewById(R.id.Teacher);
        Phone=findViewById(R.id.Phone);
        Cause=findViewById(R.id.Cause);
        Notes=findViewById(R.id.Notes);
        TimeOut=findViewById(R.id.TimeOut);
        TimeAndDate=findViewById(R.id.TimeAndDate);

        toolbar=findViewById(R.id.tb);

        toolbar.setTitle("פרטי היציאה");
        setSupportActionBar(toolbar);

        Intent gi=getIntent();
        school=gi.getStringExtra("school");
        key=gi.getStringExtra("key");

        refSchool.child(school).child("AAMonitor").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String out=dataSnapshot.getValue().toString();
                String Splitted[]= out.split("; ");
                name=Splitted[0];
                cls=Splitted[1];
                id=Splitted[2];
                teachername=Splitted[3];
                phone=Splitted[4];
                cause=Splitted[5];
                notes=Splitted[6];
                timeout=Splitted[7];
                timeanddate=Splitted[8];


                Name.setText(Name.getText().toString()+" "+name);
                Cls.setText(Cls.getText().toString()+" "+cls);
                Id.setText(Id.getText().toString()+" "+id);
                TeacherName.setText(TeacherName.getText().toString()+" "+teachername);
                Phone.setText(Phone.getText().toString()+" "+phone);
                Cause.setText(Cause.getText().toString()+" "+cause);
                Notes.setText(Notes.getText().toString()+" "+notes);
                TimeOut.setText(TimeOut.getText().toString()+" "+timeout);
                TimeAndDate.setText(TimeAndDate.getText().toString()+" "+timeanddate);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
