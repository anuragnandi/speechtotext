package com.example.wri_collectapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {


    String spellList[] = {"hello","how are you ?"};
    ListView  listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list_item);//might have issue here
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(    this,R.layout.list_view,R.id.textView,spellList);
        listView.setAdapter(arrayAdapter);

    }
}