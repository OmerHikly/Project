package com.example.alpha_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.example.alpha_test.FirebaseHelper.mAuth;
import static com.example.alpha_test.FirebaseHelper.refSchool;

public class MainActivity extends AppCompatActivity {
    Button btn;// רכיבי התצוגה
    Button bt;
    EditText PhoneNum;

    TextView tv;
    TextView sclass;
    TextView snum;


    EditText et;

    EditText Password;
    EditText FirstName;
    EditText SecondName;
    EditText Id;
    EditText Vpass;
    AutoCompleteTextView School;

    ArrayList<String> Schools = new ArrayList<>();//רשימה של בתי ספר שיש ב-firebase



    Spinner spinc;
    Spinner spinn;

    Boolean stayConnect, registered, firstrun;

    boolean bo = false;//משתנה שנועד להבחין בין הרשמה של מורה ותלמיד

    boolean IfAdmin = false; //משתנה שבודק האם המשתמש שנרשם נרשם כאדמין
    boolean IfGuard = false; //משתנה שבודק האם המשתמש שנרשם נרשם כשומר



    DatabaseReference databaseReference;//אזכור לdatabase- יצביע על השורש של העץ בבבסיס הנתונים


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirstName = findViewById(R.id.FirstName);
        SecondName = findViewById(R.id.SecondName);
        School = findViewById(R.id.School);
        Id = findViewById(R.id.Id);
        PhoneNum = findViewById(R.id.phone);
        Password = findViewById(R.id.password);
        Vpass=findViewById(R.id.VerificationPassword);
        sclass=findViewById(R.id.TvClass);
        snum=findViewById(R.id.TvClassNumber);

        et = findViewById(R.id.et);
        btn = findViewById(R.id.sign_up);
        tv = findViewById(R.id.sign_option);
        bt = findViewById(R.id.Ver);

        spinc = findViewById(R.id.Class);
        spinn = findViewById(R.id.ClassNumber);

        stayConnect=false;
        registered=true;


        databaseReference = FirebaseDatabase.getInstance().getReference();


        ArrayAdapter<CharSequence> ClassAdapter = ArrayAdapter.createFromResource(this, R.array.classes, R.layout.support_simple_spinner_dropdown_item);
        //מתאם שנועד לקשר ספינר אל רשימת הכיתות האפשריים
        ArrayAdapter<CharSequence> NumbersAdapter = ArrayAdapter.createFromResource(this, R.array.Numbers, R.layout.support_simple_spinner_dropdown_item);
        //מתאם שנועד לקשר ספינר אל רשימת מספרי הכיתות האפשריים

        ClassAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        NumbersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinc.setAdapter(ClassAdapter);
        spinn.setAdapter(NumbersAdapter); //Array creation for the spinners in the application the array will have the class  and class number


        FirebaseSchools();
        //This method goes to the firebase and gets all the schools (I need this to work every time for when a new school regstered)


    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        Boolean isChecked=settings.getBoolean("stayConnect",false);
        Intent si = new Intent(MainActivity.this,LoginScreen.class);
        if (isChecked) {
            stayConnect=true;
            startActivity(si);
        }
    }

    private void FirebaseSchools() {

        refSchool.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Schools.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {//checks how many main children there are in the firebase(Schools number)
                    String st =dsp.getKey();
                    Schools.add(st);

                }


                Adapt(Schools);//This method sets the adpater between the array we just created and the "edit text" of the school.
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });


    }

    private void Adapt( ArrayList<String> arrayList) {//מקשר בין המתאם ל-listview
        SchoolAdapter adapter=new SchoolAdapter(this, arrayList);
        School.setAdapter(adapter);


    }

//משתנים שיקבלו את מה שהקליד המשתמש
    String codeSent;


    String ClassAndNumber = null;
    String firstname;
    String secondname;
    String id;
    String school;
    String Phone;
    String pass;
    String vpass;

    public void SendData(View view) {//method that saves the data in the edit texts


        firstname = FirstName.getText().toString();
        secondname = SecondName.getText().toString();
        id = Id.getText().toString();
        school = School.getText().toString();

        ClassAndNumber = String.valueOf(spinc.getSelectedItem()) + "'" + String.valueOf(spinn.getSelectedItem());
        pass = Password.getText().toString();
        vpass = Vpass.getText().toString();


        if (firstname.isEmpty()) {
            FirstName.setError("נא לרשום שם");
            FirstName.requestFocus();
            return;
        }
        if (!Pattern.matches("['א-ת]+", firstname)) {
            FirstName.getText().clear();
            firstname = "";
            FirstName.setError("השם שלך צריך להכיל רק אותיות בעברית");
            FirstName.requestFocus();
            return;
        } else {


            if (secondname.isEmpty()) {
                SecondName.setError("נא לרשום שם משפחה");
                SecondName.requestFocus();
                return;
            }
            if (!Pattern.matches("['א-ת]+", secondname)) {
                SecondName.getText().clear();
                SecondName.setError("השם שלך צריך להכיל רק אותיות בעברית");
                SecondName.requestFocus();
                return;


            } else {
                if (id.isEmpty()) {
                    Id.setError("מספר ת.ז הוא חיוני");
                    Id.requestFocus();
                    return;
                }
                if (!Pattern.matches("[0-9]+", id)) {
                    Id.setError("נא לרשום רק ספרות");
                    Id.requestFocus();
                    return;
                }
                if (!checkId()) {
                    Id.setError("מספר ת.ז הוא לא הגיוני");
                    Id.requestFocus();
                    return;
                } else {
                    if (school.isEmpty()) {
                        School.setError("נא לבחור בית ספר");
                        School.requestFocus();
                        return;

                    } else {

                        if (((IfAdmin==false)&&(IfGuard==false)&&(!bo)&&(!Pattern.matches("[א-י]+", spinc.getSelectedItem().toString())))) {
                                Toast.makeText(MainActivity.this, "נא לבחור כיתה", Toast.LENGTH_SHORT).show();
                                spinc.requestFocus();
                                return;
                        }
                        else if ((IfAdmin==false)&&(IfGuard==false)&&(!bo)&&(!Pattern.matches("[0-9]+", spinn.getSelectedItem().toString()))) {
                            Toast.makeText(MainActivity.this, "נא לבחור מספר כיתה", Toast.LENGTH_SHORT).show();
                            spinn.requestFocus();
                            return;
                        } else {

                            if (pass.isEmpty()) {
                                Password.setError("סיסמה היא חיונית");
                                Password.requestFocus();
                                return;
                            }
                            if (!pass.equals(vpass)) {
                                Vpass.setError("סיסמה לא זהה");
                                Id.requestFocus();
                                return;
                            } else {
                                if (pass.equals("12358")) {
                                    IfAdmin = true;
                                }
                                if (pass.equals("1618")) {
                                    IfGuard = true;
                                }


                            }
                        }
                    }

                }
            }
        }



      PhoneAuthentication();




    }

    private boolean checkId() {//   A method that checks for a legal id -ID in israel requires to multiply every digit in the  odd place by 1 and a digit in a
        //even place by 2 and sum up the result (if we get result that higher than 9 we need to sum those digits  and we use the given result instead)
        //after we got all those results from each digit in the id code - we sum up all the results and the given Result should be devided by 10.
        //That's what this method does
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
            Toast.makeText(getApplicationContext(), " לא הזנת מספר טלפון", Toast.LENGTH_SHORT).show();

        } else {
            if (Phone.length() < 10) {
                Toast.makeText(getApplicationContext(), "מספר טלפון שגוי", Toast.LENGTH_SHORT).show();
            } else {

                Phone = PhoneNum.getText().toString();


                Phone = "+972" + Phone;


                    checkIfSchoolExists();
                }

            }
        }


    private void checkIfSchoolExists() {
            refSchool.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(school)){
                        CheckIfPhoneExists();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"בית הספר שהוזן אינו במערכת",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


    }

    public  void CheckIfPhoneExists() {//This method checks under each type of user in the firebase if the typed phone number has already signed in
        refSchool.child(school).child("Student").orderByChild("phone").equalTo(Phone).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Toast.makeText(getApplicationContext(), "כבר קיים משתמש עם מספר טלפון זהה", Toast.LENGTH_SHORT).show();

                } else {
                    refSchool.child(school).child("Teacher").orderByChild("phone").equalTo(Phone).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                Toast.makeText(getApplicationContext(), "כבר קיים משתמש עם מספר טלפון זהה", Toast.LENGTH_SHORT).show();

                            } else {
                                refSchool.child(school).child("Admin").orderByChild("phone").equalTo(Phone).addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            Toast.makeText(getApplicationContext(), "כבר קיים משתמש עם מספר טלפון זהה", Toast.LENGTH_SHORT).show();

                                        }
                                        else {
                                            refSchool.child(school).child("Guard").orderByChild("phone").equalTo(Phone).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.getValue() != null) {
                                                        Toast.makeText(getApplicationContext(), "כבר קיים משתמש עם מספר טלפון זהה", Toast.LENGTH_SHORT).show();

                                                    }
                                                    else{
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
            Toast.makeText(getApplicationContext(), "הקוד נשלח אליך ברגעים אלו...", Toast.LENGTH_SHORT).show();
            SignInWithPhoneAuthCredential(phoneAuthCredential);
        }


        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), "הרשמה נכשלה", Toast.LENGTH_SHORT).show();


        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            //gets the code string and triggeres a method called verify
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText(getApplicationContext(), "הקוד נשלח...", Toast.LENGTH_SHORT).show();
            codeSent = s;
            verify();
        }


    };


    private void ChangeView1() {// sets the code verification view
        PhoneNum.setVisibility(View.GONE);
        Password.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);
        btn.setVisibility(View.GONE);
        Vpass.setVisibility(View.GONE);
        FirstName.setVisibility(View.GONE);
        SecondName.setVisibility(View.GONE);
        School.setVisibility(View.GONE);
        Id.setVisibility(View.GONE);
        spinn.setVisibility(View.GONE);
        spinc.setVisibility(View.GONE);
        Vpass.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);
        sclass.setVisibility(View.GONE);
        snum.setVisibility(View.GONE);


        et.setVisibility(View.VISIBLE);
        bt.setVisibility(View.VISIBLE);


    }


    private void ChangeView2() {// sets the view to the original
        PhoneNum.setVisibility(View.VISIBLE);
        Password.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);
        btn.setVisibility(View.VISIBLE);
        FirstName.setVisibility(View.VISIBLE);
        SecondName.setVisibility(View.VISIBLE);
        School.setVisibility(View.VISIBLE);
        Id.setVisibility(View.VISIBLE);
        spinn.setVisibility(View.VISIBLE);
        spinc.setVisibility(View.VISIBLE);
        Vpass.setVisibility(View.VISIBLE);
        sclass.setVisibility(View.VISIBLE);
        snum.setVisibility(View.VISIBLE);

        et.setVisibility(View.GONE);
        bt.setVisibility(View.GONE);


        IfAdmin = false;
        IfGuard=false;



    }


    private void SignInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {//פעולה הרושמת את המשתמש החדש ל-firebase ובודקת אם הוא תלמיד מורה אדמין או שומר
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {




                            if (IfAdmin) {
                                Admin admin = new Admin(firstname, secondname, id, school, Phone, pass);
                                refSchool.child(school).child("Admin").child(Phone).setValue(admin).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(MainActivity.this, "הנך משתמש חדש עכשיו!", Toast.LENGTH_LONG).show();
                                    }
                                });
                                IfAdmin = false;
                            }
                            else
                             if(IfGuard){
                                Guard guard=new Guard(firstname, secondname, id, school, Phone, pass);
                                refSchool.child(school).child("Guard").child(Phone).setValue(guard).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(MainActivity.this, "הנך משתמש חדש עכשיו!", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "הנך משתמש חדש עכשיו!", Toast.LENGTH_LONG).show();
                                if (!bo) {

                                    Student student = new Student(firstname, secondname, id, school, ClassAndNumber, Phone, pass);
                                    refSchool.child(school).child("Student").child(Phone).setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(MainActivity.this, "הנך משתמש חדש עכשיו!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    Teacher teacher = new Teacher(firstname, secondname, id, school, ClassAndNumber, Phone, pass);
                                    refSchool.child(school).child("Teacher").child(Phone).setValue(teacher).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(MainActivity.this, "הנך משתמש חדש עכשיו!", Toast.LENGTH_LONG).show();
                                        }


                                    });

                                }
                            }

                        }
                        moveActivity();




                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(MainActivity.this, "הקוד שהוזן אינו תואם", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

    }

    private void moveActivity() {
        finish();
        Intent intento = new Intent(this, LoginScreen.class);
        startActivity(intento);
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





}
