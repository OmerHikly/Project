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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.example.alpha_test.FirebaseHelper.refSchool;

public class Acept_pupils extends AppCompatActivity {
   ListView results;
   EditText search;


    Boolean f=false;
    String school, phone, cls;


    ArrayList<String> Students = new ArrayList<>();
    ArrayList<String> Class = new ArrayList<>();
    ArrayList<String> Demo = new ArrayList<>();

    Teacher teacher;
    Student student;

    StudentsAdapter adapter;

    DatabaseReference refClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acept_pupils);

        results=findViewById(R.id.students_results);
        search=findViewById(R.id.Search_students);


        Parcelable parcelable=getIntent().getParcelableExtra("teacher");
        teacher= Parcels.unwrap(parcelable);
        school = teacher.getSchool();
        phone = teacher.getPhone();
        cls = teacher.getCls();


        refClass=  refSchool.child(school).child("Teacher").child(phone).child("zclass");


        Do();

    }

    private void Do() {


        search.addTextChangedListener(new TextWatcher() {
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

        refSchool.child(school).child("Student").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Students.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    if ((dsp.child("cls").getValue().equals(cls))&&(dsp.child("activated").getValue()==f)) {
                        student = new Student();
                        student = dsp.getValue(Student.class);
                        String id = student.getId();
                        String name = student.getName();
                        String secondname = student.getSecondName();
                        String uphone = student.getPhone();
                        String x = "תלמיד: " + name + " " + secondname + " " + id + " " + uphone;
                        Students.add(x);

                    }
                }
                Demo.clear();
                Demo.addAll(Students);
                Adapt(Students);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }




    public  class StudentsAdapter extends ArrayAdapter {
        private int layout;

        public StudentsAdapter(@NonNull Context context, int resource, @NonNull List objects) {
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
            String str = Students.get(position);
            String[] Splitted = str.split(" ");
            String text = Splitted[0] + " " + Splitted[1] + " " + Splitted[2] + Splitted[3];
            mainViewholder.details.setText(text);


            mainViewholder.approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String str = Students.get(position);
                    String[] Splitted = str.split(" ");
                    String x = String.valueOf(position);
                    Toast.makeText(getApplicationContext(), x, Toast.LENGTH_SHORT).show();

                    refSchool.child(school).child("Student").child(Splitted[4]).child("activated").setValue(true);


                     refSchool.child(school).child("Teacher").child(phone).child("class").addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue()==null){
                                refClass.setValue(str);
                            }
                            else{
                                Class.clear();
                                String Exist_Class=dataSnapshot.getValue().toString();
                                String newClass=Exist_Class+" "+str;
                                String[] Splitted = Exist_Class.split(" ");
                                for(int i=0;i<Splitted.length/5;i++){
                                    String data=Splitted[i*5]+Splitted[i*5+1]+Splitted[i*5+2]+Splitted[i*5+3]+Splitted[i*5+4];
                                    Class.add(data);
                                }


                                refClass.setValue(newClass);
                            }
                            for(int i=0;i<Class.size();i++){
                                Toast.makeText(getApplicationContext(),Class.get(i),Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });

                        refClass.setValue(Students.toString());


                    adapter.remove(adapter.getItem(position));
                    notifyDataSetChanged();
                }


            });

            mainViewholder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = Students.get(position);
                    String[] Splitted = str.split(" ");
                    refSchool.child(school).child("Student").child(Splitted[4]).removeValue();

                    adapter.remove(adapter.getItem(position));
                    notifyDataSetChanged();
                }


            });


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

            if (constraint == null || constraint.length() == 0||search.getText().toString().isEmpty()) {
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

    private void Adapt(ArrayList<String> arrayList) {
        adapter = new StudentsAdapter(this, R.layout.user_list_unconfirmed, arrayList);
        results.setAdapter(adapter);

    }
}
