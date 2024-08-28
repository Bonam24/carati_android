package com.example.carati;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carati.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterForRentBooks extends RecyclerView.Adapter<AdapterForRentBooks.HolderOfRentedBooks>{
    private Context context;
    private ArrayList<ModelPdf> pdfArrayList;
    private LayoutInflater inflater;
    String TAG = "MY_RENTED_BOOKS_TAG";
    //constructor for the class
    public AdapterForRentBooks(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderOfRentedBooks onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_pdf_books_on_rent,null);
        HolderOfRentedBooks holderOfRentedBooks = new HolderOfRentedBooks(view);
        return holderOfRentedBooks;

    }

    @Override
    public void onBindViewHolder(@NonNull HolderOfRentedBooks holder, int position) {
        ModelPdf model = pdfArrayList.get(position);
        loadBookDetails(model,holder);
    }
    //load book details from the database
    private void loadBookDetails(ModelPdf model, HolderOfRentedBooks holder) {
        String bookid = model.getId();
        modelJustToGetExpiryDate m2 = new modelJustToGetExpiryDate();
        long timestamp = model.getTimestamp();
        Log.d(TAG, "loadBookDetails: "+ bookid);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("booking");
        ref.child(bookid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String bookname = ""+snapshot.child("name").getValue();
                String BookDescription = ""+snapshot.child("description").getValue();
                String url = ""+snapshot.child("url").getValue();
                String uid = ""+snapshot.child("uid").getValue();
                String price = ""+snapshot.child("price").getValue();


                //settomodel
                model.setCart(true);
                model.setName(bookname);
                model.setDescription(BookDescription);
                model.setUrl(url);
                model.setUid(uid);

                holder.titleTV.setText(bookname);
                holder.description.setText(BookDescription);
                holder.price.setText(price+"frs");


                myapplication.loadPdffromURL_SinglePage(url,bookname,holder.pdfView,holder.progressBar);
                //myapplication.startTimer(context,holder.timeleft,timestamp);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    //load size of array
    @Override
    public int getItemCount() {
       return pdfArrayList.size();
    }

    class HolderOfRentedBooks extends RecyclerView.ViewHolder{

        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTV,description,price;
        public HolderOfRentedBooks(@NonNull View itemView) {
            super(itemView);
            pdfView = (PDFView) itemView.findViewById(R.id.pdfView);
            progressBar = itemView.findViewById(R.id.progressBar);
            titleTV = itemView.findViewById(R.id.titleTV);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);

        }
    }

}
