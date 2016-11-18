package com.example.samuel.finalproject;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

import static android.os.Build.ID;

/**
 * Created by mengxiongliu on 13/11/2016.
 */

public class RestaurantListAdapter extends ArrayAdapter<Restaurant> {
    private List<Restaurant> restaurants;

    public RestaurantListAdapter(Activity context, int resource, List<Restaurant> restaurants) {
        super(context, resource, restaurants);
        this.restaurants = restaurants;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.restaurant_list, parent, false);
        }

        final Restaurant restaurant = restaurants.get(position);

        TextView nameText = (TextView) view.findViewById(R.id.restaurant_name);
        nameText.setText(restaurant.getName());
        TextView addressText = (TextView) view.findViewById(R.id.restaurant_address);
        addressText.setText(restaurant.getAddress());
        TextView NumDishText = (TextView) view.findViewById(R.id.restaurant_liked_dishes);

        // listener to on click event to like icon
        return view;
    }

    private class CountTask extends AsyncTask<String, Integer, String> {
        private final String mEmail;
        private final int mRestaurant;

        CountTask(String user, int restaurantID) {
            mEmail = user;
            mRestaurant = restaurantID;
        }


        protected String doInBackground(String[] params) {
            // do not use 127.0.0.1, 127.0.0.1 refers to the emulator itself, use 10.0.2.2 instead
            String request = "http://10.0.2.2:5000/liked_in_rest?email=" + mEmail + "&restaurant=" + mRestaurant;
            return Utils.sendHTTPRequest(request, "GET");
        }

        @Override
        protected void onPostExecute(String message) {
            message = message.substring(message.indexOf("{"), message.lastIndexOf("}"));
            String[] splits = message.split(Pattern.quote("}, "), Integer.MAX_VALUE);
            for (String split : splits) {
                split = split.replace("\\", "");
                try {
                    JSONObject dish = new JSONObject(split + "}");
                    dish.getInt("num_dishes");
                } catch (Exception e) {
                    continue;
                }
            }
        }
    }

}
