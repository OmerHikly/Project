package com.example.alpha_test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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

public class Team extends AppCompatActivity {
    Teacher teacher;

    String school,phone,cls;
    String GroupName;

    ArrayList<String> Students = new ArrayList<>();

    ListView lv;
    Button btn;
    TextView Shown_Students;

    GroupAdapter adapter;

    DatabaseReference refGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        lv=findViewById(R.id.StudentsLv);
        btn=findViewById(R.id.AddMembers);

        Intent gi=getIntent();
       GroupName=gi.getStringExtra("name");

        Parcelable parcelable=getIntent().getParcelableExtra("teacher");
        teacher= Parcels.unwrap(parcelable);

        school=teacher.getSchool();
        phone=teacher.getPhone();
        cls=teacher.getCls();


        refGroup=refSchool.child(school).child("Teacher").child(phone).child("zgroups").child(GroupName);


            SetList();

    }

    private void SetList() {
        refSchool.child(school).child("Teacher").child(phone).child("zgroups").child(GroupName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Students.clear();
                if (dataSnapshot.getValue() != null) {
                    String team = dataSnapshot.getValue().toString();
                    String Splitted[] = team.split(" ");

                    for (int i = 0; i < Splitted.length / 5; i++) {
                        String StudentD = Splitted[i * 5] + " " + Splitted[i * 5 + 1] + " " + Splitted[i * 5 + 2] + " " + Splitted[i * 5 + 3] + " " + Splitted[i * 5 + 4];
                        Students.add(StudentD);
                    }
                    Adapt(Students);


                } else {
                    Students.clear();
                    Adapt(Students);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }

    private void Adapt(ArrayList<String> arrayList) {
        adapter = new GroupAdapter(this, R.layout.user_list_unconfirmed, arrayList);
        lv.setAdapter(adapter);
    }



    public  class GroupAdapter extends ArrayAdapter {
        private int layout;

        public GroupAdapter(@NonNull Context context, int resource, @NonNull List objects) {
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
                viewHolder.approve=convertView.findViewById(R.id.approve);
                viewHolder.details = convertView.findViewById(R.id.detail);
                viewHolder.remove = convertView.findViewById(R.id.remove);
                convertView.setTag(viewHolder);


            }

            mainViewholder =(ViewHolder) convertView.getTag();
            final String str = Students.get(position);
            final String[] Splitted = str.split(" ");
            String text = Splitted[0] + " " + Splitted[1] + " " + Splitted[2] + Splitted[3];
            mainViewholder.details.setText(text);
            mainViewholder.approve.setVisibility(View.GONE);
            mainViewholder.remove.setText("הסר");
            mainViewholder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Students.remove(position);
                    Adapt(Students);
                    if (Students.isEmpty()) {
                        refGroup.removeValue();
                    } else {
                        String GroupData = Students.toString();
                        GroupData=GroupData.substring(1,GroupData.length()-1); //שורה זו מסירה את ה'[' וה-']' שמופיעם כאשר עושים toString() ל-Arraylist -
                        refGroup.setValue(GroupData);
                    }

                }
            });




            return convertView;

        }
    }


    public class ViewHolder {
        TextView details;
        Button  remove,approve;
    }





    public void AddStudents(View view) {
        Intent i=new Intent(this,AddStudentsToGroup.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        i.putExtra("name",GroupName);
        i.putExtra("Stu",Students);
        startActivity(i);
    }
}
