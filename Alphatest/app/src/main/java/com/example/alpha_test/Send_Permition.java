    package com.example.alpha_test;

    import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.alpha_test.FirebaseHelper.refSchool;



    public class Send_Permition extends AppCompatActivity {
        EditText Searcher, FileName, Cause, Notes;
        ListView ShowOptions;
        TextView Shown;
        LinearLayout linearLayout;
        Button firstH;
        Button secondH;
        Button DaTe;

        Teacher teacher;
        Student student;
        String school, phone, cls, fullName;

        String ex, re;

        AlertDialog.Builder adb;
        boolean keepGoing = true;

        UsersNgroupsAdapter adapter;

        ArrayList<String> Mixed = new ArrayList<>();//רשימה של קבוצות שיש לאותו מורה ולהן הוא רוצה לשלוח את האישור יציאה
        ArrayList<String> PhoneList = new ArrayList<>();//רשימה של קבוצות שיש לאותו מורה ולהן הוא רוצה לשלוח את האישור יציאה

        ArrayList<String> Demo = new ArrayList<>();//רשימה מועתקת של Students לצורך ביצוע חיפושים מבלי לשנות את ערכה המקורי של רשימת התלמידים
        ArrayList<String> Choosen = new ArrayList<>();//רשימה של תלמידים שנבחרו להצטרף לקבוצה שעומד להיווצר

        DatabaseReference refGroups;//רפרנס לכתובת בdatabase שתחתיה אפשר להוסיף קבוצות
        DatabaseReference refBarcode;

        Boolean f = false;

        String fileName, cause, notes;

        long millis;


        Calendar calendar=Calendar.getInstance();
        int Year=calendar.get(Calendar.YEAR);
        int Month=calendar.get(Calendar.MONTH);
        int Date=calendar.get(Calendar.DATE);

        int UpdatedYear;
        int UpdatedMonth;
        int UpdatedDate;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_send__permition);
            Searcher = findViewById(R.id.searchUsers);
            FileName = findViewById(R.id.portClearance);
            Cause = findViewById(R.id.Cause);
            Notes = findViewById(R.id.Notes);
            ShowOptions = findViewById(R.id.showOptions);
            Shown = findViewById(R.id.choosen);

            firstH = findViewById(R.id.exit);
            secondH = findViewById(R.id.enter);

            DaTe=findViewById(R.id.datesetter);

            Parcelable parcelable = getIntent().getParcelableExtra("teacher");
            teacher = Parcels.unwrap(parcelable);

            school = teacher.getSchool();
            phone = teacher.getPhone();
            cls = teacher.getCls();

            refGroups = refSchool.child(school).child("Teacher").child(phone).child("zgroups");
            linearLayout = (LinearLayout) findViewById(R.id.linlay);//קישור בין המסך ב-xml לרכיב ב--java


            setList();
        }

        private void setList() {

            Searcher.addTextChangedListener(new TextWatcher() {//מאזין לשינוי בהקלדת הטקסט
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                  if(adapter!=null) {
                      adapter.clear();//כשהטקסט משתנה המתאם ישתנה בהתאם לStrings שיכילו את מה שהוקלד
                  }
                    adapter.getFilter().filter(s);
                }


                @Override
                public void afterTextChanged(Editable s) {

                }


            });


            refSchool.child(school).child("Student").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Mixed.clear();
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        if (dsp.child("activated").getValue() != f) {
                            student = new Student();
                            student = dsp.getValue(Student.class);
                            String id = student.getId();
                            String name = student.getName();
                            String secondname = student.getSecondName();
                            String uphone = student.getPhone();
                            String x = "תלמיד: " + name + " " + secondname + " " + id + " " + uphone;
                            Mixed.add(x);

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

            refGroups.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        String GroupName = dsp.getKey();
                        String x = "קבוצה: " + GroupName;
                        Mixed.add(x);


                    }


                    Demo.clear();
                    Demo.addAll(Mixed);
                    Adapt(Mixed);//פעולה שיוצרת מתאם עבור ה-arraylist ומקשרת בינו לבין ה-listView

                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        public void pickHourE(View view){
            Calendar mcurrentTime = Calendar.getInstance();
            final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            final int minute = mcurrentTime.get(Calendar.MINUTE);
            final int second = mcurrentTime.get(Calendar.SECOND);
            int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
            int  month = mcurrentTime.get(Calendar.MONTH);
            int year = mcurrentTime.get(Calendar.YEAR);

            if(DaTe.getText().toString().equals("בחר תאריך יציאה עתידי")){
                year=Year;
                month=Month;
                day=Date;
            }
            else{
                year=UpdatedYear;
                month=UpdatedMonth;
                day=UpdatedDate;
            }



            TimePickerDialog mTimePicker;
            final int finalYear = year;
            final int finalMonth = month;
            final int finalDay = day;
            mTimePicker = new TimePickerDialog(Send_Permition.this, new TimePickerDialog.OnTimeSetListener() {

                SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    firstH.setText(String.format("%02d:%02d", selectedHour, selectedMinute));


                    String myDate = finalYear + "/" + finalMonth + "/" + finalDay + " " + selectedHour + ":" + selectedMinute + ":" + "00";

                    Date date = null;
                    try {
                        date = sd.parse(myDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    millis = date.getTime();


                }
            }, hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();

        }

        public void pickHourR(View view) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(Send_Permition.this, new TimePickerDialog.OnTimeSetListener() {


                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    secondH.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                }
            }, hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();

        }

        public void getDate(View view) {

            DatePickerDialog datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int date) {
                    String dataString= date+"/"+month+"/"+year;
                    if(((year<Year) ||(year==Year)&&(month<Month)||((year==Year)&&(month==Month)&&(date<Date)))) {
                        Toast.makeText(getApplicationContext(), "נא לבחור תאריך עתידי", Toast.LENGTH_LONG).show();
                        DaTe.requestFocus();
                        return;
                    }
                    else{
                        DaTe.setText(dataString);
                        UpdatedYear=year;
                        UpdatedMonth=month;
                        UpdatedDate=date;
                    }
                }
            },Year,Month,Date);
            datePickerDialog.show();
        }


        public class UsersNgroupsAdapter extends ArrayAdapter {//ה-class עבור המתאם המעוצב שיצרתי
            private int layout;

            public UsersNgroupsAdapter(@NonNull Context context, int resource, @NonNull List objects) {
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
                String str = Mixed.get(position);
                String[] Splitted = str.split(" ");
                String text = "";
                //אם מדובר בתלמיד יש להציג רק את ה-4 הסטרינגים הראשונים ללא הטלפון
                if (Splitted[0].equals("תלמיד:")) {
                    text = Splitted[0] + " " + Splitted[1] + " " + Splitted[2] + Splitted[3];
                } else {
                    //כשמדובר בקבוצה יש להציג את כל שם הקבוצה
                    for (int i = 0; i < Splitted.length; i++) {
                        text = text + " " + Splitted[i];
                    }

                }
                //הצג טקסט
                mainViewholder.details.setText(text);
                ViewGroup.LayoutParams params = mainViewholder.details.getLayoutParams();
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                //התאם את גודל התא לכתוב

                mainViewholder.remove.setVisibility(View.GONE);
                mainViewholder.approve.setText("הוסף לשליחה");
                mainViewholder.approve.setOnClickListener(new View.OnClickListener() {
                    @Override
//כאשר לוחצים על approve הפעולה הזאתי מעדכנת את הרשימה של התלמידים המוצעים להוספה ומוסיפה את אותם תלמידים שנבחרו לרשימה חדשה ומציגה אותם כ-textVIew
                    public void onClick(View v) {
                        final String str = Mixed.get(position);
                        String[] Splitted = str.split(" ");
                        //להסיר מה-Listview את השורה שנבחרה
                        Mixed.remove(position);
                        notifyDataSetChanged();
                        for (int i = 0; i < Demo.size(); i++) {
                            if (Demo.get(i).contains(str)) {
                                Demo.remove(i);
                            }
                        }
                        // לוחצים על approve הפעולה הזאתי מעדכנת את הרשימה של התלמידים המוצעים להוספה ומוסיפה את אותם תלמידים שנבחרו לרשימה חדשה ומציגה אותם כ-textVIew
                            Choosen.add(str);
                            final TextView textView = new TextView(Send_Permition.this);
                            if (Splitted[0].equals("תלמיד:")) {
                                textView.setText(Splitted[1] + " " + Splitted[2] + ", ");
                            } else {
                                String text = "";
                                for (int i = 1; i < Splitted.length; i++) {
                                    text = text + " " + Splitted[i];
                                }
                                textView.setText(text + " (קבוצה), ");
                            }
                            linearLayout.addView(textView);
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    linearLayout.removeView(textView);
                                    Mixed.add(str);
                                    notifyDataSetChanged();
                                    Demo.add(str);
                                    for (int i = 0; i < Choosen.size(); i++) {
                                        if (Mixed.contains(Choosen.get(i))) {
                                            Choosen.remove(i);
                                        }
                                    }

                                }
                            });


                        }




                });
                return convertView;//בשורה הזו הפעולה מחזירה את התצוגה החדשה שהגדרנו

            }
        }

        public class ViewHolder {//רכיבי התצוגה שיועדו לlistview
            TextView details;
            Button approve, remove;
        }

        private Filter arrayfilter = new Filter() {//פעולה זו מסננת ומשאירה רק את שמות התלמידים שכוללים את רצף האותיות שנרשם בשדה הקלט
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<String> suggestions = new ArrayList<>();

                if (constraint == null || constraint.length() == 0 || Searcher.getText().toString().isEmpty()) {
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

        private void Adapt(ArrayList<String> arrayList) {
            adapter = new UsersNgroupsAdapter(this, R.layout.user_list_unconfirmed, arrayList);
            ShowOptions.setAdapter(adapter);
        }





        public void SendBarcode(View view) {

            if (Choosen.isEmpty()) {
                Toast.makeText(getApplicationContext(), "נא לבחור תלמידים או קבוצות לשליחה", Toast.LENGTH_SHORT).show();
            } else {
                if (FileName.getText().toString().isEmpty()) {
                    FileName.setError("נא לרשום את האישור");
                    FileName.requestFocus();
                    return;
                } else {
                    fileName = FileName.getText().toString();
                }
                if (Cause.getText().toString().isEmpty()) {
                    Cause.setError("נא לרשום את סיבת היציאה");
                    Cause.requestFocus();
                    return;
                } else {
                    cause = Cause.getText().toString();
                }

                if (firstH.getText().toString().equals("שעת יציאה")) {
                    Toast.makeText(getApplicationContext(), "חובה למלא שעת יציאה", Toast.LENGTH_LONG).show();
                } else {
                    if (secondH.getText().toString().equals("שעת חזרה")) {
                        keepGoing = false;
                        adb = new AlertDialog.Builder(this);
                        adb.setTitle("שים לב!");
                        adb.setMessage("לא סימנת שעת חזרה ומשמעות הדבר שאינך חוזר לבית הספר");
                        adb.setPositiveButton("אני לא מתכוון לחזור", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                keepGoing = true;
                            }
                        });
                        adb.setNegativeButton("אופס שכחתי!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                keepGoing = false;
                            }
                        });
                        AlertDialog ad = adb.create();
                        ad.show();
                    }


                    notes = Notes.getText().toString();
                    ex = firstH.getText().toString();
                    re = secondH.getText().toString();
                    fullName = teacher.getName() + " " + teacher.getSecondName();


                    listUserPhone();

                    Mixed.addAll(Choosen);
                    Demo.addAll(Choosen);
                    linearLayout.removeAllViews();

                    Choosen.clear();
                    PhoneList.clear();
                    Adapt(Mixed);



                    firstH.setText("שעת יציאה");
                    secondH.setText("שעת חזרה");

                  Toast.makeText(getApplicationContext(), "ברקוד נשלח בהצלחה!", Toast.LENGTH_SHORT).show();

                }
            }
        }

        private void listUserPhone() {
            PhoneList.clear();
            for (int i = 0; i < Choosen.size(); i++) {
                String x = Choosen.get(i);
                final String z = x;

                if (x.contains("קבוצה:")) {
                    x = x.substring(7);
                    final String y = x;
                    String Splitted[];
                    refGroups.child(y).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() != null) {
                                String team = dataSnapshot.getValue().toString();
                                String Splitted[] = team.split(" ");
                                for (int i = 0; i < Splitted.length; i++) {
                                    String StudentD = Splitted[i];
                                    char last = StudentD.charAt(StudentD.length() - 1);
                                    if (last == ',') {
                                        StudentD = StudentD.substring(0, StudentD.length() - 1);
                                    }
                                    if (!PhoneList.contains(StudentD)) {
                                        PhoneList.add(StudentD);
                                        UpdateInfo(StudentD);
                                    }

                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    String Splitted2[] = z.split(" ");
                    String gphone = Splitted2[4];
                    if (!PhoneList.contains(gphone)) {
                        PhoneList.add(gphone);
                        UpdateInfo(gphone);
                    }
                }

            }

        }

        private void UpdateInfo(final String phoneSent) {
            refSchool.child(school).child("Student").child(phoneSent).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Student thisTudent = dataSnapshot.getValue(Student.class);
                    String Id = thisTudent.getId();
                    String dataStore = phoneSent + "; " + fullName + "; " + cause + "; " + notes + "; " + Id + "; " + ex + "; " + re+ "; " +millis;
                    refBarcode = refSchool.child(school).child("Student").child(phoneSent).child("QR_Info");
                    refBarcode.setValue(dataStore);

                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });


        }


    }