package com.example.alpha_test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;

import static com.example.alpha_test.FirebaseHelper.refSchool;

public class Groups extends AppCompatActivity {
    Teacher teacher;//עצם מסוג מורה
    String school,phone,cls;//מאפיינים של מורה (כיתה, בית ספר וטלפון)


     ArrayList<String> GroupsList = new ArrayList<>();// רשימה שתכיל את כל הקבוצות שיש למורה

    ListView groups;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        groups=findViewById(R.id.groups);
        toolbar=findViewById(R.id.tb);

        toolbar.setTitle("קבוצות");
        setSupportActionBar(toolbar);

        Parcelable parcelable=getIntent().getParcelableExtra("teacher");//קבלת עצם המורה מהאקטיביטים הקודמים
        teacher= Parcels.unwrap(parcelable);//קישורו אל העצם מסוג מורה שהגדרנו עבור המסך הזה

        school=teacher.getSchool();//השמת ערכים בתכונות של המורה
        phone=teacher.getPhone();
        cls=teacher.getCls();

        final Intent team=new Intent(this,Team.class);//יצירת רכיב שיעביר את המורה בשעת הצורך אל המסך שמכיל את נתוני הקבוצה ואפשרות לשנות אותה

         SetList();//פעולה שמציגה את כל הקבוצות שיש למורה בתוך listview

        groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//מאזין ללחיצה על אחד מפרטי הקבוצה
//בעת לחיצה על אחת מהקבוצות ייהיה מעבר למסך עם נתוני הקבוצה המתאימה שנבחרה
                Parcelable parcelable= Parcels.wrap(teacher);//הכנת העצם למעבר בין המסכים
                team.putExtra("teacher", parcelable);// הוספת עצם המורה לרכיב שיעביר את המורה למסך עם נתוני הקבוצה שנבחרה

                String GroupName=GroupsList.get(position);//קבלת שם הקבוצה
                team.putExtra("name",GroupName);//הוספת שם הקבוצה לרכיב שיעביר את המורה למסך עם נתוני הקבוצה שנבחרה
                startActivity(team);
            }
        });

    }


    private void SetList() {


        refSchool.child(school).child("Teacher").child(phone).child("zgroups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String x="";
                GroupsList.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    x = dsp.getKey();
                    GroupsList.add(x);

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
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, GroupsList);
        groups.setAdapter(itemsAdapter);
    }


    public void Accept_users(View view) {//מעבר למסך שמאפשר למורה לאשר תלמידים לכיתה שלו
        Intent i=new Intent(this,Acept_pupils.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        finish();
        startActivity(i);
    }


    public void new_group(View view) {//מעבר למסך שמאפשר למורה לפתוח קבוצה חדשה
        Intent i=new Intent(this,New_group.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        finish();
        startActivity(i);
    }
}
