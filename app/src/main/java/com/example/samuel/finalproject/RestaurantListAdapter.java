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
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import static android.os.Build.ID;

/**
 * Created by mengxiongliu on 13/11/2016.
 */

public class RestaurantListAdapter extends ArrayAdapter<Restaurant> {
    private List<Restaurant> restaurants;
    private boolean deletable;

    public RestaurantListAdapter(Activity context, int resource, List<Restaurant> restaurants, boolean deletable) {
        super(context, resource, restaurants);
        this.restaurants = restaurants;
        this.deletable = deletable;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.restaurant_list, parent, false);
        }

        final Restaurant restaurant = restaurants.get(position);

        TextView nameText = (TextView) view.findViewById(R.id.restaurant_name);
        nameText.setText(restaurant.getName());
        TextView addressText = (TextView) view.findViewById(R.id.restaurant_address);
        addressText.setText(restaurant.getAddress());
        TextView NumDishText = (TextView) view.findViewById(R.id.restaurant_liked_dishes);

        ImageView deleteView = (ImageView) view.findViewById(R.id.delete_restaurant_icon);
        if (deletable) {
            // set visible if deletable
            deleteView.setVisibility(View.VISIBLE);
            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // remove restaurant from list view
                    restaurants.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
        else
            deleteView.setVisibility(View.INVISIBLE);

        return view;
    }
}
