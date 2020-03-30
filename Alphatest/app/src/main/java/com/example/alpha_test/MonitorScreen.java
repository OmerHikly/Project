package com.example.alpha_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.alpha_test.FirebaseHelper.refSchool;

public class MonitorScreen extends AppCompatActivity {

    ListView lv;

    String school;

    ArrayList<String> outs=new ArrayList<>();
    ArrayList<String> keys=new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_screen);
        lv=findViewById(R.id.Outs);
        Intent gi=getIntent();
        school=gi.getStringExtra("school");

        final Intent i=new Intent(this,SpecificOut.class);//יצירת רכיב שיעביר את המורה בשעת הצורך אל המסך שמכיל את נתוני הקבוצה ואפשרות לשנות אותה


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//מאזין ללחיצה על אחד מפרטי הקבוצה
//בעת לחיצה על אחת מהקבוצות ייהיה מעבר למסך עם נתוני הקבוצה המתאימה שנבחרה

                String out=outs.get(position);//קבלת שם הקבוצה
                i.putExtra("school",school);
                i.putExtra("key",keys.get(position));//הוספת שם הקבוצה לרכיב שיעביר את המורה למסך עם נתוני הקבוצה שנבחרה
                startActivity(i);
            }
        });

        SetList();
    }

    private void SetList() {
        refSchool.child(school).child("AAMonitor").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dsp:dataSnapshot.getChildren()){
                    String d=dsp.getValue().toString();
                    String Splitted[]=d.split("; ");
                    String name=Splitted[0];
                    String TimeAndDate=Splitted[8];
                    String out=name+" "+TimeAndDate;
                    outs.add(out);
                    keys.add(dsp.getKey());



                }
                Adapt();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void Adapt() {// פעולת הקישור בין המתאם שעוצב עבור המסך הזה אל הרשימה הנגללת (listview)
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, outs);
        lv.setAdapter(itemsAdapter);
    }


}
