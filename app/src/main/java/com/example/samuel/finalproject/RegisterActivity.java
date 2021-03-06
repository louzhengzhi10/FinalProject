package com.example.samuel.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Samuel on 11/5/16.
 */

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import static android.Manifest.permission.READ_CONTACTS;
import static android.R.attr.id;

public class RegisterActivity extends Activity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mNameView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        mNameView = (EditText) findViewById(R.id.name);

        mNameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    final String email = mEmailView.getText().toString();
                    final String password = mPasswordView.getText().toString();
                    final String name = mNameView.getText().toString();
                    if (checkEmail(email) == false) {
                        Toast.makeText(RegisterActivity.this, "Your email address is invalid", Toast.LENGTH_SHORT).show();
                        return false;
                    } else if (checkPassword(password) == false) {
                        Toast.makeText(RegisterActivity.this, "Your password is invalid", Toast.LENGTH_SHORT).show();
                        return false;
                    } else if (checkName(name) == false) {
                        Toast.makeText(RegisterActivity.this, "Your name is invalid", Toast.LENGTH_SHORT).show();
                        return false;
                    } else {
                        new RegisterTask(email, password, name).execute();
                        return true;
                    }
                }
                return false;
            }
        });

        Button mEmailRegisterButton = (Button) findViewById(R.id.email_register_button);
        mEmailRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmailView.getText().toString();
                final String password = mPasswordView.getText().toString();
                final String name = mNameView.getText().toString();
                if (checkEmail(email) == false) {
                    Toast.makeText(RegisterActivity.this, "Your email address is invalid", Toast.LENGTH_SHORT).show();
                } else if (checkPassword(password) == false) {
                    Toast.makeText(RegisterActivity.this, "Your password is invalid", Toast.LENGTH_SHORT).show();
                } else if (checkName(name) == false) {
                    Toast.makeText(RegisterActivity.this, "Your name is invalid", Toast.LENGTH_SHORT).show();
                } else {
                    new RegisterTask(email, password, name).execute();
                }
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private boolean checkEmail(String email) {
        // Attack Prevention
        if (email.contains("?")) {
            return false;
        } else if (email.contains("/")){
            return false;
        } else if (email.contains("=")) {
            return false;
        } else if (email.contains(":")) {
            return false;
        } else {
            return email.contains("@") && email.contains(".");
        }

    }

    private boolean checkPassword(String password) {
        // Attack Prevention
        if (password.length() < 6) {
            return false;
        } else if (password.contains("@")) {
            return false;
        } else if (password.contains("?")) {
            return false;
        } else if (password.contains("/")){
            return false;
        } else if (password.contains("=")) {
            return false;
        } else if (password.contains(":")) {
            return false;
        }
        return true;
    }

    private boolean checkName(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return false;
            }
        }

        return true;
    }


    private class RegisterTask extends AsyncTask<String, Void, String> {
        private final String mEmail;
        private final String mPassword;
        private final String mName;

        RegisterTask(String email, String password, String name) {
            mEmail = email;
            mPassword = password;
            mName = name;
        }


        protected String doInBackground(String[] params) {
            // do not use 127.0.0.1, 127.0.0.1 refers to the emulator itself, use 10.0.2.2 instead
            String request = "http://10.0.2.2:5000/signup?email=" + mEmail + "&password=" + mPassword + "&name=" + mName.replace(" ", "+");
            return Utils.sendHTTPRequest(request, "POST");
        }

        @Override
        protected void onPostExecute(String message) {
            message = message.substring(message.indexOf("{"), message.lastIndexOf("}"));
            String[] splits = message.split(Pattern.quote("}, "), Integer.MAX_VALUE);
            for (String split : splits) {
                split = split.replace("\\", "");
                try {
                    JSONObject dish = new JSONObject(split + "}");
                    if (dish.getString("message").equals("Sign up success")) {
                        Intent fp = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(fp);
                    } else {
                        Toast.makeText(RegisterActivity.this, dish.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }
    }
}
