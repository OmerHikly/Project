package com.example.coronacheckcustomer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;

import static com.example.coronacheckcustomer.FBref.refSchool;
import static com.example.coronacheckcustomer.FBref.refUsers;

public class MoveSchool extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner UserPicked,Class,ClassNumber;
    AutoCompleteTextView School;
    Toolbar toolbar;

    Customer customer;
    Child childPicked;
    String phone;
    String cls;

    ArrayList<String> options = new ArrayList <String>();
    ArrayList<String> Schools = new ArrayList<>();//רשימה של בתי ספר שיש ב-firebase

    ArrayList<Child> ClassMembers = new ArrayList<Child>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_school);

        School=findViewById(R.id.School);
        UserPicked=findViewById(R.id.spinnerUser);
        Class=findViewById(R.id.Class);
        ClassNumber=findViewById(R.id.ClassNumber);

        Intent gi = getIntent();
        Parcelable parcelable = getIntent().getParcelableExtra("customer");
        customer = Parcels.unwrap(parcelable);
        phone=customer.getPhone();


        for (int i = 0; i < customer.getChildren().size(); i++) {
            String x = customer.getChildren().get(i).getName();
            options.add(x);

        }
        ArrayAdapter<CharSequence> UsersAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, options);
        //מתאם שנועד לקשר ספינר אל רשימת  הנבחרים האפשריים

        UserPicked.setAdapter(UsersAdapter);
        UserPicked.setOnItemSelectedListener(this);

        Parcelable parcelable1 = getIntent().getParcelableExtra("sC");
        Child child= Parcels.unwrap(parcelable1);

        int i = 0;
        for (Child c : customer.getChildren()) {
            if (c.getName().equals(child.getName())) {
                break;
            }
            i++;
        }
        UserPicked.setSelection(i);


        toolbar = findViewById(R.id.tb);

        toolbar.setTitle("מעבר בית ספר");
        setSupportActionBar(toolbar);

        ArrayAdapter<CharSequence> ClassAdapter = ArrayAdapter.createFromResource(this, R.array.classes, R.layout.support_simple_spinner_dropdown_item);
        //מתאם שנועד לקשר ספינר אל רשימת הכיתות האפשריים
        ArrayAdapter<CharSequence> NumbersAdapter = ArrayAdapter.createFromResource(this, R.array.Numbers, R.layout.support_simple_spinner_dropdown_item);
        //מתאם שנועד לקשר ספינר אל רשימת מספרי הכיתות האפשריים

        ClassAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        NumbersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Class.setAdapter(ClassAdapter);
        ClassNumber.setAdapter(NumbersAdapter); //Array creation for the spinners in the application the array will have the class  and class number


        FireBaseSchools();


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = parent.getItemAtPosition(position).toString();

        for (Child c : customer.getChildren()) {
            if (c.getName().equals(selected)) {
                childPicked = c;
                if(childPicked.getCls()!=null) {
                    cls = childPicked.getCls();
                }
            }

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void FireBaseSchools() {

        refSchool.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Schools.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {//checks how many main children there are in the firebase(Schools number)
                    String st =dsp.getKey();
                    Schools.add(st);

                }


                Adapt(Schools);//This method sets the adpater between the array we just created and the "edit text" of the school.
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });


    }

    private void Adapt(ArrayList<String> arrayList ) {//פעולה שמקשרת בין המערך הרשימתי שמכיל את בתי הספר לרשימה הנגללת
        SchoolAdapter adapter=new SchoolAdapter(this, arrayList);
        School.setAdapter(adapter);



    }


    public void MoveSchool(View view) {
        final String school = School.getText().toString();
        String Clss=String.valueOf(Class.getSelectedItem());
        String clsNum= String.valueOf(ClassNumber.getSelectedItem());
        final String childName=childPicked.getName();
        final String ClassAndNumber = Clss + "'" +clsNum;


        if (school.isEmpty()){
            School.requestFocus();
            School.setError("נא לבחור בית ספר");
             return;
    }

        if(Clss.isEmpty()||clsNum.isEmpty()){
            Toast.makeText(this, "נא לבחור כיתה או מס' כיתה", Toast.LENGTH_SHORT).show();
        }

        else{//פעולה ששולחת בקשת הצטרפות לכיתה בבית הספר החדש
            refSchool.child(school).child("Classes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {//checks how many main children there are in the firebase(Schools number)
                        if(dsp.getKey().equals(ClassAndNumber)){

                            Request request=new Request(customer.getPhone(),customer.getName(),childPicked,null);
                            refSchool.child(school).child("Classes").child(ClassAndNumber).child("requests").child("Request_"+customer.getPhone()).setValue(request);

                            Toast.makeText(MoveSchool.this, "בקשתך להוסיף את ילדך לכיתה נשלחה אל מחנך הכיתה!", Toast.LENGTH_LONG).show();




                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            refSchool.child(school).child("Classes").child(cls).child("list").addListenerForSingleValueEvent(new ValueEventListener() {//פעולה שמוציאה את הילד מהכיתה הקיימת
                //ומעבירה אותו בית ספר
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<ArrayList<Child>> t = new GenericTypeIndicator<ArrayList<Child>>() {
                    };
                    Toast.makeText(MoveSchool.this, ClassAndNumber, Toast.LENGTH_SHORT).show();
                    if (dataSnapshot.getValue(t) != null) {
                    ClassMembers = dataSnapshot.getValue(t);
                        int i = 0;
                        for (Child c : ClassMembers) {
                            String x = c.getName();
                            if (c.getName().equals(childName) && c.getParentPhone().equals(childPicked.getParentPhone())) {
                                ClassMembers.remove(i);
                                Toast.makeText(MoveSchool.this, "The debugger", Toast.LENGTH_SHORT).show();
                                refSchool.child(school).child("Classes").child(cls).child("list").setValue(ClassMembers);
                                break;
                            }
                            i++;
                        }
                    }
                    else{
                        Toast.makeText(MoveSchool.this, "null", Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            refUsers.child(phone).child("children").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<ArrayList<Child>> t = new GenericTypeIndicator<ArrayList<Child>>() {};
                    ArrayList<Child>children = dataSnapshot.getValue(t);
                    int i =0;
                    for(Child ch:children) {
                        if (ch.getName().equals(childName)) {
                            ch.setCls(null);
                            ch.setSchool(null);
                            children.set(i, ch);
                            refUsers.child(phone).child("children").child(String.valueOf(i)).setValue(ch);
                        }
                        i++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }

    }
}
