package com.example.samuel.finalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.regex.Pattern;

import static android.R.attr.button;
import static android.R.attr.theme;
import static android.R.attr.x;
import static com.example.samuel.finalproject.HomeActivity.getUser;
import static com.example.samuel.finalproject.R.id.dish_name;
import static com.example.samuel.finalproject.R.id.email;

public class DishInfoActivity extends AppCompatActivity implements Serializable {
    private Dish dish;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_info);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        catch(NullPointerException ex) {
            ex.printStackTrace();
        }

        dish = (Dish) getIntent().getExtras().getSerializable("dish");

        TextView dishText = (TextView) findViewById(R.id.dish_name);
        dishText.setText(dish.getName());

        int j = getResources().getIdentifier("dish" + dish.getId(), "drawable", getPackageName());
        ImageView dishImage = (ImageView) findViewById(R.id.dish_image);
        dishImage.setImageResource(j);

        Button mHistoryButton = (Button) findViewById(R.id.eaten_button);
        mHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DishInfoActivity.EatenTask(dish.getId()).execute();
            }
        });

        final Button mShareButton = (Button) findViewById(R.id.share_button);
        // create share dialog on click
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareDialogFragment dialog = new ShareDialogFragment();
                Bundle args = new Bundle();
                args.putSerializable("activity", DishInfoActivity.this);
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), "Share Dialog");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dish, menu);
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
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("user", HomeActivity.getUser());
                startActivity(intent);
                break;
            case R.id.action_similar_dish:
                intent = new Intent(this, SimilarDishActivity.class);
                intent.putExtra("dish", dish);
                startActivity(intent);
                break;
            case R.id.action_sign_out:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * Called by share dialog fragment, necessary because static fragment cannot create asynchronous tasks
     * @param to_user
     * @param message
     */
    public void startShareTask(String to_user, String message) {
        try {
            new ShareTask(to_user, dish.getId(), message).execute().get();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send history request to backend server
     */
    private class EatenTask extends AsyncTask<String, Void, String> {
        private final int mDish;

        EatenTask(int dishID) {
            mDish = dishID;
        }

        @Override
        protected String doInBackground(String[] params) {
            // do not use 127.0.0.1, 127.0.0.1 refers to the emulator itself, use 10.0.2.2 instead
            String request = "http://10.0.2.2:5000/set_history?user=" + HomeActivity.getUser() + "&dish=" + mDish;
            return Utils.sendHTTPRequest(request, "POST");
        }

        @Override
        protected void onPostExecute(String message) {
            message = Utils.parseMessage(message);
            Toast.makeText(DishInfoActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Send share dish request to backend server
     */
    public class ShareTask extends AsyncTask<String, Void, String> {
        private final String mRecipient;
        private final int mDish;
        private final String mMessage;

        ShareTask(String user, int dishID, String message) {
            mRecipient = user;
            mDish = dishID;
            mMessage = new String(message);
        }

        @Override
        protected String doInBackground(String[] params) {
            // do not use 127.0.0.1, 127.0.0.1 refers to the emulator itself, use 10.0.2.2 instead
            String request = "http://10.0.2.2:5000/share_dish?dish=" + mDish + "&from_user=" + HomeActivity.getUser() + "&to_user=" + mRecipient + "&message=" + mMessage.replace(" ", "+");
            return Utils.sendHTTPRequest(request, "POST");
        }

        @Override
        protected void onPostExecute(String message) {
            Toast.makeText(DishInfoActivity.this, Utils.parseMessage(message), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Share dish dialog, required to be static by android
     */
    public static class ShareDialogFragment extends DialogFragment {
        private DishInfoActivity activity;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            activity = (DishInfoActivity) getArguments().getSerializable("activity");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialog_share, null);

            builder.setView(dialogView);

            // start share task
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    EditText username = (EditText) dialogView.findViewById(R.id.share_dialog_username);
                    EditText message = (EditText) dialogView.findViewById(R.id.share_dialog_message);
                    activity.startShareTask(username.getText().toString(), message.getText().toString());
                }
            });
            // close dialog
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    DishInfoActivity.ShareDialogFragment.this.getDialog().cancel();
                }
            });
            return builder.create();
        }
    }
}
