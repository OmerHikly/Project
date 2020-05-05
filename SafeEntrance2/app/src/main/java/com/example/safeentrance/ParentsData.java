package com.example.safeentrance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.parceler.Parcels;

import java.util.concurrent.TimeUnit;

import static com.example.safeentrance.FBref.refAuth;
import static com.example.safeentrance.FBref.refSchool;

public class ParentsData extends AppCompatActivity {

    Button back,sign;

    Button Ver;//כפתור אימות
    EditText Code;//שדה הזנת קוד

    TextView tv,p1,p2;

    EditText name1,seondName1,Id1;
    EditText name2,seondName2,Id2;

    String I1;//משתנה שיאגור את מספר הת.ז של הורה אחד

    Student student,NewStudent;

    Parent one;
    Parent two;

    String Phone,school;
    String codeSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents_data);

        Ver=findViewById(R.id.Ver);
        sign=findViewById(R.id.sign_up);
        back=findViewById(R.id.back);

        tv=findViewById(R.id.textView);
        p1=findViewById(R.id.p1);
        p2=findViewById(R.id.p2);

        name1=findViewById(R.id.FirstName1);
        seondName1=findViewById(R.id.SecondName1);
        Id1=findViewById(R.id.Id1);
        name2=findViewById(R.id.FirstName2);
        seondName2=findViewById(R.id.SecondName2);
        Id2=findViewById(R.id.Id2);



        Parcelable parcelable = getIntent().getParcelableExtra("student");//קבלת עצם התלמיד מהאקטיביטים הקודמים
        student = Parcels.unwrap(parcelable);//קישורו אל העצם מסוג תלמיד שהגדרנו עבור המסך הזה

    }

    public void back(View view) {
        finish();
        Intent intento = new Intent(this, MainActivity.class);
        startActivity(intento);

    }

    public void Sign(View view) {
        String n1=name1.getText().toString();
        if (n1.isEmpty()) {
            name1.setError("נא לרשום שם של הורה אחד");
            name1.requestFocus();
            return;
        }
        String sn1=seondName1.getText().toString();
        if (sn1.isEmpty()) {
            seondName1.setError("נא לרשום שם משפחה");
            seondName1.requestFocus();
            return;
        }

        I1=Id1.getText().toString();
        String I2=Id2.getText().toString();
        if (I1.isEmpty()) {
            Id1.setError("נא לרשום תעודת זהות");
            Id1.requestFocus();
            return;
        }

        if (!checkId()||I1==I2||I2==student.getId()||I1==student.getId()) {//בדיקה האם תעודת הזהות תקינה ואם כן בדיקה נוספת שאין שתי ת.ז זהות בין המשתמשים
            Id1.setError("מספר ת.ז הוא לא הגיוני");
            Id1.requestFocus();
            return;
        }


        String Name1=n1+" "+sn1;
        String n2=name2.getText().toString();
        String sn2=seondName2.getText().toString();
        String Name2 =n2+" "+sn2;


        one=new Parent(Name1,I1,true);
        two=new Parent (Name2,I2,false);
        student.setParent1(one);
        student.setParent2(two);

        Phone=student.getPhone();
        school=student.getSchool();
        SendVerificationCode();

    }
    private void SendVerificationCode() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                Phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        //This method checks the state of the code sending and reacts for that.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Toast.makeText(getApplicationContext(), "הקוד נשלח אליך ברגעים אלו...", Toast.LENGTH_SHORT).show();
            SignInWithPhoneAuthCredential(phoneAuthCredential);
        }


        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), "הרשמה נכשלה", Toast.LENGTH_SHORT).show();


        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            //gets the code string and triggeres a method called verify
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText(getApplicationContext(), "הקוד נשלח...", Toast.LENGTH_SHORT).show();
            codeSent = s;
            verify();
        }


    };



    private void verify() {
        ChangeView1();
        Ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = Code.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
                SignInWithPhoneAuthCredential(credential);
            }
        });

    }



    private void ChangeView1() {
        name1.setVisibility(View.GONE);
        seondName1.setVisibility(View.GONE);
        Id1.setVisibility(View.GONE);
        name2.setVisibility(View.GONE);
        seondName2.setVisibility(View.GONE);
        Id2.setVisibility(View.GONE);
        p1.setVisibility(View.GONE);
        p2.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);
        back.setVisibility(View.GONE);
        sign.setVisibility(View.GONE);


        Code.setVisibility(View.VISIBLE);
        Ver.setVisibility(View.VISIBLE);
    }





    private void SignInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {//פעולה הרושמת את המשתמש החדש ל-firebase ובודקת אם הוא תלמיד מורה אדמין או שומר
        refAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            Toast.makeText(getApplicationContext(), "הנך משתמש חדש עכשיו!", Toast.LENGTH_LONG).show();


                                refSchool.child(school).child("Student").child(Phone).setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(ParentsData.this, "הנך משתמש חדש עכשיו!", Toast.LENGTH_LONG).show();
                                    }
                                });

                            }



                        SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("NotSigned", false);
                        editor.commit();
                        moveActivity();


                        task.getException();
                    }
                });

    }

    private boolean checkId() {
//   A method that checks for a legal id -ID in israel requires to multiply every digit in the  odd place by 1 and a digit in a
        //even place by 2 and sum up the result (if we get result that higher than 9 we need to sum those digits  and we use the given result instead)
        //after we got all those results from each digit in the id code - we sum up all the results and the given Result should be devided by 10.
        //That's what this method does
        String str = I1;
        if ((str.length() > 9) || (str.length() < 5)) {

            return false;
        }
        if (str.length() < 9) {
            while (str.length() < 9) {
                str = '0' + str;
            }
        }
        int sum = 0, incNum;
        for (int i = 0; i < 9; i++) {
            incNum = Integer.parseInt(String.valueOf(str.charAt(i)));
            incNum *= (i % 2) + 1;
            if (incNum > 9) {
                incNum = incNum % 10 + incNum / 10;
            }
            sum += incNum;
        }
        if (sum % 10 == 0) {
            return true;
        } else {
            return false;
        }
    }

    private void moveActivity() {
        finish();
        Intent intento = new Intent(this, LoginScreen.class);
        Parcelable parcelable = Parcels.wrap(student);
        intento.putExtra("student", parcelable);
        startActivity(intento);

    }
}
