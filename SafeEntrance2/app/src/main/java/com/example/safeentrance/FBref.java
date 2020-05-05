package com.example.safeentrance;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FBref {

public static FirebaseAuth refAuth= FirebaseAuth.getInstance();

public static FirebaseDatabase refDb = FirebaseDatabase.getInstance();

public static DatabaseReference refSchool=FirebaseDatabase.getInstance().getReference();


 }
