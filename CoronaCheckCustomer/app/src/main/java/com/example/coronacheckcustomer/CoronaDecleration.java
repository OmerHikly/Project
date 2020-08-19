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
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.coronacheckcustomer.FBref.refBusinesses;
import static com.example.coronacheckcustomer.FBref.refSchool;

public class CoronaDecleration extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Toolbar toolbar;
    Spinner Declare, For;
    CheckedTextView ctv1, ctv2;
    Button btn;

    ArrayList<String> DeclareOptions = new ArrayList<String>();
    ArrayList<Groups> groups = new ArrayList<Groups>();
    ArrayList<String> groupNames = new ArrayList<String>();


    Object SelectedUser;
    Groups group;
    Customer customer;
    Child child;
    String phone;

    String gN, selected, dateAndHour;
    int pos = 0;
    boolean user;
    boolean forGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corona_decleration);
        Declare = findViewById(R.id.Dbg);
        For = findViewById(R.id.ford);
        ctv1 = findViewById(R.id.cTv1);
        ctv2 = findViewById(R.id.cTv2);
        btn = findViewById(R.id.Dec);
        toolbar = findViewById(R.id.tb);

        toolbar.setTitle("הצהרת קורונה");
        setSupportActionBar(toolbar);

        Intent gi = getIntent();
        Parcelable parcelable = getIntent().getParcelableExtra("customer");
        customer = Parcels.unwrap(parcelable);
        phone = customer.getPhone();


        DeclareOptions.add("עצמי");
        for (int i = 0; i < customer.getChildren().size(); i++) {
            String x = customer.getChildren().get(i).getName();
            DeclareOptions.add(x);

        }
        ArrayAdapter<CharSequence> UsersAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, DeclareOptions);
        //מתאם שנועד לקשר ספינר אל רשימת  הנבחרים האפשריים

        Declare.setAdapter(UsersAdapter);
        Declare.setOnItemSelectedListener(this);

        ctv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ctv1.isChecked()){
                    ctv1.setChecked(false);
                }
                else
                    ctv1.setChecked(true);
            }
        });

        ctv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ctv2.isChecked()){
                    ctv2.setChecked(false);
                }
                else
                    ctv2.setChecked(true);
            }

        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selected = parent.getItemAtPosition(position).toString();

        if (selected.equals("עצמי")) {
            SelectedUser = customer;
            groups = customer.getGroups();
            user = true;

        } else {
            for (Child c : customer.getChildren()) {
                if (c.getName().equals(selected)) {
                    SelectedUser = c;
                    child = c;
                    groups = c.getGroups();
                    user = false;
                    pos++;
                    break;
                }
            }
        }
        if (!groups.isEmpty()) {
            groupNames.clear();
            if (!user && child.getCls() != null)
                groupNames.add("כיתה: " + child.getCls());
            for (Groups g : groups) {
                groupNames.add(g.getName());
            }
            ArrayAdapter<CharSequence> UsersAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, groupNames);
            //מתאם שנועד לקשר ספינר אל רשימת  הנבחרים האפשריים
            For.setAdapter(UsersAdapter);
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void Declare(View view) {
        if (ctv1.isChecked() && ctv2.isChecked()) {

            Child mistake = null;
            gN = String.valueOf(For.getSelectedItem());
            String check = gN.substring(0, 4);
            String organizationName = null;
            String code = null;

            Calendar rightNow = Calendar.getInstance();
            int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
            int minutes = rightNow.get(Calendar.MINUTE);

            String time = currentHour + ":" + minutes;

            String myDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());

            String current = myDate + " " + time;
            Customer customeized = new Customer(customer.getPhone(), customer.getId(), customer.getName(), null, null, null, null);
            if (!user) {
                mistake = new Child(child.getName(), null, null, null, child.getParentPhone());
            }
            if (check.equals("כיתה")) {
                Toast.makeText(this, "ההצהרה היומית נשלחה בהצלחה למורה", Toast.LENGTH_SHORT).show();
                organizationName = child.getSchool();
                code = child.getCls();
                forGroup = false;
                Decleration d = new Decleration(customeized, mistake, organizationName, code, current);
                refSchool.child(organizationName).child("Declerations").child(myDate).child(customer.getPhone()).setValue(d);


            } else {
                if (!groups.isEmpty()) {
                    forGroup = true;
                    int i = 0;
                    String check2 = String.valueOf(For.getItemAtPosition(0));
                    if (check2.substring(0, 4).equals("כיתה"))
                        i--;
                    for (Groups g : groups) {
                        if (g.getName().equals(gN)) {
                            organizationName = g.getOrganizationName();
                            code = g.getGroupCode();
                            break;
                        }
                        i++;

                    }
                    Decleration d = new Decleration(customeized, mistake, organizationName, code, current);
                    Toast.makeText(this, "ההצהרה היומית נשלחה בהצלחה", Toast.LENGTH_SHORT).show();

                    refBusinesses.child(organizationName).child("Declerations").child(myDate).child(customer.getPhone()).setValue(d);
                } else {
                    Toast.makeText(this, "המשתמש שנבחר לא נמצא עדיין בקבוצה או בכיתה", Toast.LENGTH_SHORT).show();
                }
            }

        }
        else
            Toast.makeText(this, "נא לאשר שלמשתמש שנבחר אין תסמינים", Toast.LENGTH_SHORT).show();
    }
}





