package com.example.samuel.finalproject;

import android.app.Activity;
import android.content.Context;
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

    public DishListAdapter(RestaurantActivity context, int resource, List<Dish> dishes, String user) {
        super(context, resource, dishes);
        this.dishes = dishes;
        this.user = user;
    }

    @Override
    public View getView(int position, View context, ViewGroup parent) {
        if (context == null) {
            context = LayoutInflater.from(getContext()).inflate(R.layout.dish_list, parent, false);
        }

        final Dish dish = dishes.get(position);

        final TextView nameText = (TextView) context.findViewById(R.id.dishName);
        nameText.setText(dish.getName());
        TextView priceText = (TextView) context.findViewById(R.id.dishPrice);
        priceText.setText("$" + dish.getPrice());

        final ImageView likeView = (ImageView) context.findViewById(R.id.likeIcon);

        likeView.setOnClickListener(new View.OnClickListener() {
            private boolean liked = false;

            @Override
            public void onClick(View v) {
                LikeDishTask task = new LikeDishTask();
                String response = null;
                try {
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
        return context;
    }

    public class LikeDishTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String[] params) {
            // do not use 127.0.0.1, 127.0.0.1 refers to the emulator itself, use 10.0.2.2 instead
            String request;
            if (params[2].equals("like"))
                request= "http://10.0.2.2:5000/like?email=" + params[0] + "&dish=" + params[1];
            else
                request = "http://10.0.2.2:5000/unlike?email=" + params[0] + "&dish=" + params[1];
            try {
                URL url = new URL(request);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String message = response.toString();

                try {
                    message = message.substring(message.indexOf("{"), message.lastIndexOf("}") + 1).replace("\\", "");
                    return new String(new JSONObject(message).getString("message"));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;

            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
