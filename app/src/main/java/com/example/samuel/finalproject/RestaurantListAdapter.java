package com.example.samuel.finalproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

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

        ImageView likeView = (ImageView) view.findViewById(R.id.like_restaurant_icon);
        // listener to on click event to like icon
        return view;
    }

}
