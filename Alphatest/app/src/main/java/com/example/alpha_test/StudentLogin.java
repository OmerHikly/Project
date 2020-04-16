package com.example.alpha_test;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import static com.example.alpha_test.FirebaseHelper.refSchool;

public class StudentLogin extends AppCompatActivity {
    TextView stv;
    Button btn;

    Student student;
    Toolbar toolbar;

    String school,phone;


    DatabaseReference refBarcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        stv=findViewById(R.id.stv);
        toolbar=findViewById(R.id.tb);
        btn=findViewById(R.id.btn);


        Parcelable parcelable=getIntent().getParcelableExtra("student");
        student= Parcels.unwrap(parcelable);

        school=student.getSchool();
        phone=student.getPhone();

        toolbar.setTitle("ByeCode");
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_menu_black_24dp);
        toolbar.setOverflowIcon(drawable);
        setSupportActionBar(toolbar);



        refBarcode = refSchool.child(school).child("Student").child(phone).child("QR_Info");

        stv.setText("שלום"+" "+student.getName()+"!");

        refBarcode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    btn.setCompoundDrawablesWithIntrinsicBounds( R.drawable.permission, 0, 0, 0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
      @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionsmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }



    public void Profile(MenuItem item) {
        Intent i = new Intent(this, ChangeProfile.class);
        i.putExtra("type",0);
        Parcelable parcelable= Parcels.wrap(student);
        i.putExtra("student", parcelable);
        startActivity(i);
    }

    public void AskPermition(View view) {
        Intent i = new Intent(this, AskExit.class);
        Parcelable parcelable= Parcels.wrap(student);
        i.putExtra("student", parcelable);
        startActivity(i);
    }

    public void ViewPermition(View view) {
        Intent i = new Intent(this, PermitionsRecived.class);
        Parcelable parcelable= Parcels.wrap(student);
        i.putExtra("student", parcelable);
        startActivity(i);

    }
}
