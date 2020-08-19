package com.example.coronacheckcustomer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.coronacheckcustomer.FBref.refSchool;
import static com.example.coronacheckcustomer.FBref.refUsers;

public class Login extends AppCompatActivity {
    EditText Phone, Password;//פרטים להזנה טלפון ומספר
    Toolbar toolbar;
    TextView log;


    Customer customer;


    Boolean  stayConnect;

    SpannableString ss;

    String typedpass;
    String   phone;

    int sender=-1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Phone = findViewById(R.id.Phone);
        Password = findViewById(R.id.Password);
        log=findViewById(R.id.log);

        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("התחברות");
        setSupportActionBar(toolbar);

        ss=new SpannableString(log.getText().toString());
        ClickableSpan cs=new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                finish();
                startActivity(new Intent(Login.this, Register.class));
            }
        };
        ss.setSpan(cs,14,22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        log.setText(ss);
        log.setMovementMethod(LinkMovementMethod.getInstance());




        stayConnect = false;


    }

    public void login(View view) {//פעולה בעת לחיצה על התחבר מקבלת לString את מה שהוקלד
        phone = "+972" + Phone.getText().toString();
        typedpass = Password.getText().toString();
        CheckIfPhoneExists();//פעולה הבודקת אם הבית ספר שהוקלד קיים




    }





    public  void CheckIfPhoneExists() {//This method checks under each type of user in the firebase if the typed phone number already as signed in
        String ph = Phone.getText().toString();
        if (ph.isEmpty() || ph == "" || ph == null) {
            Toast.makeText(getApplicationContext(), "נא להזין מס' טלפון", Toast.LENGTH_SHORT).show();
        }
        else{
            refUsers.orderByChild("phone").equalTo(phone)
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                CustomerActivity();

                            } else {
                                NoUser();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

    }



    private void CustomerActivity() {//password confirmation
        refUsers.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String pass = dataSnapshot.child("password").getValue().toString();
                if (typedpass.equals(pass)) {
                    customer = dataSnapshot.getValue(Customer.class);
                    CustomerScreen();//פעולה שמעבירה את המשתמש למסך הבא בהתאם לסוג המשתמש (מורה)
                }

                else{
                    Password.setError("סיסמה שגויה");
                    Password.requestFocus();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }






    //login successful







    private void NoUser() {//מתריעה למשתמש אם הפרטים שלו קיימים או לא
        Toast.makeText(getApplicationContext(),"המשתמש אינו קיים",Toast.LENGTH_SHORT).show();
    }

    //what happens when log in is successful








    private void CustomerScreen() {//העברה למסך כניסה של תלמיד
        Intent i=new Intent(this,MainScreen.class);
        Parcelable parcelable= Parcels.wrap(customer);
        i.putExtra("customer", parcelable);
        finish();
        startActivity(i);
    }


}
