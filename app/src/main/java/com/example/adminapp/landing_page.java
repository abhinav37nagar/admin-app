package com.example.adminapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class landing_page extends AppCompatActivity {

    public String curr_item = "";
    String[] item = {"csd 505", "mech 102", "cse 502", "cse 203", "csd 504"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        autoCompleteTextView = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, item);

        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                curr_item = item;
                Toast.makeText(landing_page.this, "Subject " + item, Toast.LENGTH_SHORT).show();
                openCameraAndVerify();
            }
        });
    }

    public void openCameraAndVerify() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}