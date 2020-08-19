package com.example.coronacheckcustomer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class FBref {

    public static FirebaseAuth refAuth= FirebaseAuth.getInstance();

    public static FirebaseDatabase refDb = FirebaseDatabase.getInstance();

    public static DatabaseReference refSchool=FirebaseDatabase.getInstance().getReference().child("Schools");

    public static DatabaseReference refBusinesses=FirebaseDatabase.getInstance().getReference().child("Businesses");

    public static DatabaseReference refUsers=FirebaseDatabase.getInstance().getReference().child("Users");



}
