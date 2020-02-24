package com.example.alpha_test;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.example.alpha_test.FirebaseHelper.refSchool;

public class ManageAcc extends AppCompatActivity {
    ListView users;
    Button Exist_Users, Confirm_Users;
    EditText et;

    ArrayList<String> MainArrayList = new ArrayList<>();
    ArrayList<String> suggestions = new ArrayList<>();

    ArrayList<String> Demo = new ArrayList<>();
    UsersAdapter adapter;


    String[] Unconfirmed_Users;
    String[] Confirmed_Users;

    Guard guard;
    Teacher teacher;
    Student student;
    Admin admin;

    Boolean f = false;
    Boolean t = true;

    int textlength;
    String school;
    String phone;
    boolean confirmed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_acc);
        Parcelable parcelable=getIntent().getParcelableExtra("adminM");
        admin= Parcels.unwrap(parcelable);

        phone = admin.getPhone();
        school = admin.getSchool();
        Toast.makeText(getApplicationContext(),school+"+"+phone,Toast.LENGTH_SHORT).show();

        users = findViewById(R.id.User_list);
        Exist_Users = findViewById(R.id.Users);
        Confirm_Users = findViewById(R.id.ConfirmUsers);
        et = findViewById(R.id.SearchText);






        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                    adapter.clear();
                    adapter.getFilter().filter(s);
                }



            @Override
            public void afterTextChanged(Editable s) {

            }


        });



    }



    public class UsersAdapter extends ArrayAdapter {
        private int layout;

        public UsersAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, 0, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return arrayfilter;
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
            String str = MainArrayList.get(position);
            String[] Splitted = str.split(" ");
            String text = Splitted[0] + " " + Splitted[1] + " " + Splitted[2] + Splitted[3];
            mainViewholder.details.setText(text);

            if (confirmed) {

                mainViewholder.approve.setText("חסום");
                mainViewholder.approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = MainArrayList.get(position);
                        String[] Splitted = str.split(" ");
                        String x = String.valueOf(position);
                        Toast.makeText(getApplicationContext(), x, Toast.LENGTH_SHORT).show();


                        if (Splitted[0].equals("שומר:")) {//checks if the user is guard by checking the first word in the string and looking in the firebase for the Guard branch
                            refSchool.child(school).child("Guard").child(Splitted[4]).child("activated").setValue(false);
                        } else if (Splitted[0].equals("מורה:")) {//checks if the user is guard by checking the first word in the string and looking in the firebase for the Teacher branch
                            refSchool.child(school).child("Teacher").child(Splitted[4]).child("activated").setValue(false);

                        }


                        adapter.remove(adapter.getItem(position));

                        notifyDataSetChanged();

                    }
                });


                mainViewholder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = MainArrayList.get(position);
                        String[] Splitted = str.split(" ");

                        // method that splits the string near the buttons to only words without spaces
                        if (Splitted[0].equals("שומר:")) {//checks if the user is guard by checking the first word in the string and looking in the firebase for the Guard branch
                            refSchool.child(school).child("Guard").child(Splitted[4]).removeValue();
                        }
                        if (Splitted[0].equals("מורה:")) {//checks if the user is guard by checking the first word in the string and looking in the firebase for the Teacher branch
                            refSchool.child(school).child("Teacher").child(Splitted[4]).removeValue();


                        }

                        adapter.remove(adapter.getItem(position));
                        notifyDataSetChanged();
                    }
                });
            } else {

                mainViewholder.approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = MainArrayList.get(position);
                        String[] Splitted = str.split(" ");
                        String x = String.valueOf(position);
                        Toast.makeText(getApplicationContext(), x, Toast.LENGTH_SHORT).show();

                        // method that splits the string near the buttons to only words without spaces
                        if (Splitted[0].equals("שומר:")) {//checks if the user is guard by checking the first word in the string and looking in the firebase for the Guard branch
                            refSchool.child(school).child("Guard").child(Splitted[4]).child("activated").setValue(true);
                        }
                        if (Splitted[0].equals("מורה:")) {//checks if the user is guard by checking the first word in the string and looking in the firebase for the Teacher branch

                            refSchool.child(school).child("Teacher").child(Splitted[4]).child("activated").setValue(true);

                        }
                        //


                        adapter.remove(adapter.getItem(position));
                        notifyDataSetChanged();
                    }
                });

                mainViewholder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = MainArrayList.get(position);
                        String[] Splitted = str.split(" ");

                        // method that splits the string near the buttons to only words without spaces
                        if (Splitted[0].equals("שומר:")) {//checks if the user is guard by checking the first word in the string and looking in the firebase for the Guard branch
                            refSchool.child(school).child("Guard").child(Splitted[4]).removeValue();
                        }
                        if (Splitted[0].equals("מורה:")) {//checks if the user is guard by checking the first word in the string and looking in the firebase for the Teacher branch
                            refSchool.child(school).child("Teacher").child(Splitted[4]).removeValue();


                        }
                        //
                        adapter.remove(adapter.getItem(position));
                        notifyDataSetChanged();
                    }


                });


            }
            return convertView;
        }
    }

    public class ViewHolder {
        TextView details;
        Button approve, remove;
    }




    private Filter arrayfilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<String> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0||et.getText().toString().isEmpty()) {
                suggestions.addAll(Demo);
            } else {
                String filterpattern = constraint.toString().toLowerCase().trim();
                for (String x : Demo) {
                    if (x.toLowerCase().contains(filterpattern)) {
                        suggestions.add(x);
                    }
                }

            }
            results.values = suggestions;
            results.count = suggestions.size();
            return results;
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.clear();
            adapter.addAll((List) results.values);
            adapter.notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((String) resultValue);
        }
    };



















        public void Confirm_users(View view) {
            confirmed = false;
            UsersList();

        }

        public void Watch_users(View view) {
            confirmed = true;
            UsersList();

        }


        private void UsersList() {

            refSchool.child(school).child("Guard").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> Confirmed = new ArrayList<>();
                    ArrayList<String> Unconfirmed = new ArrayList<>();

                    int index = 0;

                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        if (dsp.child("activated").getValue() == f) {
                            guard = new Guard();
                            guard = dsp.getValue(Guard.class);
                            String id = guard.getId();
                            String name = guard.getName();
                            String secondname = guard.getSecondName();
                            String uphone = guard.getPhone();
                            String x = "שומר: " + name + " " + secondname + " " + id + " " + uphone;
                            Unconfirmed.add(x);
                            index++;
                        } else {
                            guard = new Guard();
                            guard = dsp.getValue(Guard.class);
                            String id = guard.getId();
                            String name = guard.getName();
                            String secondname = guard.getSecondName();
                            String uphone = guard.getPhone();
                            String x = "שומר: " + name + " " + secondname + " " + id + " " + uphone;
                            Confirmed.add(x);
                            index++;


                        }


                    }


                    Teacher_List(Unconfirmed, Confirmed);
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void Teacher_List(final ArrayList<String> Unconfirmed, final ArrayList<String> Confirmed) {
            refSchool.child(school).child("Teacher").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> TConfirmed = new ArrayList<>();
                    ArrayList<String> TUnconfirmed = new ArrayList<>();

                    int index = 0;


                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        if (dsp.child("activated").getValue() == f) {
                            teacher = new Teacher();
                            teacher = dsp.getValue(Teacher.class);
                            String id = teacher.getId();
                            String name = teacher.getName();
                            String secondname = teacher.getSecondName();
                            String uphone = teacher.getPhone();
                            String x = "מורה: " + name + " " + secondname + " " + id + " " + uphone;
                            TUnconfirmed.add(x);
                            index++;
                        } else {
                            teacher = new Teacher();
                            teacher = dsp.getValue(Teacher.class);
                            String id = teacher.getId();
                            String name = teacher.getName();
                            String secondname = teacher.getSecondName();
                            String uphone = teacher.getPhone();
                            String x = "מורה: " + name + " " + secondname + " " + id + " " + uphone;

                            TConfirmed.add(x);
                            index++;

                        }


                    }

                    Unconfirmed.addAll(TUnconfirmed);
                    Confirmed.addAll(TConfirmed);

                    Student_List(Unconfirmed,Confirmed);
                }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        };








                private void Student_List(final ArrayList<String> Un, final ArrayList<String> Co) {
                    refSchool.child(school).child("Student").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<String> Sconfirmed = new ArrayList<>();
                            ArrayList<String> SUnconfirmed = new ArrayList<>();

                            int index = 0;


                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                if (dsp.child("activated").getValue() == f) {
                                } else {
                                    student = new Student();
                                    student = dsp.getValue(Student.class);
                                    String id = student.getId();
                                    String name = student.getName();
                                    String secondname = student.getSecondName();
                                    String uphone = student.getPhone();
                                    String x = "תלמיד: " + name + " " + secondname + " " + id + " " + uphone;

                                    Sconfirmed.add(x);
                                    index++;

                                }


                            }
                            Co.addAll(Sconfirmed);
                            Un.addAll(SUnconfirmed);


                            if (confirmed) {
                                MainArrayList = Co;
                            } else {
                                MainArrayList = Un;

                            }
                            Demo.clear();
                            Demo.addAll(MainArrayList);
                            Adapt(MainArrayList);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }


                    });
                }


        private void Adapt(ArrayList<String> arrayList) {
            adapter = new UsersAdapter(this, R.layout.user_list_unconfirmed, arrayList);
            users.setAdapter(adapter);

        }



}


