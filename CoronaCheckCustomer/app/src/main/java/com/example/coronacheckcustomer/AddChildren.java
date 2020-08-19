package com.example.coronacheckcustomer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.example.coronacheckcustomer.FBref.refBusinesses;
import static com.example.coronacheckcustomer.FBref.refSchool;
import static com.example.coronacheckcustomer.FBref.refUsers;

public class AddChildren extends AppCompatActivity {
    Toolbar toolbar;

    EditText FirstName,SecondName;
    EditText TeamCode;

    AutoCompleteTextView School;
    AutoCompleteTextView Business;

    Spinner spinc;
    Spinner spinn;

    Boolean b = true, s = true;

    Child child;

    ArrayList<String> Schools = new ArrayList<>();//רשימה של בתי ספר שיש ב-firebase
    ArrayList<String> Businesses=new ArrayList<>();

    ArrayList<Child> Children = new ArrayList<>();//רשימה של ילדים שיש firebase
    ArrayList<Groups> groups = new ArrayList<>();


    DatabaseReference databaseReference;//אזכור לdatabase- יצביע על השורש של העץ בבבסיס הנתונים

    String firstname, secondname, school, business, ClassAndNumber,teamCode;
    String Name,leaderPhone;
    Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_children);
        Parcelable parcelable = getIntent().getParcelableExtra("customer");
        customer = Parcels.unwrap(parcelable);


        FirstName = findViewById(R.id.FirstName);
        SecondName = findViewById(R.id.SecondName);
        School = findViewById(R.id.School);
        TeamCode=findViewById(R.id.TeamCode);
        Business=findViewById(R.id.LBusiness);

        spinc = findViewById(R.id.Class);
        spinn = findViewById(R.id.ClassNumber);

        toolbar = findViewById(R.id.tb);

        toolbar.setTitle("הוסף את ילדיך");
        setSupportActionBar(toolbar);

        databaseReference = FirebaseDatabase.getInstance().getReference();


        ArrayAdapter<CharSequence> ClassAdapter = ArrayAdapter.createFromResource(this, R.array.classes, R.layout.support_simple_spinner_dropdown_item);
        //מתאם שנועד לקשר ספינר אל רשימת הכיתות האפשריים
        ArrayAdapter<CharSequence> NumbersAdapter = ArrayAdapter.createFromResource(this, R.array.Numbers, R.layout.support_simple_spinner_dropdown_item);
        //מתאם שנועד לקשר ספינר אל רשימת מספרי הכיתות האפשריים

        ClassAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        NumbersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinc.setAdapter(ClassAdapter);
        spinn.setAdapter(NumbersAdapter); //Array creation for the spinners in the application the array will have the class  and class number


         FirebaseSchools();

        FireBaseBusinesses();




    }

    private void FireBaseBusinesses() {

        refBusinesses.addValueEventListener(new ValueEventListener() {

            @Override

                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Businesses.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {//checks how many main children there are in the firebase(Schools number)
                    String st = dsp.getKey();
                    Businesses.add(st);
                }
                Adapt(Businesses,Business);//This method sets the adpater between the array we just created and the "edit text" of the business.

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void FirebaseSchools() {

        refSchool.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Schools.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {//checks how many main children there are in the firebase(Schools number)
                    String st = dsp.getKey();
                    Schools.add(st);

                }


                Adapt(Schools, School);//This method sets the adpater between the array we just created and the "edit text" of the school.
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });


    }




    private void Adapt(ArrayList<String> arrayList, AutoCompleteTextView at) {//מקשר בין המתאם ל-listview
        SchoolAdapter adapter = new SchoolAdapter(this, arrayList);
        at.setAdapter(adapter);

    }

    private void checkIfSchoolExists() {//במידה ובית הספר לא קיים המערכת מודיעה למשתמש
        refSchool.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (school == "" || school == null || school.isEmpty()) {
                        if(business == "" || business == null || business.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "נא להזין את שם בית הספר", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Continue();
                        }
                } else {
                    if (dataSnapshot.hasChild(school)) {
                        Continue();
                    } else {
                        Toast.makeText(getApplicationContext(), "בית הספר שהוזן אינו קיים במערכת", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void BusinessExists() {//במידה ובית הספר לא קיים המערכת מודיעה למשתמש
        refBusinesses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (business == "" || business == null || business.isEmpty()) {
                    if(teamCode == "" || teamCode == null || teamCode.isEmpty())
                        Continue2();
                        else
                    Toast.makeText(getApplicationContext(), "נא להזין את שם בית העסק", Toast.LENGTH_SHORT).show();
                }


                else {
                    if (dataSnapshot.hasChild(business)) {
                        Continue2();
                    } else {
                        Toast.makeText(getApplicationContext(), "בית העסק שהוזן אינו קיים במערכת", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        }




    public void AddChild(View view) {


        firstname = FirstName.getText().toString();
        secondname = SecondName.getText().toString();
        business = Business.getText().toString();
        school = School.getText().toString();
        ClassAndNumber = String.valueOf(spinc.getSelectedItem()) + "'" + String.valueOf(spinn.getSelectedItem());
        teamCode=TeamCode.getText().toString();
        checkIfSchoolExists();


    }

    private void Continue() {

        if (firstname.isEmpty()) {
            FirstName.setError("נא לרשום שם");
            FirstName.requestFocus();
            return;
        }
        if (!Pattern.matches("['א-ת]+", firstname)) {
            FirstName.getText().clear();
            firstname = "";
            FirstName.setError("השם שלך צריך להכיל רק אותיות בעברית");
            FirstName.requestFocus();
            return;
        }


        if (secondname.isEmpty()) {
            SecondName.setError("נא לרשום שם משפחה");
            SecondName.requestFocus();
            return;
        }
        if (!Pattern.matches("['א-ת]+", secondname)) {
            SecondName.getText().clear();
            SecondName.setError("השם שלך צריך להכיל רק אותיות בעברית");
            SecondName.requestFocus();
            return;

        }

        if (business.isEmpty()) {
            business = null;
            b = false;
        } else if (teamCode.isEmpty()) {
            TeamCode.setError("נא לרשום את שם הקבוצה ");
            TeamCode.requestFocus();
            return;
        }

        if (school.isEmpty() && !ClassAndNumber.equals("'")) {
            Toast.makeText(getApplicationContext(), "נא לבחור בית ספר", Toast.LENGTH_SHORT).show();
        } else if (!school.isEmpty() && ClassAndNumber.equals("'")) {
            Toast.makeText(getApplicationContext(), "נא למלא כיתה", Toast.LENGTH_SHORT).show();
        } else if (school.isEmpty() && ClassAndNumber.equals("'")) {
            school = null;
            ClassAndNumber = null;
            s = false;

            BusinessExists();
        } else {
         BusinessExists();



        }
    }
    private void Continue2() {
        Name = firstname + " " + secondname;
        child = new Child(Name, null, null, null,customer.getPhone());


        method(child);
    }



    private void method(final Child chi) {
        refUsers.child(customer.getPhone()).child("children").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<Child>> t = new GenericTypeIndicator<ArrayList<Child>>() {
                };
                Children = dataSnapshot.getValue(t);
                if (!ChildrenExists(child, Children)) {

                    if (Children == null) {
                        Children = new ArrayList<>();
                        Children.add(chi);
                    } else {
                        Children.add(chi);

                    }
                    customer.setChildren(Children);
                    refUsers.child(customer.getPhone()).child("children").setValue(Children);
                    if(!b&!s) {
                        moveActivity();
                    }
                    else if(s)AssociateTeacher();
                    else AssociateGroup();
                }
                else{
                    Toast.makeText(AddChildren.this, "יש לך כבר ילד עם שם זהה ", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private boolean ChildrenExists(Child ch,ArrayList<Child>childr) {
        if(childr==null)return false;
        for(Child c:childr){
            if(c.getName().equals(ch.getName()))
                return true;

        }
        return false;
    }


    private void AssociateTeacher() {
        if (b && s) {
            AssociateGroup();
            Toast.makeText(AddChildren.this, "הוספת את ילדך בהצלחה ובקשתך להוסיף אותו לארגון ובית הספר נשלחה!", Toast.LENGTH_LONG).show();
        }
        if (b && !s) {
            Toast.makeText(AddChildren.this, "הוספת את ילדך בהצלחה ובקשתך להוסיף את ילדך לארגון נשלחה!", Toast.LENGTH_LONG).show();
        }
        if (!b && s) {
            Toast.makeText(AddChildren.this, "הוספת את ילדך בהצלחה ובקשתך להוסיף את ילדך  לבית הספר נשלחה!", Toast.LENGTH_LONG).show();

        }
        refSchool.child(school).child("Classes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {//checks how many main children there are in the firebase(Schools number)
                    if(dsp.getKey().equals(ClassAndNumber)){


                        Request request=new Request(customer.getPhone(),customer.getName(),child,null);
                        refSchool.child(school).child("Classes").child(ClassAndNumber).child("requests").child("Request_"+customer.getPhone()).setValue(request);

                        moveActivity();
                                      }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void AssociateGroup() {
        refBusinesses.child(business).child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<Groups>> t = new GenericTypeIndicator<ArrayList<Groups>>() {
                };
                groups = dataSnapshot.getValue(t);
                int i=0;
                for (Groups group:groups){
                    if(group.getGroupCode().equals(teamCode)){
                        String groupName=group.getName();
                        Request request=new Request(customer.getPhone(),customer.getName(),child,groupName);
                        refBusinesses.child(business).child("groups").child(String.valueOf(i)).child("requests").child("Request_"+customer.getPhone()).setValue(request);

                    }
                    i++;
                }
                if (!s){
                    Toast.makeText(AddChildren.this, "הוספת את ילדך בהצלחה ובקשתך להוסיף את ילדך לארגון נשלחה!", Toast.LENGTH_LONG).show();
                    moveActivity();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void moveActivity() {
        finish();
        Intent intento = new Intent(AddChildren.this, MainScreen.class);
        Parcelable parcelable = Parcels.wrap(customer);
        intento.putExtra("customer", parcelable);
        startActivity(intento);
    }

}

