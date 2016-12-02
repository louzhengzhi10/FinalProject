package com.example.samuel.finalproject;

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
 * Created by mengxiongliu on 13/11/2016.
 */

public class Utils {
    /**
     * HTTP request handler, catches all exceptions
     * @param request
     * @param method
     * @return response string
     */
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

    /**
     * Split json string
     * @param message
     * @return
     */
    private static String[] splitString(String message) {
        if (message == null || message.equals(""))
            return null;
        message = message.substring(message.indexOf("{"), message.lastIndexOf("}"));
        return message.split(Pattern.quote("}, "), Integer.MAX_VALUE);
    }

    /**
     * Parse message from backend server, return a list of dishes
     * @param message
     * @return
     */
    public static List<Dish> parseDishList(String message) {
        List<Dish> dishes = new ArrayList<>();
        try {
            String[] splits = splitString(message);
            for (String split : splits) {
                split = split.replace("\\", "");

                JSONObject dish = new JSONObject(split + "}");
                boolean liked;
                if (HomeActivity.isLiked(dish.getInt("id")))
                    liked = true;
                else
                    liked = false;
                if (dish.has("friend") && dish.has("message"))
                    dishes.add(new Dish(dish.getInt("id"), dish.getString("name"), (float) dish.getDouble("price"),
                            dish.getInt("restaurant_id"), dish.getString("restaurant_name"), liked,
                            dish.getString("friend"), dish.getString("message"), dish.getInt("notified") != 0));
                else if (dish.has("restaurant_id") && dish.has("restaurant_name"))
                    dishes.add(new Dish(dish.getInt("id"), dish.getString("name"), (float) dish.getDouble("price"),
                            dish.getInt("restaurant_id"), dish.getString("restaurant_name"), liked));
                else
                    dishes.add(new Dish(dish.getInt("id"), dish.getString("name"), (float) dish.getDouble("price"), liked));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return dishes;
    }

    /**
     * Parse message from backend server, return a list of restaurants
     * @param message
     * @return
     */
    public static List<Restaurant> parseRestaurantList(String message) {
        List<Restaurant> restaurants = new ArrayList<>();
        try {
            String[] splits = splitString(message);
            for (String split : splits) {
                split = split.replace("\\", "");
                JSONObject restaurant = new JSONObject(split + "}");
                restaurants.add(new Restaurant(restaurant.getInt("id"),
                        restaurant.getString("name"), restaurant.getString("address")));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return restaurants;
    }

    public static List<Comment> parseCommentList(String message) {
        List<Comment> comments = new ArrayList<>();
        try {
            String[] splits = splitString(message);
            for (String split : splits) {
                split = split.replace("\\", "");
                JSONObject comment = new JSONObject(split + "}");
                comments.add(new Comment(comment.getString("user"), comment.getString("date"), comment.getString("comment")));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return comments;
    }

    public static String parseMessage(String message) {
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
