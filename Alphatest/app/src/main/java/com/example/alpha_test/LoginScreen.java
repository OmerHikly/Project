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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.alpha_test.FirebaseHelper.mAuth;
import static com.example.alpha_test.FirebaseHelper.refSchool;

public class LoginScreen extends AppCompatActivity {
    EditText Phone, Password;
    AutoCompleteTextView School;

    Student student;
    Guard guard;
    Admin admin;
    Teacher teacher;



    String typedpass;
    Boolean firstrun, stayConnect;
    String name, secondname, id, school, phone, password, uid, cls;


    String[] Schools;
    ArrayList<String> arrayList=new ArrayList<String>();
    int level, x = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        Phone = findViewById(R.id.Phone);
        Password = findViewById(R.id.Password);
        School=findViewById(R.id.LSchool);



        FirebaseSchools();


        stayConnect = false;

        SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        firstrun = settings.getBoolean("firstRun", false);
        if (firstrun) {
            signup();
        }
    }

    private void FirebaseSchools() {

        refSchool.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                int index = 0;
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {//checks how many main children there are in the firebase(Schools number)
                    count++;
                }
                Schools = new String[count];
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    //Toast.makeText(getApplicationContext(), dsp.getKey(), Toast.LENGTH_LONG).show();
                    Schools[index] = dsp.getKey().toString();
                    index++;
                }

                Adapt(Schools);//This method sets the adpater between the array we just created and the "edit text" of the school.
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });


    }

    private void Adapt(String[] array) {
      //  Toast.makeText(getApplicationContext(), array[0] + array[1] + array[2] + array[3], Toast.LENGTH_SHORT).show();
        arrayList.addAll(Arrays.asList(Schools));
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
            Intent si = new Intent(this, logined.class);
            startActivity(si);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (stayConnect) finish();
    }

    public void SignUp(View view) {
        signup();
    }

    private void signup() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("n", true);
        startActivity(intent);
    }

    public void login(View view) {
        phone = "+972" + Phone.getText().toString();
        typedpass = Password.getText().toString();
        school=School.getText().toString();
        checkIfSchoolExists();




    }



    private void checkIfSchoolExists() {
        refSchool.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (school == "" || school == null|| school.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter school name or code", Toast.LENGTH_SHORT).show();

                } else {
                    if (dataSnapshot.hasChild(school)) {
                        Toast.makeText(getApplicationContext(), "School Exists", Toast.LENGTH_SHORT).show();
                        CheckIfPhoneExists();
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
                                                                NoUser();
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
                    student=dataSnapshot.getValue(Student.class);
                    StudentScreen();
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
                    teacher=dataSnapshot.getValue(Teacher.class);
                    TeacherScreen();
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
                        admin=dataSnapshot.getValue(Admin.class);
                        AdminScreen();
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
                    guard=dataSnapshot.getValue(Guard.class);
                    GuardScreen();
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




    private void NoUser() {
        Toast.makeText(getApplicationContext(),"User isn't exists",Toast.LENGTH_SHORT).show();

    }

    private void GuardScreen() {
        Intent i=new Intent(this,GuardLogin.class);
        Parcelable parcelable= Parcels.wrap(guard);
        i.putExtra("guard", parcelable);       startActivity(i);

    }


    private void AdminScreen() {
        Intent i=new Intent(this,AdminLogin.class);
        Parcelable parcelable= Parcels.wrap(admin);
        i.putExtra("admin", parcelable);
        startActivity(i);
    }

    private void TeacherScreen() {
        Intent i=new Intent(this,TeacherLogin.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);
    }

    private void StudentScreen() {//what happens when log in is successful
        Intent i=new Intent(this,StudentLogin.class);
        Parcelable parcelable= Parcels.wrap(student);
        i.putExtra("student", parcelable);
        startActivity(i);
    }











    private void log() {
        Intent i=new Intent(this,logined.class);
        startActivity(i);
    }
}
