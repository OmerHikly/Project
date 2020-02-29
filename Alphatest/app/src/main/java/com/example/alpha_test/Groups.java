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

public class Groups extends AppCompatActivity {
    Teacher teacher;
    String school,phone,cls;


     ArrayList<String> GroupsList = new ArrayList<>();

    ListView groups;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        groups=findViewById(R.id.groups);

        Parcelable parcelable=getIntent().getParcelableExtra("teacher");
        teacher= Parcels.unwrap(parcelable);

        school=teacher.getSchool();
        phone=teacher.getPhone();
        cls=teacher.getCls();

        final Intent team=new Intent(this,Team.class);
        groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Parcelable parcelable= Parcels.wrap(teacher);
                team.putExtra("teacher", parcelable);

                String GroupName=GroupsList.get(position);
                team.putExtra("name",GroupName);
                startActivity(team);
            }
        });
         SetList();


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

    private void Adapt() {
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, GroupsList);
        groups.setAdapter(itemsAdapter);
    }


    public void Accept_users(View view) {
        Intent i=new Intent(this,Acept_pupils.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);
    }


    public void new_group(View view) {
        Intent i=new Intent(this,New_group.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);
    }
}
