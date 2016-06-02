package com.example.arono.missfit.Drawer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.arono.missfit.Activities.AddItemActivity;
import com.example.arono.missfit.Activities.FeedActivity;
import com.example.arono.missfit.Activities.MyItemsActivity;
import com.example.arono.missfit.Activities.ProfileActivity;
import com.example.arono.missfit.R;


import java.util.ArrayList;

public class BaseActivityWithNavigationDrawer extends AppCompatActivity {

        private Toolbar toolbar;
        private DrawerLayout drawerLayout;
        private ListView drawerList;
        private ActionBarDrawerToggle drawerToggle;
        private ArrayList<DrawerItem> drawerItem;
        private FrameLayout contentFrame;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_base_activity_with_navigation_drawer);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            contentFrame = (FrameLayout) findViewById(R.id.content_frame);

            toolbar = (Toolbar)findViewById(R.id.tool_bar);
            setSupportActionBar(toolbar);

            setUpNavigation();


        }

        public void setUpNavigation() {
            drawerItem = new ArrayList<>();
            drawerItem.add(new DrawerItem("Home", R.drawable.home32));
            drawerItem.add(new DrawerItem("Profile",R.drawable.profile32));
            drawerItem.add(new DrawerItem("Sell",R.drawable.sell32));
            drawerItem.add(new DrawerItem("My Items",R.drawable.myitems32));

            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawerList = (ListView) findViewById(R.id.left_drawer);
            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.open_navigation,R.string.close_navigation);

            DrawerItemAdapter drawerItemAdapter = new DrawerItemAdapter(this,drawerItem);
            // Set the adapter for the list view
            drawerList.setAdapter(drawerItemAdapter);
            // Set the list's click listener
            drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    selectItem(position);
                    selectTitle(position);
                }
            });

            drawerLayout.setDrawerListener(drawerToggle);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }

        @Override
        protected void onPostCreate(Bundle savedInstanceState) {
            super.onPostCreate(savedInstanceState);
            drawerToggle.syncState();
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig)
        {
            super.onConfigurationChanged(newConfig);
            drawerToggle.onConfigurationChanged(newConfig);
        }

        public void selectTitle(int position){
            getSupportActionBar().setTitle(drawerItem.get(position).getName());
        }

        private void selectItem(int position) {
            Intent intent = null;
            switch(position){
                case 0:  intent = new Intent(this, FeedActivity.class);
                    intent.putExtra("STOP",true);
                    break;
                case 1:  intent = new Intent(this, ProfileActivity.class);
                    intent.putExtra("title", drawerItem.get(position).getName());
                    break;
                case 2:  intent = new Intent(this, AddItemActivity.class);
                    intent.putExtra("title", drawerItem.get(position).getName());
                    break;
                case 3:  intent = new Intent(this, MyItemsActivity.class);
                    intent.putExtra("title", drawerItem.get(position).getName());
                     break;
            }
            startActivity(intent);
        }


    public FrameLayout getContentFrame(){
        return contentFrame;
    }

}







