package com.example.coronacheckcustomer;



import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.storage.FirebaseStorage;

import org.parceler.Parcels;

import static com.example.coronacheckcustomer.FBref.refUsers;


public class ChangePass extends AppCompatActivity {

    EditText NewPass;
    Button btn;

    Customer customer;


    Toolbar toolbar;
    String newpass1,newpass2;
    String phone;

    int UserType;
    boolean click = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        NewPass = findViewById(R.id.NewPass);
        btn=findViewById(R.id.b);

        Intent gi = getIntent();

        UserType = gi.getIntExtra("type", -1);
        toolbar=findViewById(R.id.tb);

        toolbar.setTitle("שינוי סיסמה");
        setSupportActionBar(toolbar);
    }

    public void change (View view){


        if (click) {
            newpass2 = NewPass.getText().toString();
            if (newpass2 == null || newpass2.isEmpty() || newpass2.equals(" ") || newpass2.equals("")) {
                NewPass.setError("חסרה ססמה");
                NewPass.requestFocus();
                return;
            }
            else
            if (!newpass1.equals(newpass2)) {
                Toast.makeText(getApplicationContext(), "אין התאמה בין שתי הסיסמאות שהוקלדו", Toast.LENGTH_SHORT).show();
            } else {

                Parcelable parcelable= getIntent().getParcelableExtra("student");
                customer = Parcels.unwrap(parcelable);
                phone = customer.getPhone();
                refUsers.child(phone).child("password").setValue(newpass1);
                customer.setPassword(newpass1);



                }
                click = !click;
                Toast.makeText(getApplicationContext(), "סיסמה שונתה בהצלחה", Toast.LENGTH_SHORT).show();

                newpass1=null;
                newpass2=null;
                NewPass.setText("");
                btn.setText("החלף סיסמה");


            }

         else {
            newpass1 = NewPass.getText().toString();
            if (newpass1 == null || newpass1.isEmpty() || newpass1.equals(" ") || newpass1.equals("")) {
                NewPass.setError("חסרה ססמה");
                NewPass.requestFocus();
                return;
            }
            else {
                Toast.makeText(getApplicationContext(), "יש להקליד סיסמה בשנית לאימות", Toast.LENGTH_SHORT).show();

                NewPass.setText("");
                btn.setText("אמת סיסמה");
                click = !click;
            }
        }






    }
}



