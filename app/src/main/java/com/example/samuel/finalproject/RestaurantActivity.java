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
import android.widget.Toast;

import org.json.JSONException;
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
 * Created by mengxiongliu on 05/11/2016.
 */

public class RestaurantActivity extends AppCompatActivity {
    private int id;
    private String user = "mliu60@illinois.edu";
    private ListView listView;
    private List<Dish> dishes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch(NullPointerException e) {
            e.printStackTrace();
        }

        new SearchMenuTask().execute("Menu");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_restaurant, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_menu:
                new SearchMenuTask().execute("Menu");
                break;
            case R.id.action_recommendation:
                new SearchMenuTask().execute("Recommendation");
                break;
            default:
                break;
        }
        return true;
    }


    private void refreshListView() {
        DishListAdapter adapter = new DishListAdapter(this, R.layout.dish_list, dishes, "mliu60@illinois.edu");
        listView = (ListView)findViewById(R.id.dish_list);
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

    private class SearchMenuTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String[] params) {
            // do not use 127.0.0.1, 127.0.0.1 refers to the emulator itself, use 10.0.2.2 instead
            String request;
            if (params[0].equals("Menu"))
                request = "http://10.0.2.2:5000/get_dish?restaurant=" + id;
            else
                request = "http://10.0.2.2:5000/recommend_dish?user=" + user + "&restaurant=" + id;

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

        @Override
        protected void onPostExecute(String message) {
            dishes = new ArrayList<>();
            try {
                message = message.substring(message.indexOf("{"), message.lastIndexOf("}"));
                String[] splits = message.split(Pattern.quote("}, "), Integer.MAX_VALUE);
                for (String split : splits) {
                    split = split.replace("\\", "");
                    JSONObject dish = new JSONObject(split + "}");
                    dishes.add(new Dish(dish.getInt("id"), dish.getString("name"), (float) dish.getDouble("price")));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            refreshListView();
        }
    }
}
