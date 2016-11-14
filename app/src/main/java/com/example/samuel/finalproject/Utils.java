package com.example.samuel.finalproject;

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
 * Created by mengxiongliu on 13/11/2016.
 */

public class Utils {
    public static String sendHTTPRequest(String request, String method) {
        try {
            URL url = new URL(request);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);

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

    private static String[] splitString(String message) {
        message = message.substring(message.indexOf("{"), message.lastIndexOf("}"));
        return message.split(Pattern.quote("}, "), Integer.MAX_VALUE);
    }

    public static List<Dish> parseDishList(String message) {
        List<Dish> dishes = new ArrayList<>();
        String[] splits = splitString(message);
        for (String split : splits) {
            split = split.replace("\\", "");
            try {
                JSONObject dish = new JSONObject(split + "}");
                boolean liked;
                if (HomeActivity.isLiked(dish.getInt("id")))
                    liked = true;
                else
                    liked = false;
                if (dish.has("restaurant_id") && dish.has("restaurant_name"))
                    dishes.add(new Dish(dish.getInt("id"), dish.getString("name"), (float) dish.getDouble("price"),
                            dish.getInt("restaurant_id"), dish.getString("restaurant_name"), liked));
                else
                    dishes.add(new Dish(dish.getInt("id"), dish.getString("name"), (float) dish.getDouble("price"), liked));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dishes;
    }

    public static List<Restaurant> parseRestaurantList(String message) {
        List<Restaurant> restaurants = new ArrayList<>();
        String[] splits = splitString(message);
        for (String split : splits) {
            split = split.replace("\\", "");
            try {
                JSONObject restaurant = new JSONObject(split + "}");
                restaurants.add(new Restaurant(restaurant.getInt("id"),
                        restaurant.getString("name"), restaurant.getString("address")));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return restaurants;
    }
}
