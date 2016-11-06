package com.example.samuel.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by mengxiongliu on 05/11/2016.
 */

public class Restaurant extends AppCompatActivity {
    private ListView listView;
    private String[] text = {"Chipotle", "McDonald's"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch(NullPointerException ex) {
            ex.printStackTrace();
        }

        CustomList adapter = new CustomList(Restaurant.this, R.layout.custom_list, text);
        listView = (ListView)findViewById(R.id.dish_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Restaurant.this, "You Clicked at " + text[position], Toast.LENGTH_SHORT).show();
            }
        });
    }
}
