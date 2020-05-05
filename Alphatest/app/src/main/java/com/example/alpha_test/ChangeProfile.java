package com.example.alpha_test;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import static com.example.alpha_test.FirebaseHelper.refSchool;

public class ChangeProfile extends AppCompatActivity {
    TextView Name,Class,Educator,Id,Phone;
    ImageView Iv;
    Button chg;

    Context ctx=this;

    Student student;
    Teacher teacher;
    Guard guard;
    Admin admin;


    String phone,cls,educator,id,school,name;


    StorageReference mStorageRef;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;

    public Uri imguri;

    int user;

    public static final int IMAGE_PICK_CODE=1000;
    public static final int PERMISSION_CODE=1001;

    public static StorageReference Ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);
        Iv=findViewById(R.id.Passport);
        Name=findViewById(R.id.UserName);
        Class=findViewById(R.id.UserClass);
        Educator=findViewById(R.id.Educator);
        Id=findViewById(R.id.ID);
        Phone=findViewById(R.id.UserPhone);
        chg=findViewById(R.id.ChangeProfile);

        mStorageRef = FirebaseStorage.getInstance().getReference();


        Intent gi=getIntent();
        user=gi.getIntExtra("type",-1);
        switch (user){
            case 0:
                Parcelable parcelable1=getIntent().getParcelableExtra("student");
                student= Parcels.unwrap(parcelable1);
                school=student.getSchool();
                phone=student.getPhone();
                id = student.getId();
                name = student.getName()+" "+student.getSecondName();
                cls=student.getCls();
                Ref=mStorageRef.child("Students").child(phone+"Profile");
                findEducator();
                break;


            case 1:
                Parcelable parcelable2=getIntent().getParcelableExtra("teacher");
                teacher= Parcels.unwrap(parcelable2);
                school=teacher.getSchool();
                phone=teacher.getPhone();
                id = teacher.getId();
                name = teacher.getName()+" "+teacher.getSecondName();
                cls=teacher.getCls();
                Ref=mStorageRef.child("Teachers").child(phone+"Profile");
                Educator.setVisibility(View.GONE);
                break;

            case 2:
                Parcelable parcelable3=getIntent().getParcelableExtra("guard");
                guard= Parcels.unwrap(parcelable3);
                school=guard.getSchool();
                phone=guard.getPhone();
                id = guard.getId();
                name = guard.getName()+" "+guard.getSecondName();
                Ref=mStorageRef.child("Guards").child(phone+"Profile");
                Educator.setVisibility(View.GONE);
                Class.setVisibility(View.GONE);
                break;

            case 3:
                Parcelable parcelable=getIntent().getParcelableExtra("admin");
                admin= Parcels.unwrap(parcelable);
                school=admin.getSchool();
                phone=admin.getPhone();
                id = admin.getID();
                name = admin.getName()+" "+admin.getSecondName();
                Ref=mStorageRef.child("Admins").child(phone+"Profile");
                Educator.setVisibility(View.GONE);
                Class.setVisibility(View.GONE);
                break;

        }





        DownloadImg();


        Name.setText(Name.getText().toString()+" "+name);
        Class.setText(Class.getText().toString()+" "+cls);
        Id.setText(Id.getText().toString()+" "+id);
        Phone.setText(Phone.getText().toString()+" "+phone);

    }


    private void findEducator() {
        refSchool.child(school).child("Teacher").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dsp:dataSnapshot.getChildren()){
                    if(dsp.child("cls").getValue().equals(cls)) {

                    educator=dsp.child("name").getValue().toString()+" "+dsp.child("secondName").getValue().toString();

                }
                Educator.setText(Educator.getText().toString()+" "+educator);

            }

        }


    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});

    }

    public void Change(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions,PERMISSION_CODE);
            } else {
                pickFromGallery();
            }


        } else {
            pickFromGallery();

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    pickFromGallery();
                }
                else{
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void pickFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_PICK_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {//checking that an image have picked and that the image url and data is fine
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imguri = data.getData();
            upload();

        }
    }





    public void upload() {//This method doing the service action of uploading the file.
        Toast.makeText(this,"We are uploading your file...",Toast.LENGTH_SHORT).show();
        //the line above keeps the extension of the file and name it with his millis since the the UNIX epoch: (1970-01-01 00:00:00 UTC) a date
        // That makes sure that the first file that was uploaded will always remain the first and won't mix by the firebase order
        uploadTask=Ref.putFile(imguri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {//If the reference is right do:
                        // Get a URL to the uploaded content
                        Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                        DownloadImg();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {//if the reference is wrong:
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    private void DownloadImg() {// a method that downloads the url of the last added image
        Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(ctx).load(uri).centerCrop().fit().into(Iv);
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "הורדת התמונה נכשלה", Toast.LENGTH_SHORT).show();

            }
        });

    }

}
