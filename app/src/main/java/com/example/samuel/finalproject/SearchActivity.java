package com.example.samuel.finalproject;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

/**
 * Created by mengxiongliu on 06/11/2016.
 */

public class SearchActivity extends AppCompatActivity {
    ListView listView;
    public static String RESTAURANT_ID = "RESTAURANT_ID";
    public List<Restaurant> results;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listView = (ListView) findViewById(R.id.restaurant_list);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if(results.size() != 0) {
                    Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
                    Restaurant restaurant = results.get(position);
                    intent.putExtra(RESTAURANT_ID, restaurant.getId());
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private void doMySearch(String query){
    }

}
