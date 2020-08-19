package com.example.coronacheckcustomer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.coronacheckcustomer.FBref.refBusinesses;

public class TryActivity extends AppCompatActivity {

    AutoCompleteTextView WhyTfItIsntWorking;
    ArrayList<String> Businesses = new ArrayList<>();//רשימה של בתי ספר שיש ב-firebase



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try);
        WhyTfItIsntWorking=findViewById(R.id.LBusiness);

        fireBase();
    }

    private void fireBase() {

        refBusinesses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Businesses.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {//checks how many main children there are in the firebase(Schools number)
                    String st = dsp.getKey();
                    Businesses.add(st);

                }


                Adapt(Businesses);//This method sets the adpater between the array we just created and the "edit text" of the business.
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

    }
    private void Adapt(ArrayList<String> arrayList) {//פעולה שמקשרת בין המערך הרשימתי שמכיל את בתי הספר לרשימה הנגללת
        SchoolAdapter adapter = new SchoolAdapter(this, arrayList);
        WhyTfItIsntWorking.setAdapter(adapter);
    }
}
