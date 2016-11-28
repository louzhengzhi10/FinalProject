package com.example.samuel.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by mengxiongliu on 07/11/2016.
 */

public class DishListAdapter extends ArrayAdapter<Dish> {
    private List<Dish> dishes;
    private Activity context;
    private boolean deletable;
    private String deleteType;

    public DishListAdapter(Activity context, int resource, List<Dish> dishes, boolean deletable, String deleteType) {
        super(context, resource, dishes);
        this.dishes = dishes;
        this.context = context;
        this.deletable = deletable;
        this.deleteType = deleteType;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.dish_list, parent, false);
        }

        final Dish dish = dishes.get(position);

        TextView nameText = (TextView) view.findViewById(R.id.dish_name);
        nameText.setText(dish.getName());
        TextView priceText = (TextView) view.findViewById(R.id.dish_price);
        priceText.setText(NumberFormat.getCurrencyInstance().format(dish.getPrice()));

        TextView restaurantText = (TextView) view.findViewById(R.id.dish_restaurant);

        // listener to on click event to restaurant name text
        if (dish.getRestaurant_name() != null) {
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

        TextView sharedByText = (TextView) view.findViewById(R.id.shared_from);
        TextView shareMessage = (TextView) view.findViewById(R.id.share_message);
        if (dish.getMessage() != null && dish.getShared_by() != null) {
            sharedByText.setText(dish.getShared_by());
            shareMessage.setText(dish.getMessage());
        }

        setLikeView(view, dish);

        ImageView deleteView = (ImageView) view.findViewById(R.id.delete_dish_icon);
        if (deletable) {
            deleteView.setVisibility(View.VISIBLE);
            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String response = null;
                    if (deleteType != null) {
                        String request = "http://10.0.2.2:5000/remove_" + deleteType + "?user=" + HomeActivity.getUser() + "&dish=" + dish.getId();
                        try {
                            response = new DeleteTask().execute(request, "POST").get();
                        }
                        catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    dishes.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
        else
            deleteView.setVisibility(View.INVISIBLE);

        return view;
    }

    private void setLikeView(View view, final Dish dish) {
        ImageView likeView = (ImageView) view.findViewById(R.id.like_dish_icon);
        if (dish.isLiked())
            likeView.setImageResource(R.drawable.liked);
        else
            likeView.setImageResource(R.drawable.like);
        // listener to on click event to like icon
        likeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LikeDishTask task = new LikeDishTask();
                String response = null;
                try {
                    // wait for response from server before moving on
                    if (!dish.isLiked()) {
                        response = task.execute(HomeActivity.getUser(), Integer.toString(dish.getId()), "like").get();
                        HomeActivity.addToLikedID(dish.getId());
                        ((ImageView) v).setImageResource(R.drawable.liked);
                        dish.setLiked(true);

                    }
                    else {
                        response = task.execute(HomeActivity.getUser(), Integer.toString(dish.getId()), "unlike").get();
                        HomeActivity.removeFromLikeID(dish.getId());
                        ((ImageView) v).setImageResource(R.drawable.like);
                        dish.setLiked(false);
                    }
                }
                catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Asynchronous task used to send http request to backend server
     */
    private class LikeDishTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            // do not use 127.0.0.1, 127.0.0.1 refers to the emulator itself, use 10.0.2.2 instead
            String request;
            // send like / unlike request
            if (params[2].equals("like"))
                request= "http://10.0.2.2:5000/like?email=" + params[0] + "&dish=" + params[1];
            else
                request = "http://10.0.2.2:5000/unlike?email=" + params[0] + "&dish=" + params[1];

            String message = Utils.sendHTTPRequest(request, "POST");

            return Utils.parseMessage(message);
        }
    }

    private class DeleteTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            String message = Utils.sendHTTPRequest(params[0], params[1]);
            return Utils.parseMessage(message);
        }
    }

}
