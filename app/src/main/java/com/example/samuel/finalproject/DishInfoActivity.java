package com.example.samuel.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.regex.Pattern;

import static android.R.attr.button;
import static android.R.attr.x;
import static com.example.samuel.finalproject.HomeActivity.getUser;
import static com.example.samuel.finalproject.R.id.dish_name;
import static com.example.samuel.finalproject.R.id.email;

public class DishInfoActivity extends AppCompatActivity {

    private String dish_name_string;
    private int dish_id;
    private String user;
    private LinearLayout mShareLayout;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_info);

        dish_id = getIntent().getExtras().getInt("dish_id");
        dish_name_string = getIntent().getExtras().getString("dish_name");
        user = getIntent().getExtras().getString("user");
        mShareLayout = (LinearLayout) findViewById(R.id.share_friend);

        TextView dishText = (TextView) findViewById(R.id.dish_name);
        dishText.setText(dish_name_string);

        int j = getResources().getIdentifier("dish" + dish_id, "drawable", getPackageName());
        ImageView dishImage = (ImageView) findViewById(R.id.dish_image);
        dishImage.setImageResource(j);

        Button mSimilarButton = (Button) findViewById(R.id.similar_button);
        mSimilarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                find_similar_dishes();
            }
        });

        Button mHistoryButton = (Button) findViewById(R.id.eaten_button);
        mHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DishInfoActivity.EatenTask(user, dish_id).execute();
            }
        });


        final Button mShareButton = (Button) findViewById(R.id.share_button);
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShareLayout.getVisibility() == View.INVISIBLE) {
                    mShareLayout.setVisibility(View.VISIBLE);
                } else {
                    mShareLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        final EditText mShareTo = (EditText) findViewById(R.id.friend_email);
        final Button mSubmitShareButton = (Button) findViewById(R.id.confirm_share);
        mSubmitShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DishInfoActivity.ShareTask(mShareTo.getText().toString(), dish_id).execute();
            }
        });
    }

    private void find_similar_dishes() {
        Intent intent = new Intent(getApplicationContext(), SimilarDishActivity.class);
        intent.putExtra("dish_id", dish_id);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private class EatenTask extends AsyncTask<String, Void, String> {
        private final String mEmail;
        private final int mDish;

        EatenTask(String user, int dishID) {
            mEmail = user;
            mDish = dishID;
        }


        protected String doInBackground(String[] params) {
            // do not use 127.0.0.1, 127.0.0.1 refers to the emulator itself, use 10.0.2.2 instead
            String request = "http://10.0.2.2:5000/set_history?email=" + mEmail + "&dish=" + mDish;
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
                    Toast.makeText(DishInfoActivity.this, dish.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    continue;
                }
            }
        }
    }

    private class ShareTask extends AsyncTask<String, Void, String> {
        private final String mRecipient;
        private final int mDish;

        ShareTask(String user, int dishID) {
            mRecipient = user;
            mDish = dishID;
        }


        protected String doInBackground(String[] params) {
            // do not use 127.0.0.1, 127.0.0.1 refers to the emulator itself, use 10.0.2.2 instead
            String request = "http://10.0.2.2:5000/share_dish?dish_id=" + mDish + "&from_user=" + user + "&to_user=" + mRecipient;
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
                    if (dish.getString("message").equals("Share Success")) {
                        mShareLayout.setVisibility(View.INVISIBLE);
                    }
                    Toast.makeText(DishInfoActivity.this, dish.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    continue;
                }
            }
        }
    }
}
