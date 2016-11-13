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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        catch(NullPointerException ex) {
            ex.printStackTrace();
        }
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
            case R.id.action_restaurant:
                onRestaurantSelected();
                break;
            case R.id.action_liked_dish:
                onLikedDishSelected();
                break;
            default:
                break;
        }
        return true;
    }

    private void onRestaurantSelected() {
        searchText.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.VISIBLE);
        restaurants = new ArrayList<>();
        refreshRestaurantListView();
    }

    private void onLikedDishSelected() {
        searchText.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.INVISIBLE);
        new LikedDishTask().execute();
    }

    private void refreshRestaurantListView() {
        RestaurantListAdapter adapter = new RestaurantListAdapter(this, R.layout.restaurant_list, restaurants, "mliu60@illinois.edu");
        listView = (ListView) findViewById(R.id.home_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
                intent.putExtra("restaurant_id", restaurants.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void refreshDishListView() {
        DishListAdapter adapter = new DishListAdapter(this, R.layout.dish_list, dishes, "mliu60@illinois.edu");
        listView = (ListView) findViewById(R.id.home_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), SimilarDishActivity.class);
                intent.putExtra("dish_id", dishes.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private String sendRequest(String request) {
        try {
            URL url = new URL(request);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String[] splitString(String message) {
        message = message.substring(message.indexOf("{"), message.lastIndexOf("}"));
        return message.split(Pattern.quote("}, "), Integer.MAX_VALUE);
    }

    private class RestaurantTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            String request = "http://10.0.2.2:5000/match_restaurant?restaurant=" + params[0];
            return sendRequest(request);
        }

        @Override
        protected void onPostExecute(String message) {
            restaurants = new ArrayList<>();
            String[] splits = splitString(message);
            for (String split : splits) {
                split = split.replace("\\", "");
                try {
                    JSONObject restaurnat = new JSONObject(split + "}");
                    restaurants.add(new Restaurant(restaurnat.getInt("id"),
                            restaurnat.getString("name"), restaurnat.getString("address")));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            refreshRestaurantListView();
        }

    }

    private class LikedDishTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            // do not use 127.0.0.1, 127.0.0.1 refers to the emulator itself, use 10.0.2.2 instead
            String request = "http://10.0.2.2:5000/liked_dish?user=" + user;
            return sendRequest(request);
        }

        @Override
        protected void onPostExecute(String message) {
            dishes = new ArrayList<>();
            String[] splits = splitString(message);
            for (String split : splits) {
                split = split.replace("\\", "");
                try {
                    JSONObject dish = new JSONObject(split + "}");
                    dishes.add(new Dish(dish.getInt("id"), dish.getString("name"), (float) dish.getDouble("price"),
                            dish.getInt("restaurant_id"), dish.getString("restaurant_name")));
                } catch (Exception e) {
                    continue;
                }
            }
            refreshDishListView();
        }
    }
}

