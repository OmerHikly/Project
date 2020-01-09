package com.example.alpha_test;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.example.alpha_test.FirebaseHelper.mAuth;
import static com.example.alpha_test.FirebaseHelper.refSchool;

public class MainActivity extends AppCompatActivity {
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


    boolean bo = false;
    boolean IfAdmin = false;
    boolean IfGuard = false;
    boolean login = false;
    boolean exists=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirstName = findViewById(R.id.FirstName);
        SecondName = findViewById(R.id.SecondName);
        Id = findViewById(R.id.Id);
        Schoolcode = findViewById(R.id.SchoolCode);
        PhoneNum = findViewById(R.id.phone);
        Password = findViewById(R.id.password);


        et = findViewById(R.id.et);
        btn = findViewById(R.id.sign_up);
        tv = findViewById(R.id.sign_option);
        bt = findViewById(R.id.Ver);

        spinc = findViewById(R.id.Class);
        spinn = findViewById(R.id.ClassNumber);


        ArrayAdapter<CharSequence> ClassAdapter = ArrayAdapter.createFromResource(this, R.array.classes, R.layout.support_simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> NumbersAdapter = ArrayAdapter.createFromResource(this, R.array.Numbers, R.layout.support_simple_spinner_dropdown_item);
        ClassAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        NumbersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinc.setAdapter(ClassAdapter);
        spinn.setAdapter(NumbersAdapter);


    }


    String codeSent;


    String ClassAndNumber = null;
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
                if (!checkId()) {
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
                    if (schoolcode.length() != 6) {
                        Schoolcode.setError("School code is illegal");
                        Schoolcode.requestFocus();
                        return;

                    } else {

                        if (!Pattern.matches("[א-י]+", spinc.getSelectedItem().toString())) {
                            Toast.makeText(MainActivity.this, "Please pick class", Toast.LENGTH_SHORT).show();

                        }
                        if (!Pattern.matches("[0-9]+", spinn.getSelectedItem().toString())) {
                            Toast.makeText(MainActivity.this, "Please pick class number", Toast.LENGTH_SHORT).show();
                        } else {
                            ClassAndNumber = String.valueOf(spinc.getSelectedItem()) + "'" + String.valueOf(spinn.getSelectedItem());
                            String phone = PhoneNum.getText().toString();
                            pass = Password.getText().toString();
                            if (pass.isEmpty()) {
                                Password.setError("Password is required");
                                Password.requestFocus();
                                return;
                            }
                            if (pass.equals("12358" + schoolcode)) {
                                IfAdmin = true;
                            }
                            if (pass.equals("1618" + schoolcode)) {
                                IfGuard = true;
                            }


                        }
                    }


                }
            }

        }


        String phone = PhoneNum.getText().toString();


        PhoneAuthentication();


        PhoneNum.getText().clear();
        Password.getText().clear();

    }

    private boolean checkId() {
        String str = id;
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
                checkIfSchoolExists();
              //  CheckIfPhoneExists();
             //   SendVerificationCode();


            }
        }
    }

    private void checkIfSchoolExists() {
            refSchool.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(schoolcode)){
                        Toast.makeText(getApplicationContext(),"School Exists",Toast.LENGTH_SHORT).show();
                        CheckIfPhoneExists();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"School isn't exist",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


    }

    public  void CheckIfPhoneExists() {//This method checks under each type of user in the firebase if the typed phone number already as signed in
        refSchool.child(schoolcode).child("Student").orderByChild("phone").equalTo(Phone).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Toast.makeText(getApplicationContext(), "User with that phone number already exists", Toast.LENGTH_SHORT).show();

                } else {
                    refSchool.child(schoolcode).child("Teacher").orderByChild("phone").equalTo(Phone).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                Toast.makeText(getApplicationContext(), "User with that phone number already exists", Toast.LENGTH_SHORT).show();

                            } else {
                                refSchool.child(schoolcode).child("Admin").orderByChild("phone").equalTo(Phone).addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            Toast.makeText(getApplicationContext(), "User with that phone number already exists", Toast.LENGTH_SHORT).show();

                                        } else {
                                            SendVerificationCode();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });


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


    public void SendVerificationCode() {//The method that sents the code

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



                            //it means user already registered
                            //Add code to show your prompt



                                       if (IfAdmin) {
                                           Admin admin = new Admin(firstname, secondname, id, schoolcode, Phone, pass);
                                           refSchool.child(schoolcode).child("Admin").child(Phone).setValue(admin).addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   Toast.makeText(MainActivity.this, "Data was sucssesfully added", Toast.LENGTH_LONG).show();
                                               }
                                           });
                                           IfAdmin = false;
                                       } else {
                                           Toast.makeText(getApplicationContext(), "You are a new user now", Toast.LENGTH_LONG).show();
                                           if (!bo) {
                                               Toast.makeText(getApplicationContext(), "Student", Toast.LENGTH_LONG).show();

                                               Student student = new Student(firstname, secondname, id, schoolcode, ClassAndNumber, Phone, pass);
                                               refSchool.child(schoolcode).child("Student").child(Phone).setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                       Toast.makeText(MainActivity.this, "Data was sucssesfully added", Toast.LENGTH_LONG).show();
                                                   }
                                               });
                                           } else {
                                               Teacher teacher = new Teacher(firstname, secondname, id, schoolcode, ClassAndNumber, Phone, pass);
                                               refSchool.child(schoolcode).child("Teacher").child(Phone).setValue(teacher).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                       Toast.makeText(MainActivity.this, "Data was sucssesfully added", Toast.LENGTH_LONG).show();
                                                   }


                                               });

                                           }
                                       }
                                       ChangeView2();




                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(MainActivity.this, "The verification code entered was invalid", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

                }




    public void teacher_student(View view) {
        if (bo) {
            tv.setText("הירשם כמורה");
            //ADD RELEVANT FIELDS FOR STUDENT

        } else {
            tv.setText("הירשם כתלמיד");
            //ADD RELEVANT FIELDS FOR Teacher


        }
        bo = !bo;

    }

    //Activities
    public void login(View view) {
        Intent intento = new Intent(this, LoginScreen.class);
        startActivity(intento);
    }




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
