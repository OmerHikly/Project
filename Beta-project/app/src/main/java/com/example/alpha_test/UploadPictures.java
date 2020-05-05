package com.example.alpha_test;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadPictures extends AppCompatActivity {
    ImageView img;
    Button ch;
    Context ctx=this;


    StorageReference mStorageRef;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;

    public Uri imguri;



    public static final int IMAGE_PICK_CODE=1000;
    public static final int PERMISSION_CODE=1001;

    public static StorageReference Ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pictures);

        mStorageRef = FirebaseStorage.getInstance().getReference();


        img = findViewById(R.id.iv);
        ch = findViewById(R.id.Chs);



    }

    public void FileChooser(View view) {//Check the permission and the sdk number of the device
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
    private void pickFromGallery() {//Choose the image,saves the image you clicked on
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

    private String getExtension(Uri uri){//This method gets the extension of the file that have been picked (png,jpg,jpeg)
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }



    public void upload() {//This method doing the service action of uploading the file.
        Toast.makeText(this,"We are uploading your file...",Toast.LENGTH_SHORT).show();
        Ref=mStorageRef.child("Images").child(System.currentTimeMillis()+"."+getExtension(imguri));//
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
                Picasso.with(ctx).load(uri).fit().centerCrop().into(img);
            }


    }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();

            }
        });

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

