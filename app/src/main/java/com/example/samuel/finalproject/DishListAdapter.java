package com.example.samuel.finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by mengxiongliu on 07/11/2016.
 */

public class DishListAdapter extends ArrayAdapter<Dish> {
    private List<Dish> dishes;
    private String user;
    private Activity context;

    public DishListAdapter(Activity context, int resource, List<Dish> dishes, String user) {
        super(context, resource, dishes);
        this.dishes = dishes;
        this.user = user;
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.dish_list, parent, false);
        }

        final Dish dish = dishes.get(position);

        TextView nameText = (TextView) view.findViewById(R.id.dish_name);
        nameText.setText(dish.getName());
        TextView priceText = (TextView) view.findViewById(R.id.dish_price);
        priceText.setText("$" + dish.getPrice());

        TextView restaurantText = (TextView) view.findViewById(R.id.dish_restaurant);

        // listener to on click event to restaurant name text
        if (context instanceof SimilarDishActivity || context instanceof HomeActivity) {
            restaurantText.setText(dish.getRestaurant_name());
            restaurantText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // start new restaurant activity
                    Intent intent = new Intent(context.getApplicationContext(), RestaurantActivity.class);
                    intent.putExtra("restaurant_id", dish.getRestaurant_id());
                    context.startActivity(intent);
                }
            });
        }


        ImageView likeView = (ImageView) view.findViewById(R.id.like_dish_icon);
        // listener to on click event to like icon
        likeView.setOnClickListener(new View.OnClickListener() {
            private boolean liked = false;

            @Override
            public void onClick(View v) {
                LikeDishTask task = new LikeDishTask();
                String response = null;
                try {
                    // wait for response from server before moving on
                    if (!liked)
                        response = task.execute(user, Integer.toString(dish.getId()), "like").get();
                    else
                        response = task.execute(user, Integer.toString(dish.getId()), "unlike").get();
                    liked = !liked;
                }
                catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    /**
     * Asynchronous task used to send http request to backend server
     */
    public class LikeDishTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String[] params) {
            // do not use 127.0.0.1, 127.0.0.1 refers to the emulator itself, use 10.0.2.2 instead
            String request;
            // send like / unlike request
            if (params[2].equals("like"))
                request= "http://10.0.2.2:5000/like?email=" + params[0] + "&dish=" + params[1];
            else
                request = "http://10.0.2.2:5000/unlike?email=" + params[0] + "&dish=" + params[1];

            String message = Utils.sendHTTPRequest(request, "POST");

            try {
                message = message.substring(message.indexOf("{"), message.lastIndexOf("}") + 1).replace("\\", "");
                return new String(new JSONObject(message).getString("message"));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
