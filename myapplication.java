package com.example.carati;

import static com.example.carati.constants.MAX_BYTES_PDF;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class myapplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    //create a static timestamp to convert tiemstamp to date format
    public  static  final String formatTimestamp(long timestamp){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);
        //format time stamp
        String date = DateFormat.format("dd/MM/yyyy", calendar).toString();
        return date;
    }
    //function to delete book
    public static void deleteBook(Context context, String bookid, String bookURL, String bookTitle) {
        String TAG = "DELETE_BOOK_TAG";

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait");

        Log.d(TAG, "deleteBook: Deleting...");
        progressDialog.setMessage("Deleting"+bookTitle+ "...");
        progressDialog.show();
        Log.d(TAG, "deleteBook: from storage...");
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(bookURL);
        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: deleted from database");
                Log.d(TAG, "onSuccess: now deleting info from database");

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("booking").child(bookid);
                databaseReference.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Book deleted from database..."+bookid, Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: Failed to delete"+ e.getMessage());
                                progressDialog.dismiss();
                                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure:  Failed to delete");
                progressDialog.dismiss();
                Toast.makeText(context, "Failed to delete"+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    //function to start timer of when to delete books
    public static void startTimer(Context context, String bookid) {
       // CountDownTimer countDownTimer = new CountDownTimer(30000, 1000) { // Example duration in milliseconds

        CountDownTimer countDownTimer = new CountDownTimer(150000, 1000) { // Example duration in milliseconds

            @Override
            public void onTick(long millisUntilFinished) {
                int days = (int) (millisUntilFinished / (1000 * 60 * 60 * 24));
                int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                int seconds = (int) (millisUntilFinished / 1000) % 60;
            }

            @Override
            public void onFinish() {
                Toast.makeText(context, "Book has expired ", Toast.LENGTH_SHORT).show();
                myapplication.removeFromFavoriteList(context,bookid);
            }
        };

        countDownTimer.start();
    }

    //function to load pdf size
    public static void loadPDFsize(String pdfURL, String pdfTitle, TextView size) {
        String TAG = "PDF_SIZE_TAG";
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfURL);
        storageReference.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        double bytes = storageMetadata.getSizeBytes();
                        Log.d(TAG, "onSuccess: "+ pdfTitle+" "+bytes);
                        //covert bytes to kb/mb

                        double kb = bytes/1024;
                        double mb = kb/1024;

                        if(mb>=1){
                            size.setText(String.format("%.2f",mb) + "MB");
                        }
                        else if (kb>=1){
                            size.setText(String.format("%.2f",kb)+ "KB");
                        }
                        else{
                            size.setText(String.format("%.2f",bytes)+ "bytes");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+ e.getMessage());
                    }
                });
    }
    //function to load just the first page of a pdf
    public static void loadPdffromURL_SinglePage(String pdfURL, String pdfTitle, PDFView pdfView, ProgressBar progressBar) {
        String TAG = "PDF_LOAD_SINGLE_TAG";
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfURL);
        storageReference.getBytes(MAX_BYTES_PDF).
                addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        pdfView.fromBytes(bytes)
                                .pages(0)
                                .spacing(0)
                                .swipeHorizontal(false)
                                .enableSwipe(false)
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        Log.d(TAG, "onError: "+ t.getMessage());
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }).onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "onPageError: "+ t.getMessage());
                                    }
                                }).onLoad(new OnLoadCompleteListener() {
                                    @Override
                                    public void loadComplete(int nbPages) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }).load();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }
    //function to increment book count
    public  static  void  incrementBookViewCount(String bookid){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("booking");
        databaseReference.child(bookid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String viewsCount = ""+snapshot.child("viewsCount").getValue();
                        if(viewsCount.equals("")||viewsCount.equals("null")){
                            viewsCount="0";
                        }
                        long newViewsCount = Long.parseLong(viewsCount);
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("viewsCount",newViewsCount);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("booking");
                        reference.child(bookid).updateChildren(hashMap);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    //added code
    public  static  void addToCart(Context context,String bookId){
        //check if user is logged in
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            Toast.makeText(context, "Go and login", Toast.LENGTH_SHORT).show();
        }
        else{
            LocalDateTime currentDateTime = Dateutils.getCurrentDateTime();
            int secondsToAdd = 150; // 1 hour

            // Add the specified amount of seconds to the current date and time
            LocalDateTime newDateTime = Dateutils.addSeconds(currentDateTime, secondsToAdd);

            // Format the new date and time
            String formattedDateTime = Dateutils.formatDateTime(newDateTime);
            long timestamp = System.currentTimeMillis();
            //set up the data to add in firebase database
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("bookid",bookId);
            hashMap.put("timestamp",timestamp);
            hashMap.put("expirydate", formattedDateTime);
            //save to the database

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(firebaseAuth.getUid()).child("myCart").child(bookId)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            myapplication.startTimer(context,bookId);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Failed to add to your favorite list due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }
    //function to remove book from my booklist
    public  static  void removeFromFavoriteList(Context context,String bookId){
        //check if user is logged in
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            Toast.makeText(context, "Go and login", Toast.LENGTH_SHORT).show();
        }
        else{
            //remove from database

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(firebaseAuth.getUid()).child("myCart").child(bookId)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Failed remove from your favorite list due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }
    //check if the book is already purchased or not
    public static void checkIsInYourBooks(Context context, String bookId){
        //check if it is in cart or not
        FirebaseAuth firebaseAuth;
        FirebaseUser user;
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        if(user==null){

        }
        else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(firebaseAuth.getUid()).child("myCart").child(bookId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean isInmyCart = snapshot.exists();
                            if(isInmyCart){

                                Toast.makeText(context, "You have already purchased this book", Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }




    }
    //retrieves the books which i have uploaded in the system

    public  static  void myUploadedBooks(Context context,String bookId){
        //check if user is logged in
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            Toast.makeText(context, "Go and login", Toast.LENGTH_SHORT).show();
        }
        else{

            long timestamp = System.currentTimeMillis();
            //set up the data to add in firebase database
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("bookid",bookId);
            hashMap.put("timestamp",timestamp);

            //save to the database

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(firebaseAuth.getUid()).child("myUploadedBooks").child(bookId)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Failed to add to your book list due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }


}
