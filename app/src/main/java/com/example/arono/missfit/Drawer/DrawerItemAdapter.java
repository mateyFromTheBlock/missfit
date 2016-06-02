package com.example.arono.missfit.Drawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.arono.missfit.R;

import java.util.ArrayList;



/**
 * Created by arono on 13/02/2016.
 */
public class DrawerItemAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<DrawerItem> allData;

    public DrawerItemAdapter(Context c,ArrayList data){
        this.context = c;
        this.allData = data;
    }
    public DrawerItemAdapter(Context c){
        this.context = c;
    }
    public int getCount() {
        return allData.size();
    }


    @Override
    public Object getItem(int i) {
        return allData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View row = (RelativeLayout)inflater.inflate(R.layout.drawer_list_item, viewGroup, false);
        TextView tvName = (TextView)row.findViewById(R.id.textViewName);
        ImageView imageView = (ImageView)row.findViewById(R.id.imageViewIcon);
        DrawerItem drawerItem = allData.get(i);
        tvName.setText(drawerItem.getName());
        imageView.setImageResource(drawerItem.getIcon());
        return row;
    }

}

