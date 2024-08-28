package com.example.carati;

import static com.example.carati.constants.MAX_BYTES_PDF;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carati.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class adapterpdf extends RecyclerView.Adapter<adapterpdf.Holderpdf> implements Filterable {

    mainpdf_filter filter;
    private Context context;
    public ArrayList<ModelPdf> pdfArrayList, filterlist;
    private LayoutInflater inflater ;
    FirebaseAuth firebaseAuth;
    boolean isInmyCart= false;
    filter_homescreen_books filterHomescreenBooks;
    //constructor

    static  final String TAG = "PDF_ADAPTER";
    public adapterpdf(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
    }

    @NonNull
    @Override
    public Holderpdf onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_pdf,null);
        Holderpdf holderpdf = new Holderpdf(view);

        return holderpdf;
    }

    @Override
    public void onBindViewHolder(@NonNull Holderpdf holder, int position) {

        ModelPdf modelPdf = pdfArrayList.get(position);
        String name = modelPdf.getName();
        String price = modelPdf.getPrice();
        String description = modelPdf.getDescription();
        String category = modelPdf.getCategory();
        String uid = modelPdf.getUid();
        String url = modelPdf.getUrl();
        String bookid = modelPdf.getId();

        holder.titleTV.setText(name);
        holder.description.setText(description);
        holder.price.setText(price+ "Frs");

        loadPDFfromURL(modelPdf,holder);

        holder.addcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myapplication.checkIsInYourBooks(context, bookid);
                Intent intent = new Intent(context, payForBook.class);
                intent.putExtra("bookid", bookid);
                intent.putExtra("price",price);
                context.startActivity(intent);
            }
        });




    }
        //load the the pdf url from the database
    private void loadPDFfromURL(ModelPdf modelPdf, Holderpdf holder) {
        String pdfUrl = modelPdf.getUrl();
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        reference.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d(TAG, "onSuccess:"+ modelPdf.getName()+ "successfully got the file");
                        holder.pdfView.fromBytes(bytes)
                                .pages(0)
                                .spacing(0)
                                .swipeHorizontal(false)
                                .enableSwipe(false)
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        Log.d(TAG, "onError: "+ t.getMessage());
                                        holder.progressBar.setVisibility(View.INVISIBLE);

                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        Log.d(TAG, "onPageError: page error"+ t.getMessage());
                                        holder.progressBar.setVisibility(View.INVISIBLE);

                                    }
                                }).onLoad(new OnLoadCompleteListener() {
                                    @Override
                                    public void loadComplete(int nbPages) {
                                        holder.progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "loadComplete: pdfloading");
                                    }
                                }).load();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: failure to get the file");
                    }
                });
    }

    //added code


    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter = new mainpdf_filter(filterlist,this);
        }
        return filter;
    }


    class Holderpdf extends RecyclerView.ViewHolder{

        PDFView pdfView ;
        ProgressBar progressBar;
        TextView titleTV,description,price,pmonth;
        Button addcart;
        public Holderpdf(@NonNull View itemView) {
            super(itemView);
            pdfView = (PDFView) itemView.findViewById(R.id.pdfView);
            progressBar = itemView.findViewById(R.id.progressBar);
            titleTV = itemView.findViewById(R.id.titleTV);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            addcart = itemView.findViewById(R.id.addcart);
            pmonth = itemView.findViewById(R.id.pmonth);


        }

    }
}
