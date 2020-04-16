package com.example.alpha_test;

import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import static com.example.alpha_test.FirebaseHelper.refSchool;

public class DataView extends AppCompatActivity {
    String school;//מחרוזת מסוג בית ספר שתכיל את הבית ספר אליו שייך האדמין
    String phone;//מחרוזת מסוג טלפון שתכיל את מספר הטלפון של האדמין

    Boolean T=true;//משתנה להשוואה עם תכונת הBoolean בבסיס הנתונים

    int admins=0,guards=0,teachers=0,students=0;//משתנים שיכילו את מספר המשתמשים שקיימים עבורכל סוג של משתמש

    Admin admin;//עצם מסוג אדמין עבור המסך הזה

    TextView Admins,Guards,Teachers,Students;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);

        Parcelable parcelable=getIntent().getParcelableExtra("adminW");//קבלת עצם האדמין מהאקטיביטים הקודמים
        admin= Parcels.unwrap(parcelable);//קישורו אל העצם מסוג אדמין שהגדרנו עבור המסך הזה

        school = admin.getSchool();//השמת ערכים בתכונות האדמין
        phone = admin.getPhone();

        Admins=findViewById(R.id.TvAdmin);
        Guards=findViewById(R.id.TvGuard);
        Teachers=findViewById(R.id.TvTeacher);
        Students=findViewById(R.id.TvStudent);
        toolbar=findViewById(R.id.tb);

        toolbar.setTitle("נתוני בית הספר");
        setSupportActionBar(toolbar);

       HowMany();//פעולה שתספור ותציג כמה שומרים יש כמה תלמידים יש כמה מורים יש וכמה אדמינים יש
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
