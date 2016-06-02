package com.example.arono.missfit;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.example.arono.missfit.Activities.FeedActivity;


/**
 * Created by arono on 20/03/2016.
 */
public class TabAdapter extends FragmentStatePagerAdapter {


    private FragmentCategory fragmentCategory;
    private ImageAdapter[] im;

    public TabAdapter(FragmentManager fm,ImageAdapter[] imageAdapter) {
        super(fm);
        im = imageAdapter;
    }

    @Override
    public Fragment getItem(int position) {
        fragmentCategory = FragmentCategory.getInstance(position, im[position]);
        return fragmentCategory;
    }

    @Override
    public int getCount() {
        return FeedActivity.SIZE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0: return FeedActivity.TOPS;
            case 1: return FeedActivity.BOTTOMS;
            case 2: return FeedActivity.SHOES;
            case 3: return FeedActivity.CUSTOM;
        }
        return null;
    }
}
