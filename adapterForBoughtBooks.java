package com.example.carati;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carati.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.Inflater;

public class adapterForBoughtBooks extends  RecyclerView.Adapter<adapterForBoughtBooks.HolderpdfBoughtBooks> {

    private Context context;
    private ArrayList<ModelPdf> pdfArrayList;
    private LayoutInflater inflater;
    String TAG = "MY_BOOKS_TAG";

    //constructor

    public adapterForBoughtBooks(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderpdfBoughtBooks onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_pdf_mybooks,null);
        HolderpdfBoughtBooks holderpdfBoughtBooks = new HolderpdfBoughtBooks(view);
        return holderpdfBoughtBooks;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderpdfBoughtBooks holder, int position) {
        ModelPdf model = pdfArrayList.get(position);
        loadBookDetails(model,holder);
        //startTimer(holder.timeleft);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open details page
                Intent intent = new Intent(context, pdfDetailsActivity.class);
                intent.putExtra("bookid",model.getId());
                context.startActivity(intent);

            }
        });


    }

    private void justExpirydate(ModelPdf model, String uid,String bookid, HolderpdfBoughtBooks holder ){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid).child("myCart").child(bookid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String expirydate = ""+snapshot.child("expirydate").getValue();
                //get the string in months
                String m = expirydate.substring(5,7);
                String d = expirydate.substring(8,10);
                String h = expirydate.substring(11,13);
                String min = expirydate.substring(14,16);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date futureDate  = dateFormat.parse(expirydate);
                    Date currentDate = new Date();
                    // Calculate the difference in milliseconds
                    long differenceInMillis = futureDate.getTime() - currentDate.getTime();
                    if(differenceInMillis<1){
                        myapplication.removeFromFavoriteList(context,bookid);
                    }
                    else {
                        holder.timeleft.setText(m+"/"+d+" "+h+":"+min);
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //function to load books from database
    private void loadBookDetails(ModelPdf model, HolderpdfBoughtBooks holder) {
        //get book ID
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


                //settomodel
                model.setCart(true);
                model.setName(bookname);
                model.setDescription(BookDescription);
                model.setUrl(url);
                model.setUid(uid);

                justExpirydate(model,uid,bookid,holder);
                holder.titleTV.setText(bookname);
                holder.description.setText(BookDescription);
                justExpirydate(model,uid,bookid,holder);

                myapplication.loadPdffromURL_SinglePage(url,bookname,holder.pdfView,holder.progressBar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        
    }
    //return size of array
    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    class HolderpdfBoughtBooks extends RecyclerView.ViewHolder{
        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTV,description,timeleft,ddat;

        public HolderpdfBoughtBooks(@NonNull View itemView) {
            super(itemView);
            pdfView = (PDFView) itemView.findViewById(R.id.pdfView);
            progressBar = itemView.findViewById(R.id.progressBar);
            titleTV = itemView.findViewById(R.id.titleTV);
            description = itemView.findViewById(R.id.description);
            timeleft = itemView.findViewById(R.id.timeleft);
            ddat = itemView.findViewById(R.id.ddat);
        }

    }
}
