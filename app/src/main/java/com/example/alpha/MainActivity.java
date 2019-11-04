package com.example.alpha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Button btn;
    EditText Mail;
    EditText PhoneNum;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Mail = findViewById(R.id.mail);
        PhoneNum = findViewById(R.id.phone);
        btn = findViewById(R.id.sign_up);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
   getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }




    String Email;
    String phone;

    public void SendData(View view) {
        Email = Mail.getText().toString();
        phone = PhoneNum.getText().toString();

        if (Email.isEmpty()) {
            Mail.setError("Email is required");//not learned- doing a red icon next to the Edit Text
            // with a text view of the text given
            Mail.requestFocus();
            return;

        } else {

            if (phone.isEmpty()) {
                PhoneNum.setError("Phone number is required");
                PhoneNum.requestFocus();
                return;

            } else {


                if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                    Toast.makeText(getApplicationContext(), "Email is not valid", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(Email, phone).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "User registered successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        }
    }



//Activities path:
    public void AuthScreen(MenuItem item) {
        Intent t=new Intent(this,MainActivity.class);
        startActivity(t);
}
    public void RemoveScreen(MenuItem item) {
        Intent t=new Intent(this,User_Removal.class);
        startActivity(t);

    }

    public void ImageScreen(MenuItem item) {
        Intent t=new Intent(this,Upload_Images.class);
        startActivity(t);

    }

    public void ScanScreen(MenuItem item) {
        Intent t=new Intent(this,Barcode_Scan.class);
        startActivity(t);
    }
}
