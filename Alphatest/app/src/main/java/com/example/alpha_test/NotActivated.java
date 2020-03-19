package com.example.alpha_test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NotActivated extends AppCompatActivity {
TextView tv;

int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_activated);
        tv=findViewById(R.id.Msg);

        Intent gi=getIntent();
        type=gi.getIntExtra("Type",-1);

        switch (type){
            case 0:
                tv.setText(" נא לפנות למחנך כדי שידאג לאשר אותך במערכת");
                break;
            case 1:
                tv.setText("נא לפנות לאדמין בית הספר כדי שיאשר אותך במערכת ");
                break;
            case 2:
                tv.setText("נא לפנות למתקין האפליקציה כדי שיאשר אותך במערכת");
                break;
            case 3:
                tv.setText("נא לפנות לאדמין בית הספר כדי שיאשר אותך במערכת ");
                break;
        }



    }
}
