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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.time.temporal.TemporalAmount;
import java.util.ArrayList;

import static com.example.coronacheckcustomer.FBref.refBusinesses;
import static com.example.coronacheckcustomer.FBref.refSchool;


public class JoinOrganiztion extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner ChoosenUser;
    Toolbar toolbar;
    EditText TeamCode;
    AutoCompleteTextView Organization;

    ArrayList<String> Businesses = new ArrayList<>();//רשימה של בתי ספר שיש ב-firebase
    ArrayList<Groups> groups = new ArrayList<>();
    ArrayList<String> options = new ArrayList<String>();


    Customer customer;
    Child child;

    Object SelectedUser;
    boolean user;
    String business, code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_organiztion);
        ChoosenUser = findViewById(R.id.spinnerUser);
        Intent gi = getIntent();
        Parcelable parcelable = getIntent().getParcelableExtra("customer");
        customer = Parcels.unwrap(parcelable);


        options.add("אני");
        for (int i = 0; i < customer.getChildren().size(); i++) {
            String x = customer.getChildren().get(i).getName();
            options.add(x);

        }
        ArrayAdapter<CharSequence> UsersAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, options);
        //מתאם שנועד לקשר ספינר אל רשימת  הנבחרים האפשריים

        ChoosenUser.setAdapter(UsersAdapter);
        ChoosenUser.setOnItemSelectedListener(this);

        boolean b = gi.getBooleanExtra("b", false);
        if (b) {
            Parcelable parcelable1 = getIntent().getParcelableExtra("sU");
            Object object = Parcels.unwrap(parcelable1);
            if (object instanceof Child) {
                child = (Child) object;
                int i = 1;
                for (Child c : customer.getChildren()) {
                    if (c.getName().equals(child.getName())) {
                        break;
                    }
                    i++;
                }
                ChoosenUser.setSelection(i);
            }

        }


        Organization = findViewById(R.id.Organization);
        TeamCode = findViewById(R.id.TC);

        toolbar = findViewById(R.id.tb);

        toolbar.setTitle("הצטרף לקבוצה");
        setSupportActionBar(toolbar);


        FireBaseBusinesses();


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = parent.getItemAtPosition(position).toString();

        if (selected.equals("אני")) {
            SelectedUser = customer;
            user = true;
        } else {
            for (Child c : customer.getChildren()) {
                if (c.getName().equals(selected)) {
                    SelectedUser = c;
                    user = false;
                    child=c;
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void FireBaseBusinesses() {
        refBusinesses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Businesses.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {//checks how many main children there are in the firebase(Businesses number)
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

    private void Adapt(ArrayList<String> arrayList) {//מקשר בין המתאם ל-listview
        SchoolAdapter adapter = new SchoolAdapter(this, arrayList);
        Organization.setAdapter(adapter);

    }


    private void checkIfBusinessExists() {//במידה ובית העסק לא קיים המערכת מודיעה למשתמש
        refBusinesses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (business == "" || business == null || business.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "נא להזין את שם הארגון", Toast.LENGTH_SHORT).show();

                } else {
                    if (dataSnapshot.hasChild(business)) {
                        if (user) {
                            proceed();
                        } else {
                            childJoin();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "הארגון שהוזן אינו קיים במערכת", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public void Join(View view) {
        business = Organization.getText().toString();
        code = TeamCode.getText().toString();

        if (code.isEmpty())
            Toast.makeText(getApplicationContext(), "נא להזין את קוד הקבוצה", Toast.LENGTH_SHORT).show();

        if(UserHasGroup(code,business))
            Toast.makeText(this, "הנך נמצא כבר בקבוצה זו...", Toast.LENGTH_SHORT).show();
        else
            checkIfBusinessExists();
    }

    private boolean UserHasGroup(String code, String business) {
        ArrayList <Groups> g=new ArrayList<>();
        if(user)
            g=customer.getGroups();
        else
            g=child.getGroups();

        for (Groups group:g){
            if((group.getOrganizationName().equals(business))&&group.getGroupCode().equals(code)){
                return true;
            }
        }

        return false;
    }

    private void proceed() {
        refBusinesses.child(business).child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<Groups>> t = new GenericTypeIndicator<ArrayList<Groups>>() {
                };
                groups = dataSnapshot.getValue(t);
                int x=0;
                for (Groups group : groups) {
                    if (group.getGroupCode().equals(code)) {
                        String groupName=group.getName();

                        String ph = group.getLeaderPhone();

                        Request request = new Request(customer.getPhone(), customer.getName(), null,groupName );
                        refBusinesses.child(business).child("groups").child(String.valueOf(x)).child("requests").child("Request_" + customer.getPhone()).setValue(request);

                        Toast.makeText(getApplicationContext(), "בקשתך להצטרף לארגון נשלחה בהצלחה!", Toast.LENGTH_SHORT).show();

                     moveActivity();
                    }
                    x++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    private void childJoin() {//כאשר המשתמש בוחר להוסיף את ילדו לקבוצה  הפעולה הזאת תפעל ותשלח בקשה עבור הילד
        refBusinesses.child(business).child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<Groups>> t = new GenericTypeIndicator<ArrayList<Groups>>() {
                };
                groups = dataSnapshot.getValue(t);
                int c=0;
                for (Groups group : groups) {
                    if (group.getGroupCode().equals(code)) {
                        String groupName=group.getName();
                        Request request = new Request(customer.getPhone(), customer.getName(), child, groupName);
                        refBusinesses.child(business).child("groups").child(String.valueOf(c)).child("requests").child("Request_" + customer.getPhone()).setValue(request);

                        Toast.makeText(JoinOrganiztion.this, "בקשתך להוסיף את ילדך לארגון נשלחה!", Toast.LENGTH_LONG).show();
                        moveActivity();
                    }
                    c++;
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void moveActivity() {
        finish();
        Intent intento = new Intent(JoinOrganiztion.this, MainScreen.class);
        Parcelable parcelable = Parcels.wrap(customer);
        intento.putExtra("customer", parcelable);
        startActivity(intento);
    }
}
