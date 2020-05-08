package com.example.safeentrance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;

import static com.example.safeentrance.FBref.refSchool;

public class LoginScreen extends AppCompatActivity {
    EditText Phone, Password;//פרטים להזנה טלפון ומספר
    Toolbar toolbar;

    AutoCompleteTextView School;//בית ספר (בעת הקלדה משלים את החיפוש)

    Student student;// עצם מסוג תלמיד
    Teacher teacher;// עצם מסוג מורה


    Boolean firstrun, stayConnect;


    String typedpass;
    String school, phone;

    int sender = -1;
    ArrayList<String> Schools = new ArrayList<>();//רשימה של בתי ספר שיש ב-firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        Phone = findViewById(R.id.Phone);
        Password = findViewById(R.id.Password);
        School=findViewById(R.id.LSchool);


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("התחברות");
        setSupportActionBar(toolbar);


        FirebaseSchools();//פעולה שמוסיפה את כל האפשרויות


        stayConnect = false;
    }

    private void FirebaseSchools() {

        refSchool.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Schools.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {//checks how many main children there are in the firebase(Schools number)
                    String st = dsp.getKey();
                    Schools.add(st);

                }


                Adapt(Schools);//This method sets the adpater between the array we just created and the "edit text" of the school.
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });


    }

    private void Adapt(ArrayList<String> arrayList) {//פעולה שמקשרת בין המערך הרשימתי שמכיל את בתי הספר לרשימה הנגללת
        SchoolAdapter adapter = new SchoolAdapter(this, arrayList);
        School.setAdapter(adapter);


    }



    @Override
    protected void onStart() {
        SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        Boolean isNotSigned = settings.getBoolean("NSigned", true);

        if (isNotSigned) {//show start activity
            finish();
            startActivity(new Intent(LoginScreen.this, MainActivity.class));
        }

        super.onStart();
    }

    public void login(View view) {//פעולה בעת לחיצה על התחבר מקבלת לString את מה שהוקלד
        phone = "+972" + Phone.getText().toString();
        typedpass = Password.getText().toString();
        school = School.getText().toString();
        checkIfSchoolExists();//פעולה הבודקת אם הבית ספר שהוקלד קיים


    }






    private void checkIfSchoolExists() {//במידה ובית הספר לא קיים המערכת מודיעה למשתמש
        refSchool.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (school == "" || school == null|| school.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "נא להזין את שם בית הספר", Toast.LENGTH_SHORT).show();

                } else {
                    if (dataSnapshot.hasChild(school)) {
                        CheckIfPhoneExists();//פעולה הבודקת אם הטלפון שהוקלד קיים באותו בית ספר
                    } else {
                        Toast.makeText(getApplicationContext(), "בית הספר שהוזן אינו קיים במערכת", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    public  void CheckIfPhoneExists() {//This method checks under each type of user in the firebase if the typed phone number already as signed in
        String ph=Phone.getText().toString();
        if (ph.isEmpty()||ph == "" || ph == null ) {
            Toast.makeText(getApplicationContext(), "נא להזין מס' טלפון", Toast.LENGTH_SHORT).show();

        } else {
            refSchool.child(school).child("Student").orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        StuActivity();
                    } else {
                        refSchool.child(school).child("Teacher").orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    TeachActivity();
                                }
                                    else{
                                        NoUser();//במידה ולא קיים משתמש לא תחת עץ שומר עץ מורה עץ תלמיד או עץ אדמין יש פעולה מתאימה לכך שתתריע את המשתמש על כך
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

    private void NoUser() {
        Toast.makeText(getApplicationContext(),"המשתמש אינו קיים",Toast.LENGTH_SHORT).show();

    }



    private void StuActivity() {
        refSchool.child(school).child("Student").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String pass = dataSnapshot.child("password").getValue().toString();
                if (typedpass.equals(pass)) {
                    student = dataSnapshot.getValue(Student.class);
                    if (student.isActivated() == false) {
                        sender=0;
                        NotApproved();
                    } else {
                        StudentScreen();//פעולה שמעבירה את המשתמש למסך הבא בהתאם לסוג המשתמש (תלמיד)
                    }
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

    private void TeachActivity() {
        refSchool.child(school).child("Teacher").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String pass = dataSnapshot.child("password").getValue().toString();
                if (typedpass.equals(pass)) {
                    teacher = dataSnapshot.getValue(Teacher.class);
                    if (teacher.isActivated() == false) {
                        sender=1;
                        NotApproved();
                    } else {
                        TeacherScreen();//פעולה שמעבירה את המשתמש למסך הבא בהתאם לסוג המשתמש (מורה)
                    }
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

    private void TeacherScreen() {
    /*    Intent i=new Intent(this,TeacherLogin.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);

     */
    }


    private void NotApproved() {
        Intent i=new Intent(this,NotActivated.class );
        i.putExtra("Type",sender);
        startActivity(i);
    }
    private void StudentScreen() {
        Intent i=new Intent(this,StudentLogin.class);
        Parcelable parcelable= Parcels.wrap(student);
        i.putExtra("student", parcelable);
        startActivity(i);
    }

    }

