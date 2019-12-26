package com.example.alpha_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.example.alpha_test.FirebaseHelper.refUsers;

public class MainActivity extends AppCompatActivity  {
    Button btn;
    Button bt;
    EditText PhoneNum;

    TextView tv;
    EditText et;

    EditText Password;
    EditText FirstName;
    EditText SecondName;
    EditText Id;
    EditText Schoolcode;






    Spinner spinc;
    Spinner spinn;

    FirebaseAuth mAuth;

    boolean bo = false;
    boolean IfAdmin = false;
    boolean IfGuard=false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirstName=findViewById(R.id.FirstName);
        SecondName=findViewById(R.id.SecondName);
        Id=findViewById(R.id.Id);
        Schoolcode=findViewById(R.id.SchoolCode);
        PhoneNum = findViewById(R.id.phone);
        Password = findViewById(R.id.password);




        et = findViewById(R.id.et);
        btn = findViewById(R.id.sign_up);
        tv = findViewById(R.id.sign_option);
        bt = findViewById(R.id.Ver);

        spinc = findViewById(R.id.Class);
        spinn = findViewById(R.id.ClassNumber);




        mAuth = FirebaseAuth.getInstance();


        ArrayAdapter<CharSequence> ClassAdapter = ArrayAdapter.createFromResource(this, R.array.classes, R.layout.support_simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> NumbersAdapter = ArrayAdapter.createFromResource(this, R.array.Numbers, R.layout.support_simple_spinner_dropdown_item);
        ClassAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        NumbersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinc.setAdapter(ClassAdapter);
        spinn.setAdapter(NumbersAdapter);


    }



    String Email;



    String codeSent;


    String ClassAndNumber=null;
    String firstname;
    String secondname;
    String id;
    String schoolcode;
    String Phone;
    String pass;



    public void SendData(View view) {//method that saves the data in the edit texts


        firstname = FirstName.getText().toString();
        secondname = SecondName.getText().toString();
        id = Id.getText().toString();
        schoolcode = Schoolcode.getText().toString();



        if (firstname.isEmpty()) {
            FirstName.setError("First name is required");
            FirstName.requestFocus();
            return;
        }
        if (!Pattern.matches("['א-ת]+", firstname)) {
            FirstName.getText().clear();
            firstname = "";
            FirstName.setError("Your first name should contain only letters");
            FirstName.requestFocus();
            return;
        } else {


            if (secondname.isEmpty()) {
                SecondName.setError("Second name is required");
                SecondName.requestFocus();
                return;
            }
            if (!Pattern.matches("['א-ת]+", secondname)) {
                SecondName.getText().clear();
                SecondName.setError("Your second name should contain only letters");
                SecondName.requestFocus();
                return;

            } else {
                if (id.isEmpty()) {
                    Id.setError("id is required");
                    Id.requestFocus();
                    return;
                }
                if (!Pattern.matches("[0-9]+", id)) {
                    Id.setError("id should contain only numbers");
                    Id.requestFocus();
                    return;
                }
                 if(!checkId()){
                     Id.setError("id number is incorrect");
                     Id.requestFocus();
                     return;
                } else {
                    if (schoolcode.isEmpty()) {
                        Schoolcode.setError("School code is required");
                        Schoolcode.requestFocus();
                        return;

                    }
                    if (!Pattern.matches("[0-9]+", schoolcode)) {
                        Schoolcode.setError("School code should contain only numbers");
                        Schoolcode.requestFocus();
                        return;
                    }
                    if(schoolcode.length()!=6){
                        Schoolcode.setError("School code is illegal");
                        Schoolcode.requestFocus();
                        return;

                    } else {

                        if (!Pattern.matches("[א-י]+", spinc.getSelectedItem().toString())){
                            Toast.makeText(MainActivity.this, "Please pick class", Toast.LENGTH_SHORT).show();

                        }
                        if (!Pattern.matches("[0-9]+", spinn.getSelectedItem().toString())){
                            Toast.makeText(MainActivity.this, "Please pick class number", Toast.LENGTH_SHORT).show();
                        }


                        else {
                            ClassAndNumber = String.valueOf(spinc.getSelectedItem()) + "'" + String.valueOf(spinn.getSelectedItem());
                            String phone =PhoneNum.getText().toString();
                            pass = Password.getText().toString();
                            if (pass.isEmpty()) {
                                Password.setError("Password is required");
                                Password.requestFocus();
                                return;
                            }
                            if(pass.equals("12358" + schoolcode)){
                                IfAdmin=true;
                            }
                            if(pass.equals("1618"+schoolcode)){
                                IfGuard=true;
                            }


                        }
                    }


                }
            }

        }


    String phone =PhoneNum.getText().toString();



       PhoneAuthentication();


        PhoneNum.getText().clear();
        Password.getText().clear();

    }

    private boolean checkId() {
        String str=id;
        if ((str.length() > 9) || (str.length() < 5)) {

            return false;
        }
        if (str.length() < 9) {
            while (str.length() < 9) {
                str = '0' + str;
            }
        }
        int sum = 0, incNum;
        for (int i = 0; i < 9; i++) {
            incNum = Integer.parseInt(String.valueOf(str.charAt(i)));
            incNum *= (i % 2) + 1;
            if (incNum > 9) {
                incNum = incNum % 10 + incNum / 10;
            }
            sum += incNum;
        }
        if (sum % 10 == 0) {
            return true;
        } else {
            return false;
        }
    }





    private void MailAuthentication() {//mail authentication
        Email = PhoneNum.getText().toString();
        Toast.makeText(getApplicationContext(), " Email", Toast.LENGTH_SHORT).show();


        if (Email.isEmpty()) {
            PhoneNum.setError("Email is required");//set error doing a red icon next to the Edit Text
            // with a text view of the text given
            PhoneNum.requestFocus();
            return;

        }


        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {//checking if email exists
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


    private void PhoneAuthentication() {//phone authentication

        Phone = PhoneNum.getText().toString();
        if (Phone.isEmpty()) {//Validation of the phone
            Toast.makeText(getApplicationContext(), "You didn't write phone number...", Toast.LENGTH_SHORT).show();

        } else {
            if (Phone.length() < 10) {
                Toast.makeText(getApplicationContext(), "Phone Number is not valid", Toast.LENGTH_SHORT).show();
            } else {

                Phone = PhoneNum.getText().toString();


                Phone = "+972" + Phone;


                SendVerificationCode();
            }
        }
    }


    private void verify() {//Verification of the code sent to the user
        ChangeView1();
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = et.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
                SignInWithPhoneAuthCredential(credential);
            }
        });

    }


    private void SendVerificationCode() {//The method that sents the code

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                Phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        //This method checks the state of the code sending and reacts for that.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Toast.makeText(getApplicationContext(), "We are sending you the code...", Toast.LENGTH_SHORT).show();
            SignInWithPhoneAuthCredential(phoneAuthCredential);
        }


        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), "Sign up failed", Toast.LENGTH_SHORT).show();


        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            //gets the code string and triggeres a method called verify
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText(getApplicationContext(), "Code sent...", Toast.LENGTH_SHORT).show();
            codeSent = s;
            verify();
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(String s) {
            super.onCodeAutoRetrievalTimeOut(s);
            SendVerificationCode();

        }
    };


    private void ChangeView1() {// sets the code verification view
        PhoneNum.setVisibility(View.GONE);
        Password.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);
        btn.setVisibility(View.GONE);
        et.setVisibility(View.VISIBLE);
        bt.setVisibility(View.VISIBLE);

    }


    private void ChangeView2() {// sets the view to the original
        PhoneNum.setVisibility(View.VISIBLE);
        Password.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);
        btn.setVisibility(View.VISIBLE);
        et.setVisibility(View.GONE);
        bt.setVisibility(View.GONE);
        IfAdmin = false;



    }


    private void SignInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getApplicationContext(), "User already exists", Toast.LENGTH_LONG).show();


                            } else
                                if(IfAdmin) {
                                    Admin admin = new Admin(firstname, secondname, id, schoolcode, Phone, pass);
                                    refUsers.child("Admin").child(firstname + " " + secondname + " " + id).setValue(admin).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(MainActivity.this, "Data was sucssesfully added", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    IfAdmin=false;
                                }
                                else{
                                Toast.makeText(getApplicationContext(), "You are a new user now", Toast.LENGTH_LONG).show();
                                if (!bo) {
                                    Toast.makeText(getApplicationContext(), "Student", Toast.LENGTH_LONG).show();

                                    Student student=new Student(firstname,secondname,id,schoolcode,ClassAndNumber,Phone,pass);
                                    refUsers.child("Student").child(firstname+" "+secondname+" "+id).setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(MainActivity.this,"Data was sucssesfully added",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    Student student=new Student(firstname,secondname,id,schoolcode,ClassAndNumber,Phone,pass);
                                    Teacher teacher=new Teacher(firstname,secondname,id,schoolcode,ClassAndNumber,Phone,pass);
                                    refUsers.child("Teacher").child(firstname+" "+secondname+" "+id).setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(MainActivity.this, "Data was sucssesfully added", Toast.LENGTH_LONG).show();
                                         }


                                    });

                                    }
                            }
                            ChangeView2();

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(MainActivity.this, "The verification code entered was invalid", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

                }




    public void teacher_student(View view) {
        if (bo) {
            tv.setText("sign up as a teacher");
            //ADD RELEVANT FIELDS FOR STUDENT

        } else {
            tv.setText("sign up as student");
            //ADD RELEVANT FIELDS FOR Teacher


        }
        bo = !bo;

    }

    //Activities
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void AuthScreen(MenuItem item) {
        Intent t = new Intent(this, MainActivity.class);
        startActivity(t);
    }

    public void RemoveScreen(MenuItem item) {
        Intent t = new Intent(this, AddData.class);
        startActivity(t);

    }

    public void ImageScreen(MenuItem item) {
        Intent t = new Intent(this, UploadPictures.class);
        startActivity(t);

    }

    public void ScanScreen(MenuItem item) {
        Intent t = new Intent(this, BarcodeScan.class);
        startActivity(t);
    }




}
