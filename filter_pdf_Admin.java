package com.example.carati;

import android.widget.Filter;

import java.util.ArrayList;

public class filter_pdf_Admin extends Filter {
    ArrayList<modelPdfAdmin> filterList;
    AdapterPfdAdmin adapterPfdAdmin;
    //constructor
    public filter_pdf_Admin(ArrayList<modelPdfAdmin> filterList, AdapterPfdAdmin adapterPfdAdmin) {
        this.filterList = filterList;
        this.adapterPfdAdmin = adapterPfdAdmin;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if(constraint!=null && constraint.length()>0){
            constraint =constraint.toString().toUpperCase();
            ArrayList<modelPdfAdmin>  filterpdf = new ArrayList<>();

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
       adapterPfdAdmin.pdfAdminArrayList = (ArrayList<modelPdfAdmin>)results.values;

      adapterPfdAdmin.notifyDataSetChanged();
    }
}
