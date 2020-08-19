package com.example.coronacheckcustomer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.lang.reflect.Array;
import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.coronacheckcustomer.FBref.refBusinesses;
import static com.example.coronacheckcustomer.FBref.refUsers;

public class ChangeDetails extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner DeclaredUser;
    TextView school;
    ListView Groups;
    LinearLayout disappear;

    Customer customer;
    Child child;
    Toolbar toolbar;
    String phone;
    int pos=0;
    int groupos;




    Object SelectedUser;
    GroupsAdapter adapter;

    DatabaseReference refGroups,refChildren;


    ArrayList<String>options=new ArrayList<String>();
    ArrayList<Object>Users=new ArrayList<>();
    ArrayList<Groups>groups=new ArrayList<>();
    ArrayList<Groups>groups2=new ArrayList<>();
    ArrayList<Child>children=new ArrayList<>();

    boolean user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_details);
        DeclaredUser=findViewById(R.id.spinnerUser);
        Groups=findViewById(R.id.listViewGroups);
        school=findViewById(R.id.schoolTv);
        disappear=findViewById(R.id.disappearingLayout);

        Parcelable parcelable=getIntent().getParcelableExtra("customer");
        customer= Parcels.unwrap(parcelable);

        phone=customer.getPhone();
        toolbar=findViewById(R.id.tb);

        toolbar.setTitle("שינוי פרטים");
        setSupportActionBar(toolbar);



        options.add("אני");
        for (int i=0;i<customer.getChildren().size();i++){
            String x=customer.getChildren().get(i).getName();
            options.add(x);

        }



        ArrayAdapter<CharSequence> UsersAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, options);
        //מתאם שנועד לקשר ספינר אל רשימת  הנבחרים האפשריים

        DeclaredUser.setAdapter(UsersAdapter);
        DeclaredUser.setOnItemSelectedListener(this);





    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected= parent.getItemAtPosition(position).toString();

        if(selected.equals("אני")){
        SelectedUser=customer;
        groups= customer.getGroups();
        user=true;

    }
        else{

        for(Child c: customer.getChildren()){
            if(c.getName().equals(selected)){
                SelectedUser=c;
                child=c;
                //new Child(c.getName(),c.getSchool(),c.getCls(),c.getGroups(),c.getParentPhone(),c.getPosition());
                groups= c.getGroups();
                user=false;
                pos++;
                break;
            }
        }
    }
        if(groups!=null)
             Adapt(groups);
        else
            adapter.clear();
        if(!user){
            disappear.setVisibility(View.VISIBLE);
        }
        else{
            disappear.setVisibility(View.GONE);
        }

}

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.

    }



    public  class GroupsAdapter extends ArrayAdapter {//ה-class עבור המתאם המעוצב שיצרתי
        private int layout;

        public GroupsAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, 0, objects);
            layout = resource;

        }


        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainViewholder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.details = convertView.findViewById(R.id.detail);
                viewHolder.approve = convertView.findViewById(R.id.approve);
                viewHolder.remove = convertView.findViewById(R.id.remove);
                convertView.setTag(viewHolder);


            }

            mainViewholder = (ViewHolder) convertView.getTag();
            final Groups group = groups.get(position);
            final String code=group.getGroupCode();
            final String GroupName = group.getName();
            final String business=group.getOrganizationName();
            final String leaderPhone=group.getLeaderPhone();
            String text = GroupName;
            mainViewholder.details.setText(text);
            mainViewholder.approve.setVisibility(View.GONE);
            mainViewholder.remove.setText("צא מקבוצה");


            mainViewholder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
//הפעולה הזאתי מקבלת חלק מהתכונות של התלמיד שלאחר לחיצת כפתור נוסף לקבוצה ומשנה עבורו את ערך התכונה activated ל-true ככה התלמיד יוכל להיות חלק מבית הספר
                public void onClick(View v) {
                    refGroups = refBusinesses.child(business).child("groups");
                    refGroups.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            GenericTypeIndicator<ArrayList<Groups>> t = new GenericTypeIndicator<ArrayList<Groups>>() {
                            };
                            groups = dataSnapshot.getValue(t);
                            int counter=0,groupPosition=0;
                            for (Groups g : groups) {
                                if (g.getGroupCode().equals(code)) {
                                    groupos = counter;
                                    refGroups.child(String.valueOf(groupos)).child("list").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Users.clear();
                                            if(dataSnapshot.getValue()!=null){
                                                int i=0;
                                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                                                    if (userSnapshot.child("phone").getValue() != null) {
                                                        Customer cust = userSnapshot.getValue(Customer.class);
                                                        if ((user)&&(cust.getPhone().equals(phone))) {
                                                            Toast.makeText(ChangeDetails.this, "עזבת את הקבוצה", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else
                                                            Users.add(cust);


                                                        } else if (userSnapshot.child("parentPhone").getValue() != null) {
                                                       Child chi = userSnapshot.getValue(Child.class);
                                                       if ((!user)&&(chi.getParentPhone().equals(phone)) && (chi.getName().equals(child.getName()))) {
                                                            Toast.makeText(ChangeDetails.this, "הסרת את ילדך מהקבוצה", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else
                                                       Users.add(chi);


                                                }
                                                    i++;
                                            }
                                                refGroups.child(String.valueOf(groupos)).child("list").setValue(Users);

                                            }
                                            else
                                                Toast.makeText(ChangeDetails.this, "null", Toast.LENGTH_SHORT).show();

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                }
                                counter++;
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }


                    });
                    int d = 0;

                    if(user){
                        refUsers.child(customer.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Customer C=dataSnapshot.getValue(Customer.class);
                                int z=0;
                                for (Groups g : C.getGroups()) {
                                    if(g.getGroupCode().equals(code)){
                                        groups2=C.getGroups();
                                        groups2.remove(z);
                                        customer.setGroups(groups2);

                                        refUsers.child(customer.getPhone()).child("groups").setValue(groups2);
                                        break;
                                    }

                                    z++;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }


                    if (!user) {
                        refChildren = refUsers.child(customer.getPhone()).child("children");

                        refChildren.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                GenericTypeIndicator<ArrayList<Child>> t = new GenericTypeIndicator<ArrayList<Child>>() {
                                };
                                children=dataSnapshot.getValue(t);
                                int childPosition = 0, counter = 0;
                                for (Child c : children) {
                                    if (c.getName().equals(child.getName())) {
                                        int d=0;
                                        for (Groups g : c.getGroups()) {
                                            if(g.getGroupCode().equals(code)){
                                                groups2=c.getGroups();
                                                groups2.remove(d);
                                                child.setGroups(groups2);
                                                children=customer.getChildren();
                                                children.set(childPosition,child);
                                                customer.setChildren(children);
                                                refChildren.child(String.valueOf(counter)).setValue(child);
                                                break;
                                            }

                                            d++;
                                        }



                                    }

                                    counter++;
                                }


                                }




                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                    }
                    adapter.remove(adapter.getItem(position));//
                    notifyDataSetChanged();
                }

    });

            return convertView;//בשורה הזו הפעולה מחזירה את התצוגה החדשה שהגדרנו
        }
    }


    public class ViewHolder {//רכיבי התצוגה שיועדו לlistview
        TextView details;
        Button approve, remove;
    }

    private void Adapt(ArrayList<Groups> arrayList) {// פעולת הקישור בין המתאם שעוצב עבור המסך הזה אל הרשימה הנגללת (listview)
        adapter = new GroupsAdapter(this, R.layout.user_list_unconfirmed, arrayList);
        Groups.setAdapter(adapter);

    }


    public void moveSchool(View view) {
        AlertDialog.Builder builder=new AlertDialog.Builder(ChangeDetails.this);
        builder.setTitle("עוברים בית ספר? ");
        builder.setMessage("שים לב, שבלחיצה על המשך אתה בוחר להעביר את הילד שלך לבית ספר אחר.");
        builder.setPositiveButton("המשך", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MoveToSchoolActivity();
            }
        });
        builder.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert=builder.create();
        alert.show();


    }

    private void MoveToSchoolActivity() {
        Intent i = new Intent(this,  MoveSchool.class);
        Parcelable parcelable= Parcels.wrap(customer);
        Parcelable parcelable1= Parcels.wrap(child);
        i.putExtra("customer", parcelable);
        i.putExtra("sC",parcelable1);
        startActivity(i);
    }


    public void JoinOrganiztion(View view) {
        Intent i = new Intent(this, JoinOrganiztion.class);
        Parcelable parcelable= Parcels.wrap(customer);
        Parcelable parcelable1= Parcels.wrap(SelectedUser);
        i.putExtra("customer", parcelable);
        i.putExtra("b",true);
        i.putExtra("sU", parcelable1);
        startActivity(i);

    }

    @Override
    public void onBackPressed() {
        finish();
        Intent i=new Intent(this,MainScreen.class);
        Parcelable parcelable= Parcels.wrap(customer);
        i.putExtra("customer", parcelable);
        startActivity(i);
    }



}
