package com.example.alpha_test;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.alpha_test.FirebaseHelper.refSchool;

public class AskExit extends AppCompatActivity {
    EditText Searcher, FileName, Cause, Notes;
    ListView ShowOptions;
    TextView Shown;
    LinearLayout linearLayout;
    Button firstH;
    Button secondH;

    Teacher teacher;
    Student student;
    String school, phone, cls;

    String ex, re;
    String fileName, cause, notes;
    String to;

    ArrayList<String> Teachers = new ArrayList<>();//רשימה של תלמידים שתכלול את כל התלמידים שאושרו על ידי המורים שלהם ואפשר להוסיף אותם לקבוצה
    ArrayList<String> Demo = new ArrayList<>();//רשימה מועתקת של Students לצורך ביצוע חיפושים מבלי לשנות את ערכה המקורי של רשימת התלמידים

    Boolean f = false;

    TeachersAdapter adapter;

    public Uri imguri;

    public static final int IMAGE_PICK_CODE=1000;
    public static final int PERMISSION_CODE=1001;

    private StorageTask<UploadTask.TaskSnapshot> uploadTask;

    public static StorageReference Ref;
    StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_exit);
        Searcher = findViewById(R.id.searchUsers);
        FileName = findViewById(R.id.portClearance);
        Cause = findViewById(R.id.Cause);
        Notes = findViewById(R.id.Notes);
        ShowOptions = findViewById(R.id.showOptions);
        Shown = findViewById(R.id.choosen);

        firstH = findViewById(R.id.exit);
        secondH = findViewById(R.id.enter);

        Parcelable parcelable = getIntent().getParcelableExtra("student");
        student = Parcels.unwrap(parcelable);

        school = student.getSchool();
        phone = student.getPhone();


        mStorageRef = FirebaseStorage.getInstance().getReference();

        long time=System.currentTimeMillis();
        Ref=mStorageRef.child("ParentsPermit").child(time+phone);

        setList();


    }

    private void setList() {
        Searcher.addTextChangedListener(new TextWatcher() {//מאזין לשינוי בהקלדת הטקסט
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.clear();//כשהטקסט משתנה המתאם ישתנה בהתאם לStrings שיכילו את מה שהוקלד
                }
                adapter.getFilter().filter(s);

            }


            @Override
            public void afterTextChanged(Editable s) {

            }


        });


        refSchool.child(school).child("Teacher").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Teachers.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    if (dsp.child("activated").getValue() != f) {
                        teacher = new Teacher();
                        teacher = dsp.getValue(Teacher.class);
                        String phone=teacher.getPhone();
                        String name = teacher.getName();
                        String cls = teacher.getCls();
                        String secondname = teacher.getSecondName();
                        String x = "מורה: " + name + " " + secondname + " " + cls+" "+ phone;
                        Teachers.add(x);

                    }
                }
                Demo.clear();
                Demo.addAll(Teachers);
                Adapt(Teachers);//פעולה שיוצרת מתאם עבור ה-arraylist ומקשרת בינו לבין ה-listView

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    private void Adapt(ArrayList<String> arrayList) {
        adapter = new TeachersAdapter(this, R.layout.user_list_unconfirmed, arrayList);
        ShowOptions.setAdapter(adapter);
    }

    public void ParentsApproval(View view) {
        pickFromGallery();
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    pickFromGallery();
                }
                else{
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void pickFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_PICK_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {//checking that an image have picked and that the image url and data is fine
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imguri = data.getData();
            upload();

        }
    }



    public void upload() {//This method doing the service action of uploading the file.
        Toast.makeText(this,"We are uploading your file...",Toast.LENGTH_SHORT).show();
        //the line above keeps the extension of the file and name it with his millis since the the UNIX epoch: (1970-01-01 00:00:00 UTC) a date
        // That makes sure that the first file that was uploaded will always remain the first and won't mix by the firebase order
        uploadTask=Ref.putFile(imguri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {//If the reference is right do:
                        // Get a URL to the uploaded content
                        Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();


                    }

                })
                .addOnFailureListener(new OnFailureListener() {//if the reference is wrong:
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }


    public class TeachersAdapter extends ArrayAdapter {//ה-class עבור המתאם המעוצב שיצרתי
        private int layout;

        public TeachersAdapter(@NonNull Context context, int resource, @NonNull List objects) {
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
            final String str = Teachers.get(position);
            final String[] Splitted = str.split(" ");

            String text = "";
            text = Splitted[0]+" "+Splitted[1]+" "+Splitted[2]+" "+Splitted[3] ;


            mainViewholder.details.setText(text);
            ViewGroup.LayoutParams params= mainViewholder.details.getLayoutParams();
            params.height= ViewGroup.LayoutParams.MATCH_PARENT;

            mainViewholder.remove.setVisibility(View.GONE);
            mainViewholder.approve.setText("בחר מורה");
            mainViewholder.approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Searcher.setText(str);
                    to=Splitted[4];
                }
            });
            return convertView;

        }
    }

        private Filter arrayfilter=new Filter() {//פעולה זו מסננת ומשאירה רק את שמות התלמידים שכוללים את רצף האותיות שנרשם בשדה הקלט
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<String> suggestions = new ArrayList<>();

                if (constraint == null || constraint.length() == 0||Searcher.getText().toString().isEmpty()) {
                    suggestions.addAll(Demo);
                } else {
                    String filterpattern = constraint.toString().toLowerCase().trim();
                    for (String x :Demo ) {
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

        public class ViewHolder {//רכיבי התצוגה שיועדו לlistview
                TextView details;
                Button approve, remove;
            }


    public void pickHourE(View view) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AskExit.this, new TimePickerDialog.OnTimeSetListener() {


            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                firstH.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
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
        mTimePicker = new TimePickerDialog(AskExit.this, new TimePickerDialog.OnTimeSetListener() {


            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                secondH.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void RequestBarcode(View view) {
        if(FileName.getText().toString().isEmpty()){
            FileName.setError("נא לרשום את האישור");
            FileName.requestFocus();
            return;
        }
        else {
            fileName = FileName.getText().toString();
        }
        if(Cause.getText().toString().isEmpty()){
            Cause.setError("נא לרשום את סיבת היציאה");
            Cause.requestFocus();
            return;
        }
        else {
            cause = Cause.getText().toString();
        }

        notes=Notes.getText().toString();
        ex=firstH.getText().toString();
        re=secondH.getText().toString();
        firstH.setText("שעת יציאה");
        secondH.setText("שעת חזרה");


    }
}
