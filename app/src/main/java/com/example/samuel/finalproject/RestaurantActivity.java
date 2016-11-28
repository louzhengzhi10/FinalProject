package com.example.samuel.finalproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static android.R.id.message;


/**
 * Created by mengxiongliu on 05/11/2016.
 */

public class RestaurantActivity extends AppCompatActivity implements Serializable {
    private int id;
    private ListView listView;
    private List<Dish> dishes;
    private List<Restaurant> restaurants;
    private List<Comment> comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        id = getIntent().getExtras().getInt("restaurant_id");

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        catch(NullPointerException e) {
            e.printStackTrace();
        }

        FloatingActionButton commentButton = (FloatingActionButton) findViewById(R.id.comment_button);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentDialogFragment dialog = new CommentDialogFragment();
                Bundle args = new Bundle();
                args.putSerializable("activity", RestaurantActivity.this);
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), "Comment Dialog");
            }
        });

        // search for restaurant menu
        new SearchMenuTask().execute("Menu");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_restaurant, menu);
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
            case R.id.action_home:
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("user", HomeActivity.getUser());
                startActivity(intent);
                break;
            case R.id.action_menu:
                new SearchMenuTask().execute("Menu");
                break;
            case R.id.action_recommendation:
                new SearchMenuTask().execute("Recommendation");
                break;
            case R.id.action_comment:
                new SearchCommentTask().execute();
                break;
            case R.id.action_similar_restaurant:
                new SimilarRestaurantTask().execute();
                break;
            case R.id.action_sign_out:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

    public void startAddCommentTask(String comment) {
        try {
            new AddCommentTask().execute(comment).get();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class CommentDialogFragment extends DialogFragment {
        private RestaurantActivity activity;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            activity = (RestaurantActivity) getArguments().getSerializable("activity");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialog_comment, null);

            builder.setView(dialogView);

            builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    EditText message = (EditText) dialogView.findViewById(R.id.comment_dialog_message);
                    activity.startAddCommentTask(message.getText().toString());
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    RestaurantActivity.CommentDialogFragment.this.getDialog().cancel();
                }
            });
            return builder.create();
        }
    }

    private void refreshRestaurantListView() {
        RestaurantListAdapter adapter = new RestaurantListAdapter(this, R.layout.restaurant_list, restaurants, false);
        listView = (ListView)findViewById(R.id.restaurant_activity_list);
        listView.setAdapter(adapter);
        // listener to on click event on dish button
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
                intent.putExtra("restaurant_id", restaurants.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private class SimilarRestaurantTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            String request;
            request = "http://10.0.2.2:5000/similar_restaurant?restaurant=" + id;
            return Utils.sendHTTPRequest(request, "GET");
        }

        /**
         * Parse response from server, and refresh list view
         * @param message
         */
        @Override
        protected void onPostExecute(String message) {
            restaurants = Utils.parseRestaurantList(message);
            refreshRestaurantListView();
        }
    }

    /**
     * Generate list view, called on post execute
     */
    private void refreshDishListView() {
        DishListAdapter adapter = new DishListAdapter(this, R.layout.dish_list, dishes, false, null);
        listView = (ListView)findViewById(R.id.restaurant_activity_list);
        listView.setAdapter(adapter);
        // listener to on click event on dish button
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DishInfoActivity.class);
                intent.putExtra("dish", dishes.get(position));
                intent.putExtra("user", HomeActivity.getUser());
                startActivity(intent);
            }
        });
    }

    /**
     * Asynchronous task used to send http request to backend server
     */
    private class SearchMenuTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            // do not use 127.0.0.1, 127.0.0.1 refers to the emulator itself, use 10.0.2.2 instead
            String request;
            // send get_dish / recommend_dish request
            if (params[0].equals("Menu"))
                request = "http://10.0.2.2:5000/get_dish?restaurant=" + id;
            else if (params[0].equals("Recommendation"))
                request = "http://10.0.2.2:5000/recommend_dish?user=" + HomeActivity.getUser() + "&restaurant=" + id;
            else
                request = "http://10.0.2.2:5000/recommend_dish?user=" + HomeActivity.getUser() + "&restaurant=" + id + "&limit=3";

            return Utils.sendHTTPRequest(request, "GET");
        }

        /**
         * Parse response from server, and refresh list view
         * @param message
         */
        @Override
        protected void onPostExecute(String message) {
            dishes = Utils.parseDishList(message);
            // refresh list view
            refreshDishListView();
        }
    }

    private void refreshCommentListView() {
        CommentListAdapter adapter = new CommentListAdapter(this, R.layout.comment_list, comments, id);
        listView = (ListView)findViewById(R.id.restaurant_activity_list);
        listView.setAdapter(adapter);
    }

    private class AddCommentTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            String request = "http://10.0.2.2:5000/add_comment?user=" + HomeActivity.getUser() + "&restaurant=" + id + "&comment=" + params[0].replace(" ", "+");
            return Utils.sendHTTPRequest(request, "POST");
        }
    }

    private class SearchCommentTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            String request = "http://10.0.2.2:5000/get_comment?restaurant=" + id;
            return Utils.sendHTTPRequest(request, "GET");
        }

        @Override
        protected void onPostExecute(String message) {
            comments = Utils.parseCommentList(message);
            refreshCommentListView();
        }
    }
}
