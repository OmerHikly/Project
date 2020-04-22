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
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.example.alpha_test.FirebaseHelper.refSchool;

public class Team extends AppCompatActivity {
    Teacher teacher;//עצם מסוג מורה

    String school,phone,cls;//מאפיינים של מורה (כיתה, בית ספר וטלפון)
    String GroupName;//יכיל את שם הקבוצה שנבחרה במסך הקודם

    ArrayList<String> Students = new ArrayList<>();// יכיל את התלמידים ששייכים לקבוצה
    ArrayList<String> ExistPhones = new ArrayList<>();//רשימת התלמידים שנבחרו להיווסף לקבוצה

    Toolbar toolbar;
    ListView lv;
    Button btn;

    Student student;


    GroupAdapter adapter;//מתאם אישי שעוצב עבור הlistview

    DatabaseReference refGroup;// רפרנס אל מיקום הקבוצה ב -database
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        lv=findViewById(R.id.StudentsLv);
        btn=findViewById(R.id.AddMembers);
        toolbar=findViewById(R.id.tb);



        Intent gi=getIntent();//רכיב שמעביר את הערכים שהועברו מהמסך הקודם
       GroupName=gi.getStringExtra("name");//הצבת שם הקבוצה מהמסך הקודם

        toolbar.setTitle(GroupName);
        setSupportActionBar(toolbar);
        Parcelable parcelable=getIntent().getParcelableExtra("teacher");//קבלת עצם המורה מהאקטיביטים הקודמים
        teacher= Parcels.unwrap(parcelable);//קישורו אל העצם מסוג מורה שהגדרנו עבור המסך הזה

        school=teacher.getSchool();;//השמת ערכים בתכונות של המורה
        phone=teacher.getPhone();
        cls=teacher.getCls();


        refGroup=refSchool.child(school).child("Teacher").child(phone).child("zgroups").child(GroupName);//הגדרת הרפרנס


            SetList();// פעולה ששמה בListview את הרשימה של התלמידים ששייכים לקבוצה

    }

    private void SetList() {
        refSchool.child(school).child("Teacher").child(phone).child("zgroups").child(GroupName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Students.clear();
                ExistPhones.clear();
                if (dataSnapshot.getValue() != null) {
                    String team = dataSnapshot.getValue().toString();
                    String Splitted[] = team.split(" ");

                    for (int i = 0; i < Splitted.length; i++) {
                        String StudentD = Splitted[i];
                        char last=StudentD.charAt(StudentD.length()-1);
                        if(last==','){
                            StudentD=StudentD.substring(0,StudentD.length()-1);
                        }
                        ExistPhones.add(StudentD);
                    }
                    showData();



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }

    private void showData() {
        Students.clear();
        for(String p:ExistPhones) {
            refSchool.child(school).child("Student").child(p).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dsp) {
                    student = new Student();
                    student = dsp.getValue(Student.class);
                    String id = student.getId();
                    String name = student.getName();
                    String secondname = student.getSecondName();
                    String uphone = student.getPhone();
                    String x = "תלמיד: " + name + " " + secondname + " " + id + " " + uphone;
                    Students.add(x);
                    Adapt(Students);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private void Adapt(ArrayList<String> arrayList) {// פעולת הקישור בין המתאם שעוצב עבור המסך הזה אל הרשימה הנגללת (listview)
        adapter = new GroupAdapter(this, R.layout.user_list_unconfirmed, arrayList);
        lv.setAdapter(adapter);
    }



    public  class GroupAdapter extends ArrayAdapter {//ה-class עבור המתאם המעוצב שיצרתי
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
                @Override//פעולה זו מאםשרת למורה להסיר תלמיד מהקבוצה בגלל שכנראה התלמיד עבר כיתה
                public void onClick(View v) {
                    Students.remove(position);
                    ExistPhones.remove(position);
                    Adapt(Students);
                    if (Students.isEmpty()) {
                        refGroup.removeValue();
                    } else {
                        String GroupData = ExistPhones.toString();
                        GroupData=GroupData.substring(1,GroupData.length()-1); //שורה זו מסירה את ה'[' וה-']' שמופיעם כאשר עושים toString() ל-Arraylist -
                        refGroup.setValue(GroupData);
                    }

                }
            });




            return convertView;//בשורה הזו הפעולה מחזירה את התצוגה החדשה שהגדרנו

        }
    }


    public class ViewHolder {//רכיבי התצוגה שיועדו לlistview
        TextView details;
        Button  remove,approve;
    }





    public void AddStudents(View view) {//פעולת מעבר בין המסכים שמעבירה את המורה בלחיצת כפתור אל המסך שבו הוא יכול להוסיף תלמידים נוספים לקבוצה
        Intent i=new Intent(this,AddStudentsToGroup.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        i.putExtra("name",GroupName);
        i.putExtra("Stu",Students);
        i.putExtra("Ex",ExistPhones);
        startActivity(i);
    }
}
