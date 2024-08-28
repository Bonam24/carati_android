package com.example.carati;

import static com.example.carati.constants.MAX_BYTES_PDF;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterPfdAdmin extends RecyclerView.Adapter<AdapterPfdAdmin.HolderPdfAdmin> implements Filterable {


    private Context context;
    public ArrayList<modelPdfAdmin> pdfAdminArrayList, filterlist;
    private filter_pdf_Admin filter;
    private static  final String TAG = "PDF_ADAPTER";
    private ProgressDialog progressDialog;


    //constructor
    public AdapterPfdAdmin(Context context, ArrayList<modelPdfAdmin> pdfAdminArrayList) {
        this.context = context;
        this.pdfAdminArrayList = pdfAdminArrayList;
        this.filterlist = pdfAdminArrayList;
        //init progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("please wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HolderPdfAdmin(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pdf_admin,null));
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfAdmin holder, int position) {
        modelPdfAdmin model = pdfAdminArrayList.get(position);
        String title = model.getName();
        String description = model.getDescription();
        String url = model.getUrl();
        String author = model.getAuthor();
        String pdfID = model.getId();
        String price = model.getPrice();
        long timestamp = model.getTimestamp();

        //formated date
        String formattedDate = myapplication.formatTimestamp(timestamp);

        holder.titleTV.setText(title);
        holder.description.setText(description);

        holder.author.setText(author);
        holder.price.setText(price+"frs/month");
        holder.morebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //brings more options
                moreOptionsDialog(model,holder);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, pdfDetailsActivity.class);
                intent.putExtra("bookid",pdfID);
                context.startActivity(intent);
            }
        });


        myapplication.loadPdffromURL_SinglePage(""+url,""+title,holder.pdfView,holder.progressBar);
        myapplication.loadPDFsize(""+url,""+title,holder.size);


    }

    private void moreOptionsDialog(modelPdfAdmin model, HolderPdfAdmin holder) {

        String[] options = {"Edit","Delete"};
        String bookid = model.getId();
        String bookURL = model.getUrl();
        String bookTitle = model.getName();
        String bookcategory = model.getCategory();
        //bring alert dialog

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    //handle dialog options
                        if(which==0){
                            //Edit clicked
                            Intent intent = new Intent(context, edit_pdf_activity.class);
                            intent.putExtra("bookid",bookid);
                            intent.putExtra("bookcategory",bookcategory);
                            context.startActivity(intent);

                        }
                        else if(which==1){
                            //delete has been clicked
                            myapplication.deleteBook(context,
                                    ""+bookid,
                                    ""+bookURL,
                                    ""+bookTitle);
                            //deleteBook(model, holder);
                        }
                    }
                }).show();
    }









    @Override
    public int getItemCount() {
        return pdfAdminArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter = new filter_pdf_Admin(filterlist,this);
        }
        return filter;
    }


    class HolderPdfAdmin extends RecyclerView.ViewHolder{


        private TextView titleTV,description,author,price,size,date;
        private ImageButton morebtn;
        PDFView pdfView;
        ProgressBar progressBar;
        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.titleTV);
            description =itemView.findViewById(R.id.description);
            author = itemView.findViewById(R.id.author);
            price = itemView.findViewById(R.id.price);
            size = itemView.findViewById(R.id.size);


            morebtn = itemView.findViewById(R.id.morebtn);

            pdfView = itemView.findViewById(R.id.pdfView);
            progressBar = itemView.findViewById(R.id.progressBar);

        }
    }
}
