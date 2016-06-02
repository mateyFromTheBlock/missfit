package com.example.arono.missfit.Registration;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.backendless.async.callback.BackendlessCallback;
import com.example.arono.missfit.R;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private Button signInBtn;
    private BackendlessUser user;

    private HashMap<String, Object> properties = new HashMap<>();

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        signInBtn = (Button)findViewById(R.id.sign_in_button);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidCredentials()) {
                    registerUser();
                    startActivity(new Intent(getApplicationContext(),LoginInActivity.class));
                }
            }
        });


    }

    public boolean isValidCredentials() {
        String email = ((EditText)findViewById(R.id.email_signin)).getText().toString();
        String password = ((EditText)findViewById(R.id.password_signin)).getText().toString();
        String reEnterPassword = ((EditText)findViewById(R.id.reenter_password)).getText().toString();

        if (validateEmail(email) && validatePassword(password,reEnterPassword)) {
            return true;
        }

        return false;

    }

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    public static boolean validatePassword(String passwordStr,String reEnterPasswordStr) {
        return passwordStr.length() > 4 && passwordStr.equals(reEnterPasswordStr);
    }

    public void registerUser() {

        user = new BackendlessUser();
        user.setEmail(((EditText) findViewById(R.id.email_signin)).getText().toString());
        user.setPassword(((EditText) findViewById(R.id.password_signin)).getText().toString());

        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {
                setProperties();
                user.putProperties(properties);
                Backendless.UserService.update(user, new BackendlessCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser backendlessUser) {
                        Log.e("user", backendlessUser.toString());
                    }

                    public void handleFault(BackendlessFault fault) {
                        Log.e("fault", fault.toString());
                    }
                });
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Log.e("RegisterFailed", backendlessFault.getCode());
            }
        });

    }

    public void setProperties() {
        String name = ((EditText)findViewById(R.id.name)).getText().toString();
        String city = ((EditText)findViewById(R.id.city)).getText().toString();
        String phone = ((EditText)findViewById(R.id.phone)).getText().toString();

        if(!name.equals(""))    
            properties.put("name",name);

        if(phone.matches("^([0-9]{10})|[0-9]{9}"))
            properties.put("phone", phone);

        if(!city.isEmpty())
            properties.put("city", city);

    }


}


