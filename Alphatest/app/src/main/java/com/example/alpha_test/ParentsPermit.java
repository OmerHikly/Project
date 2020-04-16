package com.example.alpha_test;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ParentsPermit extends AppCompatActivity {
    SubsamplingScaleImageView subsamplingScaleImageView;
    ImageView iv;
    Toolbar toolbar;

    StorageReference mStorageRef;
   public static StorageReference Refp;

    String fillref;

    Context ctx=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents_permit);
        subsamplingScaleImageView=findViewById(R.id.Zoom);
        iv=findViewById(R.id.ImageView);
        toolbar=findViewById(R.id.tb);

        toolbar.setTitle("אישור הורים");
        setSupportActionBar(toolbar);

        Intent gi=getIntent();
        fillref=gi.getStringExtra("refrence");


        mStorageRef = FirebaseStorage.getInstance().getReference();
       Refp = mStorageRef.child("ParentsPermit").child(fillref);
       DownloadPermitImg();



    }

    private void DownloadPermitImg() {
        Refp.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(ctx).load(uri).fit().centerCrop().into(iv);
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               Toast.makeText(getApplicationContext(), "לא צורף אישור הורים", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
