package com.example.samuel.finalproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Created by mengxiongliu on 05/11/2016.
 */

public class RestaurantActivity extends AppCompatActivity {
    private int id;
    private String user;
    private ListView listView;
    private List<Dish> dishes;
    private List<Restaurant> restaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        id = getIntent().getExtras().getInt("restaurant_id");
        user = getIntent().getExtras().getString("user");

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        catch(NullPointerException e) {
            e.printStackTrace();
        }

        try {
            user = getIntent().getExtras().getString("user");
        }
        catch (Exception e) {
            user = "mliu60@illinois.edu";
        }

        // search for restaurant menu
        new SearchMenuTask().execute("Menu");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_restaurant, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
            case R.id.action_menu:
                new SearchMenuTask().execute("Menu");
                break;
            case R.id.action_recommendation:
                new SearchMenuTask().execute("Recommendation");
                break;
            case R.id.action_similar_restaurant:
                new SimilarRestaurantTask().execute();
            default:
                break;
        }
        return true;
    }

    private void refreshRestaurantListView() {
        RestaurantListAdapter adapter = new RestaurantListAdapter(this, R.layout.restaurant_list, restaurants, user);
        listView = (ListView)findViewById(R.id.restaurant_activity_list);
        listView.setAdapter(adapter);
        // listener to on click event on dish button
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
                intent.putExtra("restaurant_id", restaurants.get(position).getId());
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    private class SimilarRestaurantTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            String request;
            request = "http://10.0.2.2:5000/similar_restaurant?restaurant=" + id;
            return Utils.sendHTTPRequest(request, "GET");
        }

        /**
         * Parse response from server, and refresh list view
         * @param message
         */
        @Override
        protected void onPostExecute(String message) {
            restaurants = Utils.parseRestaurantList(message);
            refreshRestaurantListView();
        }
    }

    /**
     * Generate list view, called on post execute
     */
    private void refreshDishListView() {
        DishListAdapter adapter = new DishListAdapter(this, R.layout.dish_list, dishes, user);
        listView = (ListView)findViewById(R.id.restaurant_activity_list);
        listView.setAdapter(adapter);
        // listener to on click event on dish button
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

    /**
     * Asynchronous task used to send http request to backend server
     */
    private class SearchMenuTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            // do not use 127.0.0.1, 127.0.0.1 refers to the emulator itself, use 10.0.2.2 instead
            String request;
            // send get_dish / recommend_dish request
            if (params[0].equals("Menu"))
                request = "http://10.0.2.2:5000/get_dish?restaurant=" + id;
            else
                request = "http://10.0.2.2:5000/recommend_dish?user=" + user + "&restaurant=" + id;

            return Utils.sendHTTPRequest(request, "GET");
        }

        /**
         * Parse response from server, and refresh list view
         * @param message
         */
        @Override
        protected void onPostExecute(String message) {
            dishes = Utils.parseDishList(message);
            // refresh list view
            refreshDishListView();
        }
    }
}
