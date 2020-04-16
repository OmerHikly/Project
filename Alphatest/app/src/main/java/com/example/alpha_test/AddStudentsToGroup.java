package com.example.alpha_test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

public class AddStudentsToGroup extends AppCompatActivity {
    TextView Shown_Students;
    ListView students_options;
    EditText search_students;

    LinearLayout linearLayout;

    ArrayList<String> Students = new ArrayList<>();//יכיל את התלמידים ששייכים לקבוצה שנבחרה
    ArrayList<String> Options = new ArrayList<>();// יכיל את התלמידים שאינם שייכים לקבוצה ורוצה המורה להוסיף
    ArrayList<String> Demo = new ArrayList<>();//העתק של  options
    ArrayList<String> Choosen = new ArrayList<>();//רשימת התלמידים שנבחרו להיווסף לקבוצה

    ArrayList<String> ExistPhones = new ArrayList<>();//רשימת התלמידים שנבחרו להיווסף לקבוצה
    ArrayList<String> NewPhones = new ArrayList<>();//רשימת התלמידים שנבחרו להיווסף לקבוצה


    Teacher teacher;//עצם מסוג מורה
    Student student;//עצם מסוג תלמיד

    String school,phone,cls;//מאפיינים של מורה (כיתה, בית ספר וטלפון)
    String GroupName;//שם הקבוצה
    String Stu;//Students  עצם מסוג מחרוזת שיכיל את ה-arraylist
    Boolean f=false;

    NotInGroupAdapter adapter;//מתאם לListview שיכל את מי שלא נמצא בקבוצה ויכול להתווסף
    DatabaseReference refGroup;//הרפרנס של הקבוצה ב-database


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_students_to_group);
        students_options= findViewById(R.id.Show_Universal_Students);
        search_students=findViewById(R.id.Search_Universal_Students);
        Shown_Students=findViewById(R.id.ChoosenText);
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);

        Parcelable parcelable=getIntent().getParcelableExtra("teacher");//קבלת עצם המורה מהאקטיביטים הקודמים
        teacher= Parcels.unwrap(parcelable);//קישורו אל העצם מסוג מורה שהגדרנו עבור המסך הזה

        Intent gi=getIntent();
        GroupName=gi.getStringExtra("name");
        Students=gi.getStringArrayListExtra("Stu");
        ExistPhones=gi.getStringArrayListExtra("Ex");
        Stu=Students.toString();

        school=teacher.getSchool();//השמת ערכים בתכונות של המורה
        phone=teacher.getPhone();
        cls=teacher.getCls();

        refGroup=refSchool.child(school).child("Teacher").child(phone).child("zgroups").child(GroupName);
      SetList();//יצירת רשימה של מועמדים להוספה וסינונם

    }

    private void SetList() {
        search_students.addTextChangedListener(new TextWatcher() {//מגיב כשהטקסט משתנה ו
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(adapter!=null) {
                    adapter.clear();
                }
                adapter.getFilter().filter(s);// משאי רק את כל המחרוזות שמכילות את מה שהוקלד
            }


            @Override
            public void afterTextChanged(Editable s) {

            }


        });

        refSchool.child(school).child("Student").addValueEventListener(new ValueEventListener() {//יצירת רשימה של כל מי שיוכל להתווסף
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Options.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    if ((dsp.child("activated").getValue() != f)&&(!Stu.contains(dsp.getKey()))) {

                            student = new Student();
                            student = dsp.getValue(Student.class);
                            String id = student.getId();
                            String name = student.getName();
                            String secondname = student.getSecondName();
                            String uphone = student.getPhone();
                            String x = "תלמיד: " + name + " " + secondname + " " + id + " " + uphone;
                            Options.add(x);



                    }
                    Demo.clear();
                    Demo.addAll(Options);
                    Adapt();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }



    public void Add(View view) {//הוספה של מי שנבחר לקבוצה החדשה ועדכון בסיס הנתונים
        String ChoosensStudents=NewPhones.toString();
        ChoosensStudents=ChoosensStudents.substring(1,ChoosensStudents.length()-1);
        //שורה זו מסירה את ה'[' וה-']' שמופיעם כאשר עושים toString() ל-Arraylist -
        String ExsistStudents=ExistPhones.toString();
        ExsistStudents=ExsistStudents.substring(1,ExsistStudents.length()-1);
        //שורה זו מסירה את ה'[' וה-']' שמופיעם כאשר עושים toString() ל-Arraylist -
        linearLayout.removeAllViews();
        Students.addAll(Choosen);
        NewPhones.clear();
        Choosen.clear();
        String UpdatedGroup=ExsistStudents+" "+ChoosensStudents;
        refGroup.setValue(UpdatedGroup);
        Adapt();

        Intent i=new Intent(this,Team.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        i.putExtra("name",GroupName);
        startActivity(i);    }


    public  class NotInGroupAdapter extends ArrayAdapter {//ה-class של המתאם
        private int layout;

        public NotInGroupAdapter(@NonNull Context context, int resource, @NonNull List objects) {
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
                viewHolder.approve = convertView.findViewById(R.id.approve);
                viewHolder.details = convertView.findViewById(R.id.detail);
                viewHolder.remove = convertView.findViewById(R.id.remove);
                convertView.setTag(viewHolder);


            }

            mainViewholder = (ViewHolder) convertView.getTag();
            final String str = Options.get(position);
            final String[] Splitted = str.split(" ");
            String text = Splitted[0] + " " + Splitted[1] + " " + Splitted[2] + Splitted[3];
            mainViewholder.details.setText(text);
            ViewGroup.LayoutParams params= mainViewholder.details.getLayoutParams();
            params.height= ViewGroup.LayoutParams.MATCH_PARENT;

            mainViewholder.remove.setVisibility(View.GONE);
            mainViewholder.approve.setText("הוסף לקבוצה");
            mainViewholder.approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Options.remove(position);
                    Demo.remove(position);
                    Adapt();
                    if(Shown_Students.getText() == ""){
                        Choosen.add(str);
                        NewPhones.add(Splitted[4]);
                        final TextView textView = new TextView(AddStudentsToGroup.this);
                        textView.setTextColor(Color.parseColor("#46cb18"));
                        textView.setText(Splitted[1]+" "+Splitted[2]+", ");
                        linearLayout.addView(textView);
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Choosen.remove(position);
                                NewPhones.remove(position);
                                linearLayout.removeView(textView);
                                Options.add(str);
                                Demo.add(str);
                                Adapt();

                            }
                        });



                    }
                    else{
                        Choosen.add(str);
                        NewPhones.add(Splitted[4]);
                        final TextView textView = new TextView(AddStudentsToGroup.this);
                        textView.setTextColor(Color.parseColor("#b06500"));
                        textView.setText(Splitted[1]+Splitted[2]+" ");
                        linearLayout.addView(textView);
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Choosen.remove(position);
                                NewPhones.remove(position);
                                linearLayout.removeView(textView);
                                Options.add(str);
                                Demo.add(str);
                                Adapt();
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



    private Filter arrayfilter=new Filter() {//הפעולה שמסננת (עצם מסוג פילטר) את מי שמתאים לפי להוספה ומכיל את המחרוזת שהוקלדה לחיפוש
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
        protected void publishResults(CharSequence constraint, FilterResults results) {//עדכון המתאם עם הערכים המתאימים לחיפוש שהוזן
            adapter.clear();
            adapter.addAll((List) results.values);
            adapter.notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {//המרה של העצם resultValue למחרוזת
            return ((String) resultValue);
        }
    };





    private void Adapt() {//פעולה המקשרת בין המתאם לבין Options
        adapter = new NotInGroupAdapter(this, R.layout.user_list_unconfirmed, Options);
        students_options.setAdapter(adapter);
    }



    public void Accep_pupils(View view) {//מעבר למסך אישור תלמידים לכיתה
        Intent i=new Intent(this,Acept_pupils.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);
    }

    public void Groups(View view) { //מעבר למסך קבוצות
        Intent i=new Intent(this,Groups.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);
    }
    public void Open_Group(View view){//מעבר למסך פתיחת קבוצה
        Intent i=new Intent(this,New_group.class);
        Parcelable parcelable= Parcels.wrap(teacher);
        i.putExtra("teacher", parcelable);
        startActivity(i);
    }


}
