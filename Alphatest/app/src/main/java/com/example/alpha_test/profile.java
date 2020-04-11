package com.example.alpha_test;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import static com.example.alpha_test.FirebaseHelper.refSchool;

public class profile extends AppCompatActivity {

    Button Chgp, Remu;
    TextView Name, Class, Educator, Id, Phone, Password;
    ImageView Iv;
    EditText NewPass;

    Context ctx = this;
    StorageReference mStorageRef;
    public Uri imguri;

    private StorageTask<UploadTask.TaskSnapshot> uploadTask;


    public static final int IMAGE_PICK_CODE = 1000;
    public static final int PERMISSION_CODE = 1001;

    public static StorageReference Ref;
    public static DatabaseReference RefUser;

    Guard guard;
    Student student;
    Teacher teacher;

    String newpass;
    String Watcherphone;
    String Watchedphone, cls, educator, id, school, name, pass;
    int UserType, WatchedUserType;
    boolean click = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Iv = findViewById(R.id.Passport);
        Name = findViewById(R.id.UserName);
        Class = findViewById(R.id.UserClass);
        Educator = findViewById(R.id.Educator);
        Id = findViewById(R.id.ID);
        Phone = findViewById(R.id.UserPhone);
        Password = findViewById(R.id.UserPassword);

        Chgp = findViewById(R.id.ChangePass);
        Remu = findViewById(R.id.RemoveAcc);

        NewPass = findViewById(R.id.NewPassword);


        mStorageRef = FirebaseStorage.getInstance().getReference();


        Intent gi = getIntent();
        Watcherphone = gi.getStringExtra("tphone");
        school = gi.getStringExtra("sc");
        UserType = gi.getIntExtra("type", -1);
        WatchedUserType = gi.getIntExtra("WatchedUserType", -1);


        if (UserType == 3) {
            Chgp.setVisibility(View.VISIBLE);
            Remu.setVisibility(View.VISIBLE);
        }


        switch (WatchedUserType) {
            case 0:
                Parcelable parcelable1 = getIntent().getParcelableExtra("student");
                student = Parcels.unwrap(parcelable1);
                school = student.getSchool();
                Watchedphone = student.getPhone();
                id = student.getId();
                name = student.getName() + " " + student.getSecondName();
                cls = student.getCls();
                pass = student.getPassword();
                Ref = mStorageRef.child("Students").child(Watchedphone + "Profile");
                RefUser=refSchool.child(school).child("Student").child(Watchedphone);
                findEducator();
                break;

            case 1:
                Parcelable parcelable2 = getIntent().getParcelableExtra("teacher");
                teacher = Parcels.unwrap(parcelable2);
                school = teacher.getSchool();
                Watchedphone =  teacher.getPhone();
                id = teacher.getId();
                name = teacher.getName() + " " + teacher.getSecondName();
                cls = teacher.getCls();
                pass = teacher.getPassword();
                Educator.setVisibility(View.GONE);
                Ref = mStorageRef.child("Teachers").child(Watchedphone + "Profile");
                RefUser=refSchool.child(school).child("Teacher").child(Watchedphone);
                break;

            case 2:
                Parcelable parcelable3 = getIntent().getParcelableExtra("guard");
                guard = Parcels.unwrap(parcelable3);
                school = guard.getSchool();
                Watchedphone = guard.getPhone();
                id = guard.getId();
                name = guard.getName() + " " + guard.getSecondName();
                pass = guard.getPassword();
                Class.setVisibility(View.GONE);
                Educator.setVisibility(View.GONE);
                Ref = mStorageRef.child("Guards").child(Watchedphone + "Profile");
                RefUser=refSchool.child(school).child("Guard").child(Watchedphone);
                break;
        }

        DownloadImg();

        Name.setText(Name.getText().toString() + " " + name);
        Class.setText(Class.getText().toString() + " " + cls);
        Id.setText(Id.getText().toString() + " " + id);
        Phone.setText(Phone.getText().toString() + " " + Watchedphone);
        Password.setText(Password.getText().toString() + pass);


    }


    private void findEducator() {
        refSchool.child(school).child("Teacher").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    if (dsp.child("cls").getValue().equals(cls)) {
                        String edphone = dsp.getKey();

                        if (edphone.equals(Watcherphone)) {
                            educator = "את/ה";
                            Chgp.setVisibility(View.VISIBLE);
                            Remu.setVisibility(View.VISIBLE);

                        } else {
                            educator = dsp.child("name").getValue().toString() + " " + dsp.child("secondName").getValue().toString();

                        }
                        Educator.setText(Educator.getText().toString() + " " + educator);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void DownloadImg() {// a method that downloads the url of the last added image
        Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(ctx).load(uri).fit().centerCrop().into(Iv);
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void SetPass(View view) {
        if (click == false) {
            Remu.setVisibility(View.GONE);
            NewPass.setVisibility(View.VISIBLE);
            Chgp.setText("החל סיסמה");
            click = !click;
        } else {
            newpass = NewPass.getText().toString();
            if (newpass==null||newpass.isEmpty() || newpass.equals(" ") || newpass.equals("")) {
                NewPass.setError("חסרה ססמה");
                NewPass.requestFocus();
                return;
            } else {

            NewPass.setVisibility(View.GONE);
            Remu.setVisibility(View.VISIBLE);


                switch (WatchedUserType) {
                    case 0:
                        refSchool.child(school).child("Student").child(Watchedphone).child("password").setValue(newpass);
                        student.setPassword(newpass);
                        break;

                    case 1:
                        refSchool.child(school).child("Teacher").child(Watchedphone).child("password").setValue(newpass);
                        teacher.setPassword(newpass);
                        break;
                    case 2:
                        refSchool.child(school).child("Guard").child(Watchedphone).child("password").setValue(newpass);
                        guard.setPassword(newpass);
                        break;


                }
                Password.setText("סיסמה: " + newpass);

            }
            Chgp.setText("שינוי סיסמה");
            newpass = null;
            click = !click;
        }




    }

    public void RemoveUser(View view) {
        RefUser.removeValue();
    }
}