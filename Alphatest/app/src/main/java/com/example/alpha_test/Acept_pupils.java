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

public class Acept_pupils extends AppCompatActivity {
   ListView results;//רכיבי התצוגה של המסך הזה
   EditText search;
   Toolbar toolbar;


    Boolean f=false;//מכיוון שאחת התכונות שהועלתה לfirebase היא מסוג Boolean  ולא boolean כדי להשוות את התכונה הזאת נצטרך עצם מסוג Boolean
    String school, phone, cls;//מאפיינים של מורה (כיתה, בית ספר וטלפון)


    ArrayList<String> Students = new ArrayList<>();//רשימה של תלמידים שתכלול את כל התלמידים ששייכים לכיתה של המורה שהתחבר
    ArrayList<String> Class = new ArrayList<>();//רשימה של תלמידים שאושרו על ידי המורה ונוספו לכיתה שלו
    ArrayList<String> Demo = new ArrayList<>();//רשימה מועתקת של Students לצורך ביצוע חיפושים מבלי לשנות את ערכה המקורי של רשימת התלמידים

    Teacher teacher;//עצם מסוג מורה
    Student student;//עצם מסוג תלמיד

    StudentsAdapter adapter;

    DatabaseReference refClass;//רפרנס לכתובת בdatabase שתכיל את הכיתה שיש למורה
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acept_pupils);

        results=findViewById(R.id.students_results);
        search=findViewById(R.id.Search_students);
        toolbar=findViewById(R.id.tb);

        toolbar.setTitle("אישור תלמידים לכיתה");
        setSupportActionBar(toolbar);

        Parcelable parcelable=getIntent().getParcelableExtra("teacher");//קבלת עצם המורה מהאקטיביטים הקודמים
        teacher= Parcels.unwrap(parcelable);//קישורו אל העצם מסוג מורה שהגדרנו עבור המסך הזה

        school = teacher.getSchool();//השמת ערכים בתכונות של המורה
        phone = teacher.getPhone();
        cls = teacher.getCls();


        refClass=  refSchool.child(school).child("Teacher").child(phone).child("zclass");


        Do();;// פעולה שמציגה את כל התלמידים שניתן להוסיף אל הקבוצה ב-Listview
        //ומאפשרת חיפוש עבור תלמיד ספציפי בין כל התלמידים שאושרו כתלמידי בית הספר

    }

    private void Do() {

        //האזנה לרכיב ה-EditText והפעלת פעולה שמסננת את כל העצמים שאינם כוללים את מה שנרשם ב-editText

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
//הפעולה שמוסיפה ל-arrayuList את כל התלמידים שאושרו בחלק ממערכת בית הספר
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
                Adapt(Students);//פעולה שיוצרת מתאם עבור ה-arraylist ומקשרת בינו לבין ה-listView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
    }




    public  class StudentsAdapter extends ArrayAdapter {//ה-class עבור המתאם המעוצב שיצרתי
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
            ViewGroup.LayoutParams params= mainViewholder.details.getLayoutParams();
            params.height= ViewGroup.LayoutParams.MATCH_PARENT;


            mainViewholder.approve.setOnClickListener(new View.OnClickListener() {
                @Override//הפעולה הזאתי מקבלת חלק מהתכונות של התלמיד שלאחר לחיצת כפתור נוסף לקבוצה ומשנה עבורו את ערך התכונה activated ל-true ככה התלמיד יוכל להיות חלק מבית הספר
                public void onClick(View v) {
                    final String str = Students.get(position);
                    String[] Splitted = str.split(" ");
                    String x = String.valueOf(position);

                    refSchool.child(school).child("Student").child(Splitted[4]).child("activated").setValue(true);


                     refSchool.child(school).child("Teacher").child(phone).child("class").addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue()==null){//אם ה-class ריק אז התלמיד הוא היחיד בכיתה ולכן הוא הכיתה
                                refClass.setValue(str);
                            }
                            else{//במקרה שלא יש לצרף אליו עוד תלמידים כמו בפעולה הזאת
                                Class.clear();
                                String Exist_Class=dataSnapshot.getValue().toString();
                                String newClass=Exist_Class+" "+str;
                                String[] Splitted = Exist_Class.split(" ");//הפעולה Split יוצרת מערך של כל המילים לפי הרווחים שיש במחרוזת הראשית של הכיתה ב_firebase
                                for(int i=0;i<Splitted.length/5;i++){
                                    String data=Splitted[i*5]+Splitted[i*5+1]+Splitted[i*5+2]+Splitted[i*5+3]+Splitted[i*5+4];
                                    Class.add(data);
                                }


                                refClass.setValue(newClass);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });

                        refClass.setValue(Students.toString());


                    adapter.remove(adapter.getItem(position));//
                    notifyDataSetChanged();
                }


            });

            mainViewholder.remove.setOnClickListener(new View.OnClickListener() {//פעולה שמסירה את הצעת התלמיד להצטרף לכיתה משום שהתלמייד לא בחר את כיתתו בטעות
                @Override
                public void onClick(View v) {
                    String str = Students.get(position);
                    String[] Splitted = str.split(" ");
                    refSchool.child(school).child("Student").child(Splitted[4]).removeValue();

                    adapter.remove(adapter.getItem(position));
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





    private Filter arrayfilter=new Filter() {//פעולה זו מסננת ומשאירה רק את שמות התלמידים שכוללים את רצף האותיות שנרשם בשדה הקלט
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
        adapter = new StudentsAdapter(this, R.layout.user_list_unconfirmed, arrayList);
        results.setAdapter(adapter);

    }




    public void new_group(View view) {//מעבר למסך שמאפשר למורה לפתוח קבוצה חדשה
        Intent i=new Intent(this,New_group.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        finish();
        startActivity(i);
    }


    public void groups(View view) {//מעבר למסך שמציג את הקבוצות שהקים המורה
        Intent i=new Intent(this,Groups.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        finish();
        startActivity(i);
    }
}
