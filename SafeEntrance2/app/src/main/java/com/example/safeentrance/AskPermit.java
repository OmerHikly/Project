package com.example.safeentrance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.parceler.Parcels;

import static com.example.safeentrance.FBref.refSchool;


public class AskPermit extends AppCompatActivity {
    Toolbar toolbar;
    Spinner ChooseParent;//משמש לבחירת הורה

    Student student;

    public static StorageReference Ref;
    StorageReference mStorageRef;

    String school, Phone;

    boolean proceed = false;//משתנה בוליאני לזיהוי בחירת תמונה
    boolean whichParrent;

    Parent p1, p2;

    String Unique;//מחרוזת שתשמור את שם קיצור קובץ התמונה
    String permit = "no";


    public Uri imguri;

    private StorageTask<UploadTask.TaskSnapshot> uploadTask;

    public static final int IMAGE_PICK_CODE = 1000;
    public static final int PERMISSION_CODE = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_permit);

        ChooseParent = findViewById(R.id.sp);

        toolbar = findViewById(R.id.tb);
        toolbar.setTitle("מילוי בקשה");
        setSupportActionBar(toolbar);

        Parcelable parcelable = getIntent().getParcelableExtra("student");
        student = Parcels.unwrap(parcelable);

        school = student.getSchool();
        Phone = student.getPhone();


        mStorageRef = FirebaseStorage.getInstance().getReference();


        p1 = student.getParent1();//קבלת עצמי הורים
        p2 = student.getParent2();

        String[] arraySpinner = new String[3];
        arraySpinner[0] = "";
        arraySpinner[1] = p1.getName();

        if (p2 != null) {
            arraySpinner[2] = p2.getName();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ChooseParent.setAdapter(adapter);
        //Toast.makeText(getApplicationContext(),, Toast.LENGTH_LONG).show();
    }


    public void ParentsApproval(View view) {
        Unique = System.currentTimeMillis() + "_" + student.getPhone();
        Ref = mStorageRef.child("ParentsPermit").child(Unique);
        pickFromGallery();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), "תמונה נבחרה בהצלחה", Toast.LENGTH_SHORT).show();
            proceed = true;
        }
    }


    public void upload() {//This method doing the service action of uploading the file.
        //the line above keeps the extension of the file and name it with his millis since the the UNIX epoch: (1970-01-01 00:00:00 UTC) a date
        // That makes sure that the first file that was uploaded will always remain the first and won't mix by the firebase order
        uploadTask = Ref.putFile(imguri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {//If the reference is right do:
                        // Get a URL to the uploaded content
                        permit = Ref.toString();

                    }

                })
                .addOnFailureListener(new OnFailureListener() {//if the reference is wrong:
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Toast.makeText(getApplicationContext(), "משהו לא עבד כשורה בעת העלאת התמונה... נסה שוב", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void RequestBarcode(View view) {
        if (proceed) {
            upload();
            String choosen = (String) ChooseParent.getSelectedItem();
            if (choosen == "") {
                Toast.makeText(getApplicationContext(), "נא לבחור הורה מאשר", Toast.LENGTH_SHORT).show();
            } else {
                if(choosen==p1.getName()){
                    whichParrent=true;
                }
                else{
                    whichParrent=false;
                }
                long d =System.currentTimeMillis();
                String t= String.valueOf(d);
                String Qr_Info=t+"; "+Unique+"; "+whichParrent;
                ParentsPermit p;
                p=new ParentsPermit(t,Unique,Qr_Info,whichParrent);
                refSchool.child(school).child("Student").child(student.getPhone()).child("Permit").setValue(p);

                Toast.makeText(getApplicationContext(),"אישור הורים נוצר!",Toast.LENGTH_SHORT).show();

                Return();

            }
        }
        else{
            Toast.makeText(getApplicationContext(), "נא לבחור תמונה מהגלריה", Toast.LENGTH_SHORT).show();

        }
    }

    private void Return() {
        finish();
        Intent intento = new Intent(this, StudentLogin.class);
        Parcelable parcelable = Parcels.wrap(student);
        intento.putExtra("student", parcelable);
        startActivity(intento);
    }
}