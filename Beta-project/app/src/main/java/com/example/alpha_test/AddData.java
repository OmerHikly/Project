package com.example.alpha_test;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.alpha_test.FirebaseHelper.refUsers;

public class AddData extends AppCompatActivity implements AdapterView.OnItemClickListener {


    ArrayAdapter<String> adapter;


    EditText et;
    Button btn;
    ListView lv;

    AlertDialog.Builder adb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);


        et=findViewById(R.id.et);
        btn=findViewById(R.id.upload);
        lv=findViewById(R.id.lv);

        lv.setOnItemClickListener(this);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
        lv.setAdapter(adapter);



    }

UserData user;


    public void AddToLv(View view) {//adding the data to the firebase
        String st = et.getText().toString();

      user = new UserData(st);
      String localdata = user.getDatadata();
      refUsers.child(localdata).setValue(user);


        adapter.add(st);
        et.getText().clear();

    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view,  int position, long id) {//Data remove fro firebase

        final int x=position;
        adb=new AlertDialog.Builder(this);
        adb.setTitle("Item deletion");
        adb.setMessage("Do you want to delete this item?");
        adb.setPositiveButton("Let's delete!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteChild();
                adapter.remove(adapter.getItem(x));
            }
        });
        adb.setNegativeButton("No I regret", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        AlertDialog ad=adb.create();
        ad.show();
    }

    private void deleteChild() {
        refUsers.child(user.getDatadata()).removeValue();

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void AuthScreen (MenuItem item){
        Intent t = new Intent(this, MainActivity.class);
        startActivity(t);
    }

    public void RemoveScreen (MenuItem item){
        Intent t = new Intent(this, AddData.class);
        startActivity(t);

    }

    public void ImageScreen (MenuItem item){
        Intent t = new Intent(this, UploadPictures.class);
        startActivity(t);

    }

    public void ScanScreen (MenuItem item){
        Intent t = new Intent(this, BarcodeScan.class);
        startActivity(t);
    }



}
