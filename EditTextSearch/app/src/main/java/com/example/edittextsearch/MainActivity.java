package com.example.edittextsearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<ExampleItem> mExampleList;

    private RecyclerView mRecyclerView;
    private ExampleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createExampleList();
        buildRecyclerView();

        EditText editText = findViewById(R.id.edittext);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());

            }
        });
    }





    private void createExampleList() {
        mExampleList = new ArrayList<>();
        mExampleList.add(new ExampleItem(R.drawable.ic_android, "One", "Line 2"));
        mExampleList.add(new ExampleItem(R.drawable.ic_audio, "Two", "Line 2"));
        mExampleList.add(new ExampleItem(R.drawable.ic_sun, "Three", "Line 2"));
        mExampleList.add(new ExampleItem(R.drawable.ic_android, "Four", "Line 2"));
        mExampleList.add(new ExampleItem(R.drawable.ic_audio, "Five", "Line 2"));
        mExampleList.add(new ExampleItem(R.drawable.ic_sun, "Six", "Line 2"));
        mExampleList.add(new ExampleItem(R.drawable.ic_android, "Seven", "Line 2"));
        mExampleList.add(new ExampleItem(R.drawable.ic_audio, "Eight", "Line 2"));
        mExampleList.add(new ExampleItem(R.drawable.ic_sun, "Nine", "Line 2"));
    }
    private void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter(mExampleList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }
    }

    private void filter(String text) {
        ArrayList<ExampleItem> filteredList = new ArrayList<>();

        for (ExampleItem item : mExampleList) {
            if (item.getText1().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        mAdapter.filterList(filteredList);
        }
}