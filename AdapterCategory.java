package com.example.carati;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.HolderCategory> {
    private Context context;
    private ArrayList<Model_Category> categoryArrayList;
    //constructor
    public AdapterCategory(Context context, ArrayList<Model_Category> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
    }

    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return  new HolderCategory(LayoutInflater.from(parent.getContext()).inflate(R.layout.individualcategory,null));

    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position) {
        Model_Category model = categoryArrayList.get(position);
        String id = model.getId();
        String category = model.getCategory();
        String uid = model.getUid();
        long timestamp = model.getTimestamp();

        holder.catname.setText(category);
        
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }
    //return the size of the array categoryArraylist
    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    class  HolderCategory extends RecyclerView.ViewHolder{

        TextView catname;
        ImageButton delete;
        public HolderCategory(@NonNull View itemView) {
            super(itemView);
            catname = itemView.findViewById(R.id.catname);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
