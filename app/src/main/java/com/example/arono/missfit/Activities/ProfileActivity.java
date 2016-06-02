package com.example.arono.missfit.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.arono.missfit.Drawer.BaseActivityWithNavigationDrawer;
import com.example.arono.missfit.R;
import com.example.arono.missfit.Registration.LoginInActivity;

public class ProfileActivity extends BaseActivityWithNavigationDrawer {

    private LayoutInflater inflater;
    private FrameLayout contentFrame;
    private TextView tvName,tvEmail,tvPhone,tvCity;
    private BackendlessUser user;
    private Button btnLogOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFrameView();
        initTitle();
        initialize();
        setProfileInformationInsideTextView();
        logOut();

    }

    public void initFrameView(){
        contentFrame = getContentFrame();
        inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_profile, null, false);
        contentFrame.addView(view);
    }

    public void initTitle(){
        Intent titleIntent = getIntent();
        String title =  titleIntent.getStringExtra("title");
        getSupportActionBar().setTitle(title);
    }

    public void initialize(){
        tvName = (TextView) findViewById(R.id.tvName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvCity = (TextView) findViewById(R.id.tvCity);

        btnLogOut = (Button) findViewById(R.id.btnLogOut);
    }

    public void logOut(){
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Backendless.UserService.logout(new AsyncCallback<Void>() {
                    @Override
                    public void handleResponse(Void response) {
                        Log.e("Error", "LogOut");
                        Intent intent = new Intent(getApplicationContext(), LoginInActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {

                    }
                });
            }
        });
    }

    public void setProfileInformationInsideTextView(){
        user = Backendless.UserService.CurrentUser();
        String email = user.getEmail();
        String name = (String) user.getProperty("name");
        String phone = (String) user.getProperty("phone");
        String city = (String) user.getProperty("city");

        tvEmail.setText(email);
        tvName.setText(name);
        if(phone != null){
            tvPhone.setText(phone);
        }
        if(city != null){
            tvCity.setText(city);
        }
    }
}
