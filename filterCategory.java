package com.example.carati;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

public class filterCategory extends Filter {
    ArrayList<Model_Category> filterList;
    Adapter_Category adapter_category;
    //constructor
    public filterCategory(ArrayList<Model_Category> filterList, Adapter_Category adapter_category) {
        this.filterList = filterList;
        this.adapter_category = adapter_category;
    }
    //filter the categories
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults = new FilterResults();
        if(constraint!=null && constraint.length()>0){
            constraint = constraint.toString().toUpperCase();
            ArrayList<Model_Category> filteredModels = new ArrayList<>();
            for(int i=0; i<filterList.size();i++){
                //validate
                if(filterList.get(i).getCategory().toUpperCase().contains(constraint)){
                    //add to filtered model
                    filteredModels.add(filterList.get(i));
                }

            }
            filterResults.count = filteredModels.size();
            filterResults.values = filteredModels;
        }else{
            filterResults.count=filterList.size();
            filterResults.values = filterList;
        }
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter_category.modelCategoryList = (ArrayList<Model_Category>)results.values;
        //notify changes
        adapter_category.notifyDataSetChanged();
    }
}
