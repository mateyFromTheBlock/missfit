package com.example.arono.missfit.Activities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessException;
import com.backendless.exceptions.BackendlessFault;
import com.example.arono.missfit.DataServerManagement.DataManager;
import com.example.arono.missfit.Drawer.BaseActivityWithNavigationDrawer;
import com.example.arono.missfit.ImageAdapter;
import com.example.arono.missfit.Item;
import com.example.arono.missfit.R;

import java.util.ArrayList;

public class MyItemsActivity extends BaseActivityWithNavigationDrawer {

    private FrameLayout contentFrame;
    private LayoutInflater inflater;
    private ImageAdapter imageAdapter;
    private RelativeLayout rl;
    private GridView gvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFrameView();
        splashScreen();
        initTitle();
        initialize();
        loadSpecificItems();
        setItemListener();

    }

    public void initFrameView(){
        contentFrame = getContentFrame();
        inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_myitems,null,false);
        contentFrame.addView(view);

    }

    public void initialize(){
        rl = (RelativeLayout)this.findViewById(R.id.rlMyItems);
        imageAdapter = new ImageAdapter(getApplicationContext());
        gvItems = new GridView(this);
        gvItems.setNumColumns(2);

    }
    public void initTitle(){
        Intent titleIntent = getIntent();
        getSupportActionBar().setTitle("YourItems");
    }

    public void splashScreen(){
        final View popUp = inflater.inflate(R.layout.pop_up_progress_bar, null, false);
        final ProgressBar progressBar = (ProgressBar) popUp.findViewById(R.id.popUpProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        contentFrame.addView(popUp);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                contentFrame.removeView(popUp);
            }

        }, 2000L);
    }

    public void loadSpecificItems(){
        BackendlessUser user = Backendless.UserService.CurrentUser();
        new LoadItemsFromSpecificUserAsyncTask(user).execute();
    }

    public void setItemListener(){
        gvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                gvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(getApplicationContext(), ShowItemActivity.class);
                        BackendlessUser user = imageAdapter.getItem(i).getUser();
                        Item item = imageAdapter.getItem(i);
                        intent.putExtra("Description", item.getName());
                        intent.putExtra("phone", (String) user.getProperty("phone"));
                        intent.putExtra("brand", item.getBrand());
                        intent.putExtra("color", item.getColor());
                        intent.putExtra("size", Item.sizeToString(item.getSize()));
                        intent.putExtra("type", item.getType());
                        intent.putExtra("price", String.valueOf(item.getPrice()));
                        intent.putExtra("photoOne", item.getPhotoOne());
                        intent.putExtra("photoTwo", item.getPhotoTwo());
                        intent.putExtra("photoThree", item.getPhotoThird());
                        startActivity(intent);
                    }
                });
            }
        });
        gvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Item itemToDelete = imageAdapter.getItem(i);
                BackendlessUser user = imageAdapter.getItem(i).getUser();
                deleteAlert(itemToDelete);
                return false;
            }
        });
    }


    public void deleteAlert(final Item itemToDelete){
        AlertDialog.Builder finishUploadAlert = new AlertDialog.Builder(this);
        finishUploadAlert.setTitle("Delete Item").setMessage("are you sure?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DataManager dataManager = DataManager.getInstance();
                dataManager.deleteItemOfSpecificUser(itemToDelete, getApplicationContext());
                Intent intent = new Intent(getApplicationContext(),MyItemsActivity.class);
                startActivity(intent);

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog1 = finishUploadAlert.create();
        alertDialog1.show();
    }

class LoadItemsFromSpecificUserAsyncTask extends AsyncTask {

    private DataManager dataManager;
    private boolean flag = false;
    private BackendlessUser user;
    private ArrayList<Item> itemArrayList;

    public LoadItemsFromSpecificUserAsyncTask(BackendlessUser user){
        this.dataManager = DataManager.getInstance();
        this.user = user;
    }
    @Override
    protected Object doInBackground(Object... objects) {
        dataManager.getSpecificUserItems(user, new DataManager.GetAllCallback() {
            @Override

            public void done(ArrayList<Item> value, BackendlessException e) {
                if (e != null) {
                    Log.e("LoadItemsFromSpecificUserAsyncTask", e.toString());
                }
                itemArrayList = value;
                flag = true;
            }
        });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if(flag) {
            if(itemArrayList.size() != 0) {
                imageAdapter.setItems(itemArrayList);
                gvItems.setAdapter(imageAdapter);
                rl.addView(gvItems);
            }else
                Toast.makeText(getApplication().getApplicationContext(),"No Items",Toast.LENGTH_SHORT).show();
        }

    }
}

}
