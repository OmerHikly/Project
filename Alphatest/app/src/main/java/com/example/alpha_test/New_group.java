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
Teacher teacher;//עצם מסוג מורה
Student student;//עצם מסוג תלמיד

Boolean f=false;//מכיוון שאחת התכונות שהועלתה לfirebase היא מסוג Boolean  ולא boolean כדי להשוות את התכונה הזאת נצטרך עצם מסוג Boolean

GroupOptionsAdapter adapter;//מתאם שמתאים לאקטיביטי הספציפי


    ArrayList<String> Students = new ArrayList<>();//רשימה של תלמידים שתכלול את כל התלמידים שאושרו על ידי המורים שלהם ואפשר להוסיף אותם לקבוצה
    ArrayList<String> Demo = new ArrayList<>();//רשימה מועתקת של Students לצורך ביצוע חיפושים מבלי לשנות את ערכה המקורי של רשימת התלמידים
    ArrayList<String> Choosen = new ArrayList<>();//רשימה של תלמידים שנבחרו להצטרף לקבוצה שעומד להיווצר


    String school,phone,cls;//מאפיינים של מורה (כיתה, בית ספר וטלפון)

TextView Shown_Students;
ListView students_options;
EditText search_students,Group_Name;

    DatabaseReference refGroups;//רפרנס לכתובת בdatabase שתחתיה אפשר להוסיף קבוצות

    LinearLayout linearLayout;//יכיל את רכיב ה-xml של המסך
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        students_options= findViewById(R.id.Show_Universal_Students);//קישורבין עצמים ב-xml ל-Java
        search_students=findViewById(R.id.Search_Universal_Students);
        Shown_Students=findViewById(R.id.ChoosenText);
        Group_Name=findViewById(R.id.GN);

        Parcelable parcelable=getIntent().getParcelableExtra("teacher");//קבלת עצם המורה מהאקטיביטים הקודמים
        teacher= Parcels.unwrap(parcelable);//קישורו אל העצם מסוג מורה שהגדרנו עבור המסך הזה

        school=teacher.getSchool();//השמת ערכים בתכונות של המורה
        phone=teacher.getPhone();
        cls=teacher.getCls();


        refGroups=refSchool.child(school).child("Teacher").child(phone).child("zgroups");
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);//קישור בין המסך ב-xml לרכיב ב--java

        GroupOptions();// פעולה שמציגה את כל התלמידים שניתן להוסיף אל הקבוצה ב-Listview
        //ומאפשרת חיפוש עבור תלמיד ספציפי בין כל התלמידים שאושרו כתלמידי בית הספר
    }

    private void GroupOptions() {
        //האזנה לרכיב ה-EditText והפעלת פעולה שמסננת את כל העצמים שאינם כוללים את מה שנרשם ב-editText
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
//הפעולה שמוסיפה ל-arrayuList את כל התלמידים שאושרו בחלק ממערכת בית הספר
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
            Adapt(Students);//פעולה שיוצרת מתאם עבור ה-arraylist ומקשרת בינו לבין ה-listView
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


    public  class GroupOptionsAdapter extends ArrayAdapter {//ה-class עבור המתאם המעוצב שיצרתי
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
                @Override//כאשר לוחצים על approve הפעולה הזאתי מעדכנת את הרשימה של התלמידים המוצעים להוספה ומוסיפה את אותם תלמידים שנבחרו לרשימה חדשה ומציגה אותם כ-textVIew
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
                   else{// לוחצים על approve הפעולה הזאתי מעדכנת את הרשימה של התלמידים המוצעים להוספה ומוסיפה את אותם תלמידים שנבחרו לרשימה חדשה ומציגה אותם כ-textVIew
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
            return convertView;//בשורה הזו הפעולה מחזירה את התצוגה החדשה שהגדרנו

        }
    }


    public class ViewHolder {//רכיבי התצוגה שיועדו לlistview
        TextView details;
        Button approve, remove;
    }




    private Filter arrayfilter=new Filter() {//פעולה זו מסננת ומשאירה רק את שמות התלמידים שכוללים את רצף האותיות שנרשם בשדה הקלט
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
        protected void publishResults(CharSequence constraint, FilterResults results) {//פעולה זו מעדכנת את המתאם הספציפי עבור המסך הזה עם הערכים שמעניקה הפעולה שמעליה
            adapter.clear();
            adapter.addAll((List) results.values);
            adapter.notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {// פעולה שהופכת את העצם שהתקבל מהפעולה הראשונה של הסינון לעצם מסוג String
            return ((String) resultValue);
        }
    };

    private void Adapt(ArrayList<String> arrayList) {// פעולת הקישור בין המתאם שעוצב עבור המסך הזה אל הרשימה הנגללת (listview)
        adapter = new GroupOptionsAdapter(this, R.layout.user_list_unconfirmed, arrayList);
        students_options.setAdapter(adapter);

    }



















    public void Groups(View view) {//מעבר למסך שמציג את הקבוצות שהקים המורה
        Intent i=new Intent(this,Groups.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);
    }



       public void Accep_pupils(View view) {//מעבר למסך שמאפשר למורה לאשר תלמידים לכיתה שלו
        Intent i=new Intent(this,Acept_pupils.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);
    }



}

