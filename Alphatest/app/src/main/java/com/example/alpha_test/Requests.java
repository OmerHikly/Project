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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;

import static com.example.alpha_test.FirebaseHelper.refSchool;

public class Requests extends AppCompatActivity {
    ListView Requests;

    Teacher teacher;
    String school,phone,cls;

    ArrayList<String> reqs=new ArrayList<>();
    ArrayList<String> phones=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        Requests=findViewById(R.id.Reqs);

        Parcelable parcelable=getIntent().getParcelableExtra("teacher");
        teacher= Parcels.unwrap(parcelable);

        school=teacher.getSchool();
        phone=teacher.getPhone();
        cls=teacher.getCls();

        final Intent r=new Intent(this,RequestScreen.class);
        Requests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Parcelable parcelable= Parcels.wrap(teacher);//הכנת העצם למעבר בין המסכים
                r.putExtra("teacher", parcelable);// הוספת עצם המורה לרכיב שיעביר את המורה למסך עם נתוני הקבוצה שנבחרה
                r.putExtra("sphone",phones.get(position));
                startActivity(r);

            }
        });
        setList();


    }

    private void setList() {
        refSchool.child(school).child("Teacher").child(phone).child("Requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reqs.clear();
                phones.clear();
                for(DataSnapshot dsp:dataSnapshot.getChildren()){
                    String str=dsp.getValue().toString();
                    String [] Splitted=str.split(";");
                    String  text=Splitted[0]+" "+Splitted[1]+"    "+Splitted[2]+" "+Splitted[3];
                    phones.add(Splitted[4]);
                    reqs.add(text);
                }
                Adapt(reqs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void Adapt(ArrayList<String> arrayList) {
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        Requests.setAdapter(itemsAdapter);
    }
}
