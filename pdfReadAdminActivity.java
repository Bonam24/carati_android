package com.example.carati;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

public class pdfReadAdminActivity extends AppCompatActivity {
    ImageButton backbtn;
    TextView readbooktv, subTitle;
    PDFView pdfView;
    ProgressBar progressBar;
    private String bookid;

    private  static  final String TAG = "PDF_VIEW_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pdf_read_admin);
        //register ui elements
        backbtn = findViewById(R.id.backbtn);
        readbooktv = findViewById(R.id.readbooktv);
        subTitle = findViewById(R.id.subTitle);


        pdfView = findViewById(R.id.pdfView);
        progressBar = findViewById(R.id.progressBar);

        Intent intent = getIntent();
        bookid = intent.getStringExtra("bookid");
        Log.d(TAG, "onCreate: Bookid"+ bookid);
        loadBookDetails();


        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    //function to load the details of the book from the database
    private void loadBookDetails() {
        //get book url using book id
        Log.d(TAG, "loadBookDetails: get pfd url");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("booking");
        ref.child(bookid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get book url
                        String pdfUrl = ""+snapshot.child("url").getValue();
                        Log.d(TAG, "onDataChange: pdf url"+ pdfUrl);

                        //load pdf using that url from firebase storage
                        loadBookFromUrl(pdfUrl);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    //function to load pages of the book from the database using a url
    private void loadBookFromUrl(String pdfUrl) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        storageReference.getBytes(constants.MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        progressBar.setVisibility(View.GONE);
                        //load pdf using bytes
                        pdfView.fromBytes(bytes)
                                .swipeHorizontal(false)
                                .onPageChange(new OnPageChangeListener() {
                                    @Override
                                    public void onPageChanged(int page, int pageCount) {
                                        //set current and total pages in toolbar subtitle
                                        int currentPage = (page +1); //added +1 because it will start from zero

                                        subTitle.setText(currentPage+"/" + pageCount);
                                        Log.d(TAG, "onPageChanged: page count");

                                    }
                                }).onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        Toast.makeText(pdfReadAdminActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }).onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        Toast.makeText(pdfReadAdminActivity.this, "error on page"+page+t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }).load();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "onFailure: "+e.getMessage());
                    }
                });
    }
}