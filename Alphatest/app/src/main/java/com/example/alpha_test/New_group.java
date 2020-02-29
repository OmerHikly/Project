package com.example.alpha_test;

import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
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

public class New_group extends AppCompatActivity {
Teacher teacher;
Student student;

Boolean f=false;

GroupOptionsAdapter adapter;


    ArrayList<String> Students = new ArrayList<>();
    ArrayList<String> Demo = new ArrayList<>();
    ArrayList<String> Choosen = new ArrayList<>();
    ArrayList<String> BetaChoosen = new ArrayList<>();



    String school,phone,cls;

TextView Shown_Students;
ListView students_options;
EditText search_students,Group_Name;

    DatabaseReference refGroups;

    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        students_options= findViewById(R.id.Show_Universal_Students);
        search_students=findViewById(R.id.Search_Universal_Students);
        Shown_Students=findViewById(R.id.ChoosenText);
        Group_Name=findViewById(R.id.GN);

        Parcelable parcelable=getIntent().getParcelableExtra("teacher");
        teacher= Parcels.unwrap(parcelable);

        school=teacher.getSchool();
        phone=teacher.getPhone();
        cls=teacher.getCls();


        refGroups=refSchool.child(school).child("Teacher").child(phone).child("zgroups");
      //  refGroups.setValue(null);
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);

        GroupOptions();
    }

    private void GroupOptions() {
        search_students.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(adapter!=null) {
                    adapter.clear();
                }
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
                if (dsp.child("activated").getValue()!=f) {
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

    public void New_group(View view) {
        if ((Choosen.size() > 1) && (Group_Name.getText().toString() != "") && (Group_Name.getText().toString() != null)) {
            if (Group_Name.length() > 1) {
                String groupName = Group_Name.getText().toString();
                String NewGroup = Choosen.toString();
                NewGroup = NewGroup.substring(1, NewGroup.length() - 1);//שורה זו מסירה את ה'[' וה-']' שמופיעם כאשר עושים toString() ל-Arraylist -
                refGroups.child(groupName).setValue(NewGroup);
                Students.addAll(Choosen);
                linearLayout.removeAllViews();
                Choosen.clear();
                Adapt(Students);
                Toast.makeText(getApplicationContext(), "קבוצה נוספה בהצלחה!", Toast.LENGTH_SHORT).show();


            }
            else{
                Toast.makeText(getApplicationContext(), "נא לרשום שם ראוי לקבוצה ", Toast.LENGTH_SHORT).show();

            }
        }
    }


    public  class GroupOptionsAdapter extends ArrayAdapter {
        private int layout;

        public GroupOptionsAdapter(@NonNull Context context, int resource, @NonNull List objects) {
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
                viewHolder.approve=convertView.findViewById(R.id.approve);
                viewHolder.details = convertView.findViewById(R.id.detail);
                viewHolder.remove = convertView.findViewById(R.id.remove);
                convertView.setTag(viewHolder);


            }

            mainViewholder = (ViewHolder) convertView.getTag();
            final String str = Students.get(position);
            final String[] Splitted = str.split(" ");
            String text = Splitted[0] + " " + Splitted[1] + " " + Splitted[2] + Splitted[3];
            mainViewholder.details.setText(text);
            mainViewholder.remove.setVisibility(View.GONE);
            mainViewholder.approve.setText("הוסף לקבוצה");
            mainViewholder.approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Students.remove(position);
                    Demo.remove(position);
                    Adapt(Students);
                   if(Shown_Students.getText() == ""){
                       Choosen.add(str);
                       final TextView textView = new TextView(New_group.this);
                       textView.setText(Splitted[1]+" "+Splitted[2]+", ");
                       linearLayout.addView(textView);
                       textView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               Choosen.remove(position);
                               linearLayout.removeView(textView);
                              Students.add(str);
                               Demo.add(str);
                               Adapt(Students);

                           }
                       });



                   }
                   else{
                       Choosen.add(str);
                       final TextView textView = new TextView(New_group.this);
                       textView.setText(Splitted[1]+" "+Splitted[2]+", ");
                       linearLayout.addView(textView);
                       textView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               linearLayout.removeView(textView);
                               Students.add(str);
                               Demo.add(str);
                               Adapt(Students);
                               for (int i=0;i<Choosen.size();i++){
                                   if(Students.contains(Choosen.get(i))){
                                       Choosen.remove(i);
                                   }
                               }

                           }
                       });


                    }

                }

                ;






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

            if (constraint == null || constraint.length() == 0||search_students.getText().toString().isEmpty()) {
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
        adapter = new GroupOptionsAdapter(this, R.layout.user_list_unconfirmed, arrayList);
        students_options.setAdapter(adapter);

    }



















    public void Groups(View view) {
        Intent i=new Intent(this,Groups.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);
    }



       public void Accep_pupils(View view) {
        Intent i=new Intent(this,Acept_pupils.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);
    }



}

