package com.example.alpha_test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.example.alpha_test.FirebaseHelper.refSchool;

public class DataView extends AppCompatActivity {
    String school;
    String phone;

    Boolean T=true;

    int admins=0,guards=0,teachers=0,students=0;


    TextView Admins,Guards,Teachers,Students;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);

        Intent gi = getIntent();
        school = gi.getStringExtra("n");
        phone = gi.getStringExtra("nn");
        Admins=findViewById(R.id.TvAdmin);
        Guards=findViewById(R.id.TvGuard);
        Teachers=findViewById(R.id.TvTeacher);
        Students=findViewById(R.id.TvStudent);

       HowMany();
    }

    private void HowMany() {
        refSchool.child(school).child("Guard").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    if(dsp.child("activated").getValue()==T){
                          guards++;
                    }
                }
                Guards.setText(guards+" שומרים");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


            refSchool.child(school).child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        if(dsp.child("activated").getValue()==T){
                            admins++;
                        }
                    }
                    Admins.setText(admins+" אדמינים");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        refSchool.child(school).child("Teacher").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    if(dsp.child("activated").getValue()==T){
                        teachers++;
                    }
                }
                Teachers.setText(teachers+" מורים");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

            refSchool.child(school).child("Student").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    if(dsp.child("activated").getValue()==T){
                        students++;
                    }
                }
                Students.setText(students+" תלמידים");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });







    }
}
