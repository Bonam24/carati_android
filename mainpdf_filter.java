package com.example.carati;

import android.widget.Filter;

import com.example.carati.models.ModelPdf;

import java.util.ArrayList;

public class mainpdf_filter extends Filter {
    ArrayList<ModelPdf> filterList;
    adapterpdf adapterpdf1;

    public mainpdf_filter(ArrayList<ModelPdf> filterList, adapterpdf adapterpdf1) {
        this.filterList = filterList;
        this.adapterpdf1 = adapterpdf1;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if(constraint!=null && constraint.length()>0){
            constraint =constraint.toString().toUpperCase();
            ArrayList<ModelPdf>  filterpdf = new ArrayList<>();

            for(int i=0; i<filterList.size(); i++){
                if(filterList.get(i).getName().toUpperCase().contains(constraint)){
                    filterpdf.add(filterList.get(i));
                }
            }
            results.count = filterpdf.size();
            results.values = filterpdf;
        }
        else{
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        //adapterpdf.pdfArrayList = (ArrayList<ModelPdf>)results.values;

        adapterpdf1.notifyDataSetChanged();
    }
}
