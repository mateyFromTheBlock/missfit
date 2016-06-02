package com.example.arono.missfit.Drawer;

/**
 * Created by arono on 23/02/2016.
 */
public class DrawerItem {

    private int icon;
    private String name;

    public DrawerItem(String name,int icon){
        this.name = name;
        this.icon = icon;
    }

    public String getName(){
        return name;
    }
    public int getIcon(){
        return icon;
    }

}
