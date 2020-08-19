package com.example.coronacheckcustomer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.example.coronacheckcustomer.FBref.refAuth;
import static com.example.coronacheckcustomer.FBref.refDb;
import static com.example.coronacheckcustomer.FBref.refUsers;



public class Register extends AppCompatActivity {

    Button Sign;// כפתור ההירשמות
    Button Ver;// כפתור אימות קוד

    EditText PhoneNum;//שדה קלט טלפון

    TextView log;//כתובית להרשמה:


    EditText Code;//שדה קלט קוד אימות

    EditText Password;
    EditText FirstName;
    EditText SecondName;
    EditText Id;
    EditText Vpass;//שדה קלט לאימות ססמה


    SpannableString ss;


    Customer customer;


    Boolean stayConnect, registered;


    String codeSent;
    String ClassAndNumber = null;
    String firstname;
    String secondname;
    String name;
    String id;
    String Phone;
    String pass;
    String vpass;


    ArrayList<Groups> groups=new ArrayList<>();
    ArrayList<Child> children=new ArrayList<>();


    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirstName = findViewById(R.id.FirstName);
        SecondName = findViewById(R.id.SecondName);
        Id = findViewById(R.id.Id);
        PhoneNum = findViewById(R.id.phone);
        Password = findViewById(R.id.password);
        Vpass=findViewById(R.id.VerificationPassword);


        Code = findViewById(R.id.et);
        Sign = findViewById(R.id.sign_up);

        Ver = findViewById(R.id.Ver);



        log=findViewById(R.id.log);

        stayConnect=false;
        registered=true;

        databaseReference = refDb.getReference();




        ss=new SpannableString(log.getText().toString());
        ClickableSpan cs=new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                finish();
                startActivity(new Intent(Register.this, Login.class));
            }
        };
        ss.setSpan(cs,17,25, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        log.setText(ss);
        log.setMovementMethod(LinkMovementMethod.getInstance());




    }








        public void SendData(View view) {//פעולה שאוספת את כל הנתונים שהוזנו ובודקת אותם ומתריאה בהתאם
            firstname = FirstName.getText().toString();
            secondname = SecondName.getText().toString();
            name=firstname+" "+secondname;
            id = Id.getText().toString();

            pass = Password.getText().toString();
            vpass = Vpass.getText().toString();


            if (firstname.isEmpty()) {
                FirstName.setError("נא לרשום שם");
                FirstName.requestFocus();
                return;
            }
            if (!Pattern.matches("['א-ת]+", firstname)) {
                FirstName.getText().clear();
                firstname = "";
                FirstName.setError("השם שלך צריך להכיל רק אותיות בעברית");
                FirstName.requestFocus();
                return;
            } else {


                if (secondname.isEmpty()) {
                    SecondName.setError("נא לרשום שם משפחה");
                    SecondName.requestFocus();
                    return;
                }
                if (!Pattern.matches("['א-ת]+", secondname)) {
                    SecondName.getText().clear();
                    SecondName.setError("השם שלך צריך להכיל רק אותיות בעברית");
                    SecondName.requestFocus();
                    return;


                } else {
                    if (id.isEmpty()) {
                        Id.setError("מספר ת.ז הוא חיוני");
                        Id.requestFocus();
                        return;
                    }
                    if (!Pattern.matches("[0-9]+", id)) {
                        Id.setError("נא לרשום רק ספרות");
                        Id.requestFocus();
                        return;
                    }
                    if (!checkId()) {
                        Id.setError("מספר ת.ז הוא לא הגיוני");
                        Id.requestFocus();
                        return;
                    } else {




                        if (pass.isEmpty()) {
                            Password.setError("סיסמה היא חיונית");
                            Password.requestFocus();
                            return;
                        }
                        if (!pass.equals(vpass)) {
                            Vpass.setError("סיסמה לא זהה");
                            Id.requestFocus();
                            return;
                        }


                    }
                }


            }




            PhoneAuthentication();//פעולה שמתחילה את תהליך ה-Authentication

        }

        private void PhoneAuthentication() {

            Phone = PhoneNum.getText().toString();
            if (Phone.isEmpty()) {//Validation of the phone
                Toast.makeText(getApplicationContext(), " לא הזנת מספר טלפון", Toast.LENGTH_SHORT).show();

            } else {
                if (Phone.length() < 10) {
                    Toast.makeText(getApplicationContext(), "מספר טלפון שגוי", Toast.LENGTH_SHORT).show();
                } else {

                    Phone = PhoneNum.getText().toString();


                    Phone = "+972" + Phone;


                    CheckIfPhoneExists();//פעולה שבודקת האם הבית ספר שהוזן קיים
                }

            }

        }


        private void CheckIfPhoneExists() {

            refUsers.orderByChild("phone").equalTo(Phone)
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Toast.makeText(getApplicationContext(), "קיים במערכת", Toast.LENGTH_SHORT).show();
                            } else {
                                SendVerificationCode();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
        });
        }


        private boolean checkId() {
//   A method that checks for a legal id -ID in israel requires to multiply every digit in the  odd place by 1 and a digit in a
            //even place by 2 and sum up the result (if we get result that higher than 9 we need to sum those digits  and we use the given result instead)
            //after we got all those results from each digit in the id code - we sum up all the results and the given Result should be devided by 10.
            //That's what this method does
            String str = id;
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
            FirstName.setVisibility(View.GONE);
            SecondName.setVisibility(View.GONE);
            Id.setVisibility(View.GONE);
            PhoneNum.setVisibility(View.GONE);
            Password.setVisibility(View.GONE);
            Vpass.setVisibility(View.GONE);
            Sign.setVisibility(View.GONE);
            log.setVisibility(View.GONE);




            Code.setVisibility(View.VISIBLE);
            Ver.setVisibility(View.VISIBLE);
        }





        private void SignInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {//פעולה הרושמת את המשתמש החדש ל-firebase ובודקת אם הוא תלמיד מורה אדמין או שומר
            refAuth.signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Customer customer = new Customer(Phone,  id, name,pass,firstname, null, null);

                                refUsers.child(Phone).setValue(customer).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(Register.this, "הנך משתמש חדש עכשיו!", Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                            SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("NSigned", false);
                            editor.commit();
                            moveActivity();


                            task.getException();
                        }
                    });

        }

    private void moveActivity() {
        finish();
        Intent intento = new Intent(this, Login.class);
        Parcelable parcelable = Parcels.wrap(customer);
        intento.putExtra("customer", parcelable);
        startActivity(intento);
    }


}

