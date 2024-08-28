package com.example.carati;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Category extends RecyclerView.Adapter<Adapter_Category.MyviewHolder> implements Filterable {
    public ArrayList<Model_Category> modelCategoryList,filterList;

    //this class was used to delete the categories

    private final Context context;
    //instance of our filter class
    filterCategory filterCategory;
    //the constructor to initialize the variables
    public Adapter_Category(ArrayList<Model_Category> modelCategoryList, Context context) {
        this.modelCategoryList = modelCategoryList;
        this.context = context;
        this.filterList= modelCategoryList;
    }

    @NonNull
    @Override
    public Adapter_Category.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyviewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.individualcategory,null));

    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Category.MyviewHolder holder, int position) {
    Model_Category model_category = modelCategoryList.get(position);
    holder.catname.setText(model_category.getCategory());


    holder.delete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //call the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete")
                    .setMessage("Are you sure you want to delete this category?")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String categoryId = model_category.getId();
                            if (categoryId != null && !categoryId.isEmpty()) {
                                DatabaseReference reference = FirebaseDatabase.getInstance()
                                        .getReference("Categories").child(categoryId);

                                reference.removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(context, "Deleted successfully"+categoryId, Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Invalid category ID", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    });

    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, pdfListAdmin.class);
            intent.putExtra("category", model_category.getCategory());
            context.startActivity(intent);
        }
    });


    }
    //function to delete data
    private void deleteData(int which) {
        String id = String.valueOf(which);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Successfully deleted...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, " "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public int getItemCount() {
        return modelCategoryList.size();
    }

    @Override
    public Filter getFilter() {
        if(filterCategory==null){
            filterCategory= new filterCategory(filterList, this);
        }
        return filterCategory;
    }

    static  class MyviewHolder extends RecyclerView.ViewHolder{
    private TextView catname;
    private ImageButton delete;
        public MyviewHolder(@NonNull View itemView) {
            super(itemView);
            catname = itemView.findViewById(R.id.catname);

            delete = itemView.findViewById(R.id.delete);
        }
    }
}
