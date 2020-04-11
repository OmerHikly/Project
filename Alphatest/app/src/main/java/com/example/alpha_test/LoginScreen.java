package com.example.alpha_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;

import static com.example.alpha_test.FirebaseHelper.mAuth;
import static com.example.alpha_test.FirebaseHelper.refSchool;

public class LoginScreen extends AppCompatActivity {
    EditText Phone, Password;//פרטים להזנה טלפון ומספר
    Toolbar toolbar;

    AutoCompleteTextView School;//בית ספר (בעת הקלדה משלים את החיפוש)

    Student student;// עצם מסוג תלמיד
    Guard guard;// עצם מסוג שומר
    Admin admin;// עצם מסוג אדמין
    Teacher teacher;// עצם מסוג מורה


    Boolean firstrun, stayConnect;


    String typedpass;
    String  school, phone;

    int sender=-1;
    ArrayList<String> Schools = new ArrayList<>();//רשימה של בתי ספר שיש ב-firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        Phone = findViewById(R.id.Phone);
        Password = findViewById(R.id.Password);
        School=findViewById(R.id.LSchool);

      toolbar=findViewById(R.id.toolbar);
      toolbar.setTitle("התחברות");

       setSupportActionBar(toolbar);





        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            //show start activity

            startActivity(new Intent(LoginScreen.this, MainActivity.class));
            Toast.makeText(LoginScreen.this, "First Run", Toast.LENGTH_LONG)
                    .show();
        }

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();


        FirebaseSchools();//פעולה שמוסיפה את כל האפשרויות


        stayConnect = false;


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

    private void Adapt(ArrayList<String> arrayList ) {//פעולה שמקשרת בין המערך הרשימתי שמכיל את בתי הספר לרשימה הנגללת
         SchoolAdapter adapter=new SchoolAdapter(this, arrayList);
        School.setAdapter(adapter);



    }


    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        Boolean isChecked = settings.getBoolean("stayConnect", false);
        if (mAuth.getCurrentUser() != null && isChecked) {
            stayConnect = true;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (stayConnect) finish();
    }


    public void login(View view) {//פעולה בעת לחיצה על התחבר מקבלת לString את מה שהוקלד
        phone = "+972" + Phone.getText().toString();
        typedpass = Password.getText().toString();
        school=School.getText().toString();
        checkIfSchoolExists();//פעולה הבודקת אם הבית ספר שהוקלד קיים




    }



    private void checkIfSchoolExists() {//במידה ובית הספר לא קיים המערכת מודיעה למשתמש
        refSchool.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (school == "" || school == null|| school.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter school name or code", Toast.LENGTH_SHORT).show();

                } else {
                    if (dataSnapshot.hasChild(school)) {
                        CheckIfPhoneExists();//פעולה הבודקת אם הטלפון שהוקלד קיים באותו בית ספר
                    } else {
                        Toast.makeText(getApplicationContext(), "School isn't exist", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), "Please enter phone number", Toast.LENGTH_SHORT).show();

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
                                        Toast.makeText(getApplicationContext(), "Teacher", Toast.LENGTH_SHORT).show();
                                        TeachActivity();

                                    } else {
                                        refSchool.child(school).child("Admin").orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {

                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getValue() != null) {
                                                    AdActivity();
                                                } else {
                                                    refSchool.child(school).child("Guard").orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.getValue() != null) {
                                                                GuardActivity();
                                                            } else {
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



    private void StuActivity() {//password confirmation
        Toast.makeText(getApplicationContext(),"Student login",Toast.LENGTH_SHORT).show();
        refSchool.child(school).child("Student").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String pass = dataSnapshot.child("password").getValue().toString();
                if (typedpass.equals(pass)) {
                    student = dataSnapshot.getValue(Student.class);
                    if (student.getActivated() == false) {
                        sender=0;
                        NotApproved();
                    } else {
                        StudentScreen();//פעולה שמעבירה את המשתמש למסך הבא בהתאם לסוג המשתמש (תלמיד)
                    }
                }
                else{
                    Password.setError("Wrong password");
                    Password.requestFocus();
                    return;

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void TeachActivity() {//password confirmation
        Toast.makeText(getApplicationContext(), "Teacher login", Toast.LENGTH_SHORT).show();
        refSchool.child(school).child("Teacher").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String pass = dataSnapshot.child("password").getValue().toString();
                if (typedpass.equals(pass)) {
                    teacher = dataSnapshot.getValue(Teacher.class);
                    if (teacher.getActivated() == false) {
                        sender=1;
                            NotApproved();
                    } else {
                        TeacherScreen();//פעולה שמעבירה את המשתמש למסך הבא בהתאם לסוג המשתמש (מורה)
                    }
                }
                else{
                    Password.setError("Wrong password");
                    Password.requestFocus();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void AdActivity() {//password confirmation
        Toast.makeText(getApplicationContext(),"Admin login",Toast.LENGTH_SHORT).show();

        refSchool.child(school).child("Admin").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String pass = dataSnapshot.child("password").getValue().toString();
                    if (typedpass.equals(pass)) {
                        admin = dataSnapshot.getValue(Admin.class);
                        if (admin.getActivated() == false) {
                            sender=2;
                            NotApproved();
                        } else {
                            AdminScreen();//פעולה שמעבירה את המשתמש למסך הבא בהתאם לסוג המשתמש (אדמין)
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Password is wrong",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
   }
            });



            //login successful

        }
    private void GuardActivity() {
        Toast.makeText(getApplicationContext(),"Guard login",Toast.LENGTH_SHORT).show();

        refSchool.child(school).child("Guard").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String pass = dataSnapshot.child("password").getValue().toString();
                if (typedpass.equals(pass)) {
                    guard = dataSnapshot.getValue(Guard.class);
                    if (guard.getActivated() == false) {
                        sender=3;
                        NotApproved();
                    } else {
                        GuardScreen();//פעולה שמעבירה את המשתמש למסך הבא בהתאם לסוג המשתמש (שומר)
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Password is wrong",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




    private void NoUser() {//מתריעה למשתמש אם הפרטים שלו קיימים או לא
        Toast.makeText(getApplicationContext(),"User isn't exists",Toast.LENGTH_SHORT).show();

    }

    private void NotApproved() {
        Intent i=new Intent(this,NotActivated.class );
        i.putExtra("Type",sender);
        startActivity(i);
    }
    //what happens when log in is successful
    private void GuardScreen(){//העברה למסך כניסה של שומר
        Intent i=new Intent(this,GuardLogin.class);
        Parcelable parcelable= Parcels.wrap(guard);
        i.putExtra("guard", parcelable);
        startActivity(i);

    }


    private void AdminScreen() {//העברה למסך כניסה של אדמין
        Intent i=new Intent(this,AdminLogin.class);
        Parcelable parcelable= Parcels.wrap(admin);
        i.putExtra("admin", parcelable);
        startActivity(i);
    }

    private void TeacherScreen() {//העברה למסך כניסה של מורה
        Intent i=new Intent(this,TeacherLogin.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);
    }

    private void StudentScreen() {//העברה למסך כניסה של תלמיד
        Intent i=new Intent(this,StudentLogin.class);
        Parcelable parcelable= Parcels.wrap(student);
        i.putExtra("student", parcelable);
        startActivity(i);
    }



}
