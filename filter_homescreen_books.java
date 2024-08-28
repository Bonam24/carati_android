package com.example.carati;

import android.widget.Filter;

import com.example.carati.models.ModelPdf;

import java.util.ArrayList;

public class filter_homescreen_books extends Filter {
    private ArrayList<ModelPdf>  filterlist;
    private adapterpdf adapterpdf1;

    public filter_homescreen_books(ArrayList<ModelPdf> filterlist, adapterpdf adapterpdf1) {
        this.filterlist = filterlist;
        this.adapterpdf1 = adapterpdf1;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if(constraint!=null && constraint.length()>0){
            constraint =constraint.toString().toUpperCase();
            ArrayList<ModelPdf>  filterpdf = new ArrayList<>();

            for(int i=0; i<filterlist.size(); i++){
                if(filterlist.get(i).getName().toUpperCase().contains(constraint)){
                    filterpdf.add(filterlist.get(i));
                }
            }
            results.count = filterpdf.size();
            results.values = filterpdf;
        }
        else{
            results.count = filterlist.size();
            results.values = filterlist;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

    }
}
