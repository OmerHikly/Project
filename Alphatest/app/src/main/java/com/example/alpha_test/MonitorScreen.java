package com.example.alpha_test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.alpha_test.FirebaseHelper.refSchool;

public class MonitorScreen extends AppCompatActivity {

    ListView lv;

    String school;

    EditText et;

    ArrayList<String> outs=new ArrayList<>();
    ArrayList<String> Demo=new ArrayList<>();


    HiderAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_screen);
        lv=findViewById(R.id.Outs);
        et=findViewById(R.id.SearchText);

        Intent gi=getIntent();
        school=gi.getStringExtra("school");



        final Intent i=new Intent(this,SpecificOut.class);//יצירת רכיב שיעביר את המורה בשעת הצורך אל המסך שמכיל את נתוני הקבוצה ואפשרות לשנות אותה


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//מאזין ללחיצה על אחד מפרטי הקבוצה
//בעת לחיצה על אחת מהקבוצות ייהיה מעבר למסך עם נתוני הקבוצה המתאימה שנבחרה

                String str=outs.get(position);//קבלת שם הקבוצה
                String Splitted[]=str.split("__");
                i.putExtra("school",school);
                i.putExtra("key",Splitted[1]);//הוספת שם הקבוצה לרכיב שיעביר את המורה למסך עם נתוני הקבוצה שנבחרה
                startActivity(i);
            }
        });

        SetList();
        et.addTextChangedListener(new TextWatcher() {//מאזין לשינוי בהקלדת הטקסט
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.clear();//כשהטקסט משתנה המתאם ישתנה בהתאם לStrings שיכילו את מה שהוקלד
                adapter.getFilter().filter(s);

            }


            @Override
            public void afterTextChanged(Editable s) {

            }


        });

    }

    private void SetList() {
        refSchool.child(school).child("AAMonitor").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dsp:dataSnapshot.getChildren()){
                    String d=dsp.getValue().toString();
                    String Splitted[]=d.split("; ");
                    String name=Splitted[0];
                    String TimeAndDate=Splitted[8];
                    String out=name+" "+TimeAndDate;
                    String [] Split=dsp.getKey().split("_");

                    outs.add(out+"__"+Split[1]+Split[2]);




                }
                Demo.addAll(outs);
                Adapt();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public class HiderAdapter extends ArrayAdapter {//ה-class עבור המתאם המעוצב שיצרתי
        private int layout;

        public HiderAdapter(@NonNull Context context, int resource, @NonNull List objects) {
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
                viewHolder.details = convertView.findViewById(R.id.tv1);
                viewHolder.Secret = convertView.findViewById(R.id.tv2);
                convertView.setTag(viewHolder);


            }

            mainViewholder = (ViewHolder) convertView.getTag();
            String str = outs.get(position);
            String Splitted[]=str.split("__");
            mainViewholder.details.setText(Splitted[0]);
            mainViewholder.Secret.setText(Splitted[1]);
            mainViewholder.Secret.setVisibility(View.GONE);


            return convertView;
        }




        public class ViewHolder {//רכיבי התצוגה שיועדו לlistview
            TextView details, Secret;
        }


    }

    private Filter arrayfilter = new Filter() {//פעולה זו מסננת ומשאירה רק את שמות התלמידים שכוללים את רצף האותיות שנרשם בשדה הקלט
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<String> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0 || et.getText().toString().isEmpty()) {
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


    private void Adapt() {// פעולת הקישור בין המתאם שעוצב עבור המסך הזה אל הרשימה הנגללת (listview)
        adapter = new HiderAdapter(this, R.layout.mylist, outs);
        lv.setAdapter(adapter);
    }


}
