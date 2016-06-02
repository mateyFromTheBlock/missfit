package com.example.arono.missfit.Registration;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.arono.missfit.Activities.FeedActivity;
import com.example.arono.missfit.DataServerManagement.BackendUtility;
import com.example.arono.missfit.R;

public class LoginInActivity extends AppCompatActivity {


    private EditText etEmail,etPassword;
    private TextView tvSignIn;
    private Button btnLogIn;
    private Context c = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_in);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Backendless.initApp(this, BackendUtility.APPLIATION_ID, BackendUtility.APPLIATION_Key, BackendUtility.VERSION);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPaswword);
        btnLogIn = (Button) findViewById(R.id.btnLogIn);
        tvSignIn = (TextView) findViewById(R.id.tvSignIn);


        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInInetnt = new Intent(c, SignUpActivity.class);
                startActivity(signInInetnt);
            }
        });



    }


    private void attemptLogin() {

        Backendless.UserService.login(etEmail.getText().toString(), etPassword.getText().toString(), new AsyncCallback<BackendlessUser>() {
            public void handleResponse(BackendlessUser user) {
                //startActivity(new Intent(c,FeedActivity.class));
                startActivity(new Intent(c,FeedActivity.class));

            }

            public void handleFault(BackendlessFault fault) {
               Log.e("Error",fault.getMessage());
            }
        },true);



        // Store values at the time of the login attempt.
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etEmail;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_field_required));
            focusView = etEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            etEmail.setError(getString(R.string.error_invalid_email));
            focusView = etEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

        }
    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}
