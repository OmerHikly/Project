package com.example.alpha_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.example.alpha_test.FirebaseHelper.refSchool;

public class profile extends AppCompatActivity {

    Button Chgp,Remu;
    TextView Name,Class,Educator,Id,Phone;
    ImageView Iv;

    Guard guard;
    Student student;
    Teacher teacher;



    String tphone;
    String sphone,cls,educator,id,school,name;
    int UserType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Iv=findViewById(R.id.Passport);
        Name=findViewById(R.id.UserName);
        Class=findViewById(R.id.UserClass);
        Educator=findViewById(R.id.Educator);
        Id=findViewById(R.id.ID);
        Phone=findViewById(R.id.UserPhone);

        Chgp=findViewById(R.id.ChangePass);
        Remu=findViewById(R.id.RemoveAcc);

        Intent gi=getIntent();
        tphone=gi.getStringExtra("tphone");
        sphone=gi.getStringExtra("sphone");
        school=gi.getStringExtra("sc");
        UserType=gi.getIntExtra("st",0);




        getUser();
    }

    private void getUser() {
        refSchool.child(school).child("Student").child(sphone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                student = new Student();
                student = dataSnapshot.getValue(Student.class);
                id = student.getId();
                name = student.getName()+" "+student.getSecondName();
                cls=student.getCls();
                findEducator();

                Name.setText(Name.getText().toString()+" "+name);
                Class.setText(Class.getText().toString()+" "+cls);
                Id.setText(Id.getText().toString()+" "+id);
                Phone.setText(Phone.getText().toString()+" "+sphone);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void findEducator() {
        refSchool.child(school).child("Teacher").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dsp:dataSnapshot.getChildren()){
                    if(dsp.child("cls").getValue().equals(cls)){
                        String edphone=dsp.getKey();

                        if(edphone.equals(tphone)){
                            educator="את/ה";
                            Chgp.setVisibility(View.VISIBLE);
                            Remu.setVisibility(View.VISIBLE);

                        }
                        else{
                            educator=dsp.child("name").getValue().toString()+" "+dsp.child("secondName").getValue().toString();

                        }
                        Educator.setText(Educator.getText().toString()+" "+educator);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
