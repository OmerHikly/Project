package com.example.alpha_test;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseHelper {
   public static FirebaseAuth mAuth=FirebaseAuth.getInstance();

    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
  public static DatabaseReference refUsers=FBDB.getReference("User");

    public static DatabaseReference refSchool=FirebaseDatabase.getInstance().getReference();


   public static FirebaseStorage storage = FirebaseStorage.getInstance();

   public  static StorageReference storageRef = storage.getReference();
  public  static StorageReference pathReference = storageRef.child("Images/stars.jpg");



}
