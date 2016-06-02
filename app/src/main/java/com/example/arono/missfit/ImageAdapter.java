package com.example.arono.missfit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.arono.missfit.DataServerManagement.DataManager;


import java.util.ArrayList;


/**
 * Created by arono on 13/02/2016.
 */
public class ImageAdapter extends BaseAdapter implements Filterable{

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Item> filteredData;
    private ArrayList<Item> allData;
    private DataManager dataManager;

    public ImageAdapter(Context c){
        this.context = c;
        dataManager = DataManager.getInstance();
    }
    public int getCount() {
        return filteredData.size();
    }

    public void setItems(ArrayList data) {
        this.allData= data;
        this.filteredData = data;
    }

    @Override
    public Item getItem(int i) {
        return filteredData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout  row = (RelativeLayout)inflater.inflate(R.layout.single_row, viewGroup, false);

        TextView tvName = (TextView)row.findViewById(R.id.itemName);
        TextView tvPrice = (TextView)row.findViewById(R.id.itemPrice);
        ImageView imageView = (ImageView)row.findViewById(R.id.imageView);
        final ProgressBar progressBar = (ProgressBar) row.getChildAt(1);
        Item item = null;
        if(allData.size() != 0 ) {
            item = allData.get(i);

            tvName.setText(item.getName());
            tvPrice.setText("" + item.getPrice());
            progressBar.setVisibility(View.VISIBLE);

            dataManager.downloadImageFromServer(context, item, imageView, progressBar);
        }
        return row;
    }


    /**
     *
     * @return Filters results by word that the user is looking for.
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                allData = dataManager.getItems();
                filteredData = new ArrayList<Item>();

                for (Item s :  allData) {
                    if(s.getName().contains(constraint))
                        filteredData.add(s);

                }
                allData = filteredData;
                FilterResults results = new FilterResults();
                results.values = filteredData;
                results.count = filteredData.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //filteredData = (ArrayList<String>) results.values;
                notifyDataSetChanged();
            }
        };
    }


}


