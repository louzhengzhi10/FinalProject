package com.example.samuel.finalproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by mengxiongliu on 08/11/2016.
 */

public class SimilarDishActivity extends AppCompatActivity {
    private int id;
    private String user;
    private ListView listView;
    private List<Dish> dishes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_dish);

        id = getIntent().getExtras().getInt("dish_id");
        user = getIntent().getExtras().getString("user");

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        catch(NullPointerException ex) {
            ex.printStackTrace();
        }

        new SimilarDishTask().execute();
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }


    /**
     * Generate list view, called on post execute
     */
    private void refreshListView() {
        DishListAdapter adapter = new DishListAdapter(this, R.layout.dish_list, dishes, user);
        listView = (ListView)findViewById(R.id.similar_dish_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), SimilarDishActivity.class);
                intent.putExtra("dish_id", dishes.get(position).getId());
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    private class SimilarDishTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String[] params) {
            // do not use 127.0.0.1, 127.0.0.1 refers to the emulator itself, use 10.0.2.2 instead
            String request = "http://10.0.2.2:5000/similar_dish?dish=" + id;
            return Utils.sendHTTPRequest(request, "GET");
        }

        @Override
        protected void onPostExecute(String message) {
            dishes = Utils.parseDishList(message);
            refreshListView();
        }
    }
}
