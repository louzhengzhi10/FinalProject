package com.example.samuel.finalproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by mengxiongliu on 10/11/2016.
 */

public class HomeActivity extends AppCompatActivity {
    private String user = "mliu60@illinois.edu";
    private ListView listView;
    private List<Dish> dishes;
    private List<Restaurant> restaurants;
    private boolean search_restaurant = true;
    private EditText searchText;
    private ImageView searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        searchText = (EditText) findViewById(R.id.search_txt);
        searchButton = (ImageView) findViewById(R.id.search_btn);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        catch(NullPointerException e) {
            e.printStackTrace();
        }

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (search_restaurant)
                    new SearchRestaurantTask().execute(charSequence.toString());
                else
                    new SearchDishTask().execute(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        onSearchRestaurantSelected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
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
            case R.id.action_search_restaurant:
                onSearchRestaurantSelected();
                break;
            case R.id.action_search_dish:
                onSearchDishSelected();
                break;
            case R.id.action_liked_dish:
                onLikedDishSelected();
                break;
            default:
                break;
        }
        return true;
    }

    private void onSearchRestaurantSelected() {
        searchText.setVisibility(View.VISIBLE);
        searchText.setHint("Restaurant");
        searchButton.setVisibility(View.VISIBLE);
        restaurants = new ArrayList<>();
        search_restaurant = true;
        refreshRestaurantListView();
    }

    private void onSearchDishSelected() {
        searchText.setVisibility(View.VISIBLE);
        searchText.setHint("Dish");
        searchButton.setVisibility(View.VISIBLE);
        dishes = new ArrayList<>();
        search_restaurant = false;
        refreshDishListView();
    }

    private void onLikedDishSelected() {
        searchText.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.INVISIBLE);
        new LikedDishTask().execute();
    }

    private void refreshRestaurantListView() {
        RestaurantListAdapter adapter = new RestaurantListAdapter(this, R.layout.restaurant_list, restaurants, user);
        listView = (ListView) findViewById(R.id.home_list);
        listView.setAdapter(adapter);
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

    private void refreshDishListView() {
        DishListAdapter adapter = new DishListAdapter(this, R.layout.dish_list, dishes, user);
        listView = (ListView) findViewById(R.id.home_list);
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

    private class SearchRestaurantTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            String request = "http://10.0.2.2:5000/match_restaurant?restaurant=" + params[0].replace(" ", "+");
            return Utils.sendHTTPRequest(request, "GET");
        }

        @Override
        protected void onPostExecute(String message) {
            restaurants = Utils.parseRestaurantList(message);
            refreshRestaurantListView();
        }
    }

    private class SearchDishTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            String request = "http://10.0.2.2:5000/match_dish?dish=" + params[0].replace(" ", "+");
            return Utils.sendHTTPRequest(request, "GET");
        }

        @Override
        protected void onPostExecute(String message) {
            dishes = Utils.parseDishList(message);
            refreshDishListView();
        }
    }


    private class LikedDishTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            // do not use 127.0.0.1, 127.0.0.1 refers to the emulator itself, use 10.0.2.2 instead
            String request = "http://10.0.2.2:5000/liked_dish?user=" + user;
            return Utils.sendHTTPRequest(request, "GET");
        }

        @Override
        protected void onPostExecute(String message) {
            dishes = Utils.parseDishList(message);
            refreshDishListView();
        }
    }
}

