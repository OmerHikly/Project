package com.example.edittextsearch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class MainActivity extends AppCompatActivity {
    private static final String[] COUNTRIES = new String[]{
            "Afghanistan", "Albania", "Algeria", "Andorra", "Angola"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AutoCompleteTextView editText=findViewById(R.id.actv);
        ArrayAdapter<String> adpater=new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_1,COUNTRIES);
        editText.setAdapter(adpater);
    }
}
