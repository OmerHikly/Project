package com.example.coronacheckcustomer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.Calendar;

import static com.example.coronacheckcustomer.FBref.refUsers;

public class MainScreen extends AppCompatActivity {
    TextView stv;


    Customer customer;
    Toolbar toolbar;

    String phone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        stv=findViewById(R.id.stv);
        toolbar=findViewById(R.id.tb);


     

        toolbar.setTitle("ByeCode");
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_menu_black_24dp);
        toolbar.setOverflowIcon(drawable);
        setSupportActionBar(toolbar);




        Parcelable parcelable=getIntent().getParcelableExtra("customer");
        customer= Parcels.unwrap(parcelable);

        phone=customer.getPhone();

        stv.setText("שלום"+" "+customer.getFirstName()+"!");

        getCustomer();
    }

    private void getCustomer() {
        refUsers.child(phone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customer = dataSnapshot.getValue(Customer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
        public void onBackPressed() {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage("האם אתה בטוח שאתה רוצה לצאת מהאפליקציה?")
                    .setCancelable(false)
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainScreen.super.onBackPressed();
                        }
                    })

                    .setNegativeButton("לא", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog=builder.create();
            alertDialog.show();






        }

        public void Declare(View view) {
            Intent i = new Intent(this, CoronaDecleration.class);
            Parcelable parcelable= Parcels.wrap(customer);
            i.putExtra("customer", parcelable);
            startActivity(i);
        }

        public void AddChildren(View view) {
            Intent i = new Intent(this, AddChildren.class);
            Parcelable parcelable= Parcels.wrap(customer);
            i.putExtra("customer", parcelable);

            startActivity(i);
        }

        public void JoinOrganiztion(View view) {
            Intent i = new Intent(this, JoinOrganiztion.class);
            Parcelable parcelable= Parcels.wrap(customer);
            i.putExtra("customer", parcelable);
            startActivity(i);
        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.optionsmenu, menu);
            return super.onCreateOptionsMenu(menu);
        }


        public void SetPassActivity(MenuItem item) {
            Intent i = new Intent(this, ChangePass.class);
            Parcelable parcelable= Parcels.wrap(customer);
            i.putExtra("customer", parcelable);
            startActivity(i);
        }



        public void Profile(MenuItem item) {
            Intent i = new Intent(this, ChangeProfile.class);
            i.putExtra("type",0);
            Parcelable parcelable= Parcels.wrap(customer);
            i.putExtra("customer", parcelable);
            startActivity(i);
        }

    public void ChangeDetails(MenuItem item) {
        Intent i = new Intent(this, ChangeDetails.class);
        Parcelable parcelable= Parcels.wrap(customer);
        i.putExtra("customer", parcelable);
        startActivity(i);
    }




    }

