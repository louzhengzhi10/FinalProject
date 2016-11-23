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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by mengxiongliu on 10/11/2016.
 */

public class HomeActivity extends AppCompatActivity {
    // used for like, unlike icon in dish list view, accessed from all other activities
    public static HashSet<Integer> likeID;
    private static String user;

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

        user = getIntent().getExtras().getString("user");
        likeID = new HashSet<>();

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

        // match restaurant or dish on text change
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

        // initialize likedID
        try {
            new LikedIDTask().execute().get();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        onSearchRestaurantSelected();
    }

    public static String getUser() {
        return user;
    }

    public static void addToLikedID(int id) {
        likeID.add(id);
    }

    public static void removeFromLikeID(int id) {
        likeID.remove(id);
    }

    public static boolean isLiked(int id) {
        return likeID.contains(id);
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
            case R.id.action_history:
                onHistorySelected();
                break;
            case R.id.action_share:
                onShareSelected();
                break;
            case R.id.action_sign_out:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * Restaurant search item selected
     */
    private void onSearchRestaurantSelected() {
        searchText.setVisibility(View.VISIBLE);
        searchText.setHint("Restaurant");
        searchButton.setVisibility(View.VISIBLE);
        restaurants = new ArrayList<>();
        search_restaurant = true;
        refreshRestaurantListView();
    }

    /**
     * Dish search item selected
     */
    private void onSearchDishSelected() {
        searchText.setVisibility(View.VISIBLE);
        searchText.setHint("Dish");
        searchButton.setVisibility(View.VISIBLE);
        dishes = new ArrayList<>();
        search_restaurant = false;
        refreshDishListView(true);
    }

    /**
     * Liked dish item selected
     */
    private void onLikedDishSelected() {
        searchText.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.INVISIBLE);
        new LikedDishTask().execute();
    }

    /**
     * History item selected
     */
    private void onHistorySelected() {
        searchText.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.INVISIBLE);
        new HistoryTask().execute();
    }

    /**
     * Share item selected
     */
    private void onShareSelected() {
        searchText.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.INVISIBLE);
        new GetSharedTask().execute();
    }

    /**
     * Inflate list view with restaurants
     */
    private void refreshRestaurantListView() {
        RestaurantListAdapter adapter = new RestaurantListAdapter(this, R.layout.restaurant_list, restaurants);
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

    /**
     * Inflate list view with dishes
     */
    private void refreshDishListView(boolean deletable) {
        DishListAdapter adapter;
        adapter = new DishListAdapter(this, R.layout.dish_list, dishes, deletable);
        listView = (ListView) findViewById(R.id.home_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DishInfoActivity.class);
                intent.putExtra("dish", dishes.get(position));
                startActivity(intent);
            }
        });
    }

    /**
     * Match restaurants with name that contains the substring from backend server
     */
    private class SearchRestaurantTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            String request = "http://10.0.2.2:5000/match_restaurant?user=" + user + "&restaurant=" + params[0].replace(" ", "+");
            return Utils.sendHTTPRequest(request, "GET");
        }

        @Override
        protected void onPostExecute(String message) {
            restaurants = Utils.parseRestaurantList(message);
            refreshRestaurantListView();
        }
    }

    /**
     * Match dishes with name that contains the substring from backend server
     */
    private class SearchDishTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            String request = "http://10.0.2.2:5000/match_dish?dish=" + params[0].replace(" ", "+");
            return Utils.sendHTTPRequest(request, "GET");
        }

        @Override
        protected void onPostExecute(String message) {
            dishes = Utils.parseDishList(message);
            refreshDishListView(true);
        }
    }


    /**
     * Retrieve all relevant information of liked dishes from backend server
     */
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
            refreshDishListView(false);
        }
    }

    /**
     * Retrieve IDs of liked dishes from backend server
     */
    private class LikedIDTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            String request = "http://10.0.2.2:5000/liked_dish?user=" + user;
            String message = Utils.sendHTTPRequest(request, "GET");
            dishes = Utils.parseDishList(message);
            likeID = new HashSet<>();
            for (Dish dish : dishes)
                likeID.add(dish.getId());
            return null;
        }
    }

    /**
     * Retrieve history of user from backend server
     */
    private class HistoryTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            // do not use 127.0.0.1, 127.0.0.1 refers to the emulator itself, use 10.0.2.2 instead
            String request = "http://10.0.2.2:5000/get_history?user=" + user;
            return Utils.sendHTTPRequest(request, "GET");
        }

        @Override
        protected void onPostExecute(String message) {
            dishes = Utils.parseDishList(message);
            refreshDishListView(true);
        }
    }

    /**
     * Retrieve dishes shared to current user from backend server
     */
    private class GetSharedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            // do not use 127.0.0.1, 127.0.0.1 refers to the emulator itself, use 10.0.2.2 instead
            String request = "http://10.0.2.2:5000/get_shared?user=" + user;
            return Utils.sendHTTPRequest(request, "GET");
        }

        @Override
        protected void onPostExecute(String message) {
            dishes = Utils.parseDishList(message);
            refreshDishListView(true);
        }
    }


}

