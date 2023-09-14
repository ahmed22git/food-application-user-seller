package com.example.restaurantapp.models;

import android.widget.Filter;

import com.example.restaurantapp.adapters.AdapterProductSeller;

import java.util.ArrayList;

public class FilterProduct extends Filter {

    private AdapterProductSeller adapter;
    private ArrayList<ModelProduct> filterlist;

    public FilterProduct(AdapterProductSeller adapter, ArrayList<ModelProduct> filterlist) {
        this.adapter = adapter;
        this.filterlist = filterlist;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0){
            constraint = constraint.toString().toUpperCase();

            ArrayList<ModelProduct> filteredModels =  new ArrayList<>();
            for (int i=0; i<filterlist.size(); i++ ){
                if (filterlist.get(i).getProductTitle().toUpperCase().contains(constraint)||
                        filterlist.get(i).getProductTitle().toUpperCase().contains(constraint))
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

        adapter.productList = (ArrayList<ModelProduct>) results.values;
        adapter.notifyDataSetChanged();

    }
}
