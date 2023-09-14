package com.example.restaurantapp.models;

import android.widget.Filter;

import com.example.restaurantapp.adapters.AdapterOrderShop;
import com.example.restaurantapp.adapters.AdapterProductSeller;

import java.util.ArrayList;

public class FilterOrderShop extends Filter {

    private AdapterOrderShop adapter;
    private ArrayList<ModelOrderShop> filterlist;

    public FilterOrderShop(AdapterOrderShop adapter, ArrayList<ModelOrderShop> filterlist) {
        this.adapter = adapter;
        this.filterlist = filterlist;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0){
            constraint = constraint.toString().toUpperCase();

            ArrayList<ModelOrderShop> filteredModels =  new ArrayList<>();
            for (int i=0; i<filterlist.size(); i++ ){
                if (filterlist.get(i).getOrderStatus().toUpperCase().contains(constraint))

                        {
                            filteredModels.add(filterlist.get(i));

                }  }
            results.count = filteredModels.size();
            results.values = filteredModels;

        }
        else{
            results.count = filterlist.size();
            results.values = filterlist;

        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.orderShopArrayList = (ArrayList<ModelOrderShop>) results.values;
        adapter.notifyDataSetChanged();

    }
}
