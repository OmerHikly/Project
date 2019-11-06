package com.example.alpha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    EditText et1;
    EditText Password;
    TextView tv;
    Spinner spin;


    LinearLayout dialog;

    EditText dial_et;
    Button dial_btn;
    AlertDialog.Builder adb;


    private FirebaseAuth mAuth;

    boolean bo = true;

    PhoneAuthProvider.ForceResendingToken mResendToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et1 = findViewById(R.id.mail_phone);
        Password = findViewById(R.id.password);
        btn = findViewById(R.id.sign_up);
        tv = findViewById(R.id.sign_option);
        spin = findViewById(R.id.spinner3);

        dial_et = findViewById(R.id.d_et);
        dial_btn = findViewById(R.id.d_btn);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
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
                    }
                }
            });
        }
    }


    private void PhoneAuthentication() {
        Phone = et1.getText().toString();

        if (Phone.isEmpty()) {
            Toast.makeText(getApplicationContext(), "You didn't write phone number...", Toast.LENGTH_SHORT).show();

        } else {
            if (Phone.length() < 10) {
                Toast.makeText(getApplicationContext(), "Phone Number is not valid", Toast.LENGTH_SHORT).show();
            } else {
                Phone="+972"+Phone;
                SendVerificationCode();
              //  adb = new AlertDialog.Builder(this);
                //adb.setView(dialog);
                //adb.setTitle("Code Verification");
                //adb.show();



            }
        }
    }

    private void SendVerificationCode() {
        Toast.makeText(getApplicationContext(),"stage 1",Toast.LENGTH_SHORT).show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                Phone,        // Phone number to verify
                10,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);

    }

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //verifying the code
            Toast.makeText(getApplicationContext(),"We are sending you the code...",Toast.LENGTH_SHORT).show();
            SignInWithPhoneAuthCredential(phoneAuthCredential);

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(),"User is already exists",Toast.LENGTH_SHORT).show();



        }


        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
            mResendToken = forceResendingToken;
            Toast.makeText(getApplicationContext(),"code sent",Toast.LENGTH_SHORT).show();


        }


    };





    private void SignInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"You are a new user now",Toast.LENGTH_LONG).show();

                            // Sign in success, update UI with the signed-in user's information


                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),"sign in failed",Toast.LENGTH_LONG).show();
                                // The verification code entered was invalid
                            }
                        }
                    }
                });

    }



    //Activities path:
            public void AuthScreen (MenuItem item){
                Intent t = new Intent(this, MainActivity.class);
                startActivity(t);
            }

            public void RemoveScreen (MenuItem item){
                Intent t = new Intent(this, User_Removal.class);
                startActivity(t);

            }

            public void ImageScreen (MenuItem item){
                Intent t = new Intent(this, Upload_Images.class);
                startActivity(t);

            }

            public void ScanScreen (MenuItem item){
                Intent t = new Intent(this, Barcode_Scan.class);
                startActivity(t);
            }
//Set setting for mail or phone
            public void phone_mail (View view){
                if (bo == true) {
                    tv.setText("sign up with email address");
                    et1.setInputType(InputType.TYPE_CLASS_PHONE);
                    et1.setHint("Phone number");
                    spin.setVisibility(View.VISIBLE);
                } else {
                    tv.setText("sign up with phone number");
                    et1.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    et1.setHint("Enter mail");
                    spin.setVisibility(View.GONE);

                }
                bo = !bo;

            }






    }



