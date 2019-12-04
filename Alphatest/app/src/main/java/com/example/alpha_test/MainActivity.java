package com.example.alpha_test;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Button btn;
    Button bt;
    EditText et1;
    EditText Password;
    TextView tv;
    EditText et;


    Button dial_btn;


    FirebaseAuth mAuth;

    boolean bo = true;
    boolean co = false;

    PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et1 = findViewById(R.id.mail_phone);
        Password = findViewById(R.id.password);
        et = findViewById(R.id.et);
        btn = findViewById(R.id.sign_up);
        tv = findViewById(R.id.sign_option);
        bt = findViewById(R.id.Ver);


        dial_btn = findViewById(R.id.d_btn);

        mAuth = FirebaseAuth.getInstance();
    }

    String Email;
    String pass;

    String Phone;
    String codeSent;


    public void SendData(View view) {
        pass = Password.getText().toString();
        if (pass.isEmpty()) {
            Password.setError("Phone number is required");
            Password.requestFocus();
            return;
        }
        //CRUSH DOWN BECAUSE OF PHONE.LENGTH();
        if (bo == true) {
            MailAuthentication();
        } else {
            PhoneAuthentication();
        }

        et1.getText().clear();
        Password.getText().clear();
    }


    private void MailAuthentication() {
        Email = et1.getText().toString();


        if (Email.isEmpty()) {
            et1.setError("Email is required");//not learned- doing a red icon next to the Edit Text
            // with a text view of the text given
            et1.requestFocus();
            return;

        }


        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            Toast.makeText(getApplicationContext(), "Email is not valid", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(Email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "User registered successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "User already exists", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }


    private void PhoneAuthentication() {
        Phone = et1.getText().toString();

        System.out.println("Successfully deleted user.");
        if (Phone.isEmpty()) {
            Toast.makeText(getApplicationContext(), "You didn't write phone number...", Toast.LENGTH_SHORT).show();

        } else {
                if (Phone.length() < 10) {
                    Toast.makeText(getApplicationContext(), "Phone Number is not valid", Toast.LENGTH_SHORT).show();
            } else {
                Phone = "+972" + Phone;
                if (bo == false) {
                }
                SendVerificationCode();
            }
        }
    }

    private void verify() {
        ChangeView1();
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code=et.getText().toString();
                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(codeSent,code);
                ChangeView2();
               SignInWithPhoneAuthCredential(credential);
            }
        });

        }








    private void SendVerificationCode() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                Phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }




    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Toast.makeText(getApplicationContext(), "We are sending you the code...", Toast.LENGTH_SHORT).show();

        }


        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), "Sign up failed", Toast.LENGTH_SHORT).show();


        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText(getApplicationContext(), "Code sent...", Toast.LENGTH_SHORT).show();
            codeSent =s;
            verify();
        }
    };





    private void ChangeView1() {
        et1.setVisibility(View.GONE);
        Password.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);
        btn.setVisibility(View.GONE);
        et.setVisibility(View.VISIBLE);
        bt.setVisibility(View.VISIBLE);

    }




        private void ChangeView2() {
        et1.setVisibility(View.VISIBLE);
        Password.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);
        btn.setVisibility(View.VISIBLE);
        et.setVisibility(View.GONE);
        bt.setVisibility(View.GONE);
        co=false;
        bo=true;

        tv.setText("sign up with phone number");
        et1.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        et1.setHint("Enter mail");

    }


    private void SignInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"You are a new user now",Toast.LENGTH_LONG).show();
                            ChangeView2();
                            FirebaseUser user = task.getResult().getUser();
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),"Verification code is not valid",Toast.LENGTH_LONG).show();
                            }
                         //    else {
                        //    user.delete();
                      //     Toast.makeText(getApplicationContext(),"Oops! User already exists",Toast.LENGTH_LONG).show();
                       //     ChangeView2();
                       // }
                        }
                    }
                });


    }







    public void phone_mail (View view){
        if (bo == true) {
            tv.setText("sign up with email address");
            et1.setInputType(InputType.TYPE_CLASS_PHONE);
            et1.setHint("Phone number");

        } else {
            tv.setText("sign up with phone number");
            et1.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            et1.setHint("Enter mail");


        }
        bo = !bo;

    }
    //Activities
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void AuthScreen (MenuItem item){
        Intent t = new Intent(this, MainActivity.class);
        startActivity(t);
    }

    public void RemoveScreen (MenuItem item){
        Intent t = new Intent(this, AddData.class);
        startActivity(t);

    }

    public void ImageScreen (MenuItem item){
        Intent t = new Intent(this, UploadPictures.class);
        startActivity(t);

    }

    public void ScanScreen (MenuItem item){
        Intent t = new Intent(this, BarcodeScan.class);
        startActivity(t);
    }


}
