package com.example.carati;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carati.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class pdfDetailsActivity extends AppCompatActivity {
    TextView bookdetailstv,categorylabel,category,authorlabel,author,sizelabel,size,viewcountlabel,viewcount,description,name;
    ImageButton backbtn;
    ProgressBar progressBar;
    String bookid;
    PDFView pdfView;
    Button readbook;
    private ArrayList<ModelPdf> pdfArrayList;
    private  adapterForBoughtBooks adapterForBoughtBooks1;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pdf_details);

        //register ui elements
        bookdetailstv = findViewById(R.id.bookdetailstv);
        categorylabel = findViewById(R.id.categorylabel);
        category = findViewById(R.id.category);
        authorlabel = findViewById(R.id.authorlabel);
        author = findViewById(R.id.author);
        sizelabel = findViewById(R.id.sizelabel);
        size = findViewById(R.id.size);
        viewcountlabel = findViewById(R.id.viewcountlabel);
        viewcount = findViewById(R.id.viewcount);
        description = findViewById(R.id.description);
        readbook = findViewById(R.id.readbook);

        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        progressBar = findViewById(R.id.progressBar);
        pdfView = findViewById(R.id.pdfView);
        name = findViewById(R.id.name);
        firebaseAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        bookid = intent.getStringExtra("bookid");
        loadBookDetails();
        myapplication.incrementBookViewCount(bookid);

        //loadBoughtBooks();

        readbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), pdfReadAdminActivity.class);
                intent1.putExtra("bookid",bookid);
                startActivity(intent1);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    //function to load bought pdf details
    private void loadBoughtBooks() {
        pdfArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("myCart")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for(DataSnapshot ds :snapshot.getChildren()){
                            String bookID = ""+ds.child("bookid").getValue();
                            ModelPdf modelPdf = new ModelPdf();
                            modelPdf.setId(bookID);
                            pdfArrayList.add(modelPdf);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    //function to load book details
    private void loadBookDetails() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("booking");
        databaseReference.child(bookid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String title = ""+ snapshot.child("name").getValue();
                        String categoryvalue = ""+ snapshot.child("category").getValue();
                        String descriptionvalue = ""+ snapshot.child("description").getValue();
                        String vcount= ""+ snapshot.child("viewsCount").getValue();
                        String url = ""+ snapshot.child("url").getValue();

                        myapplication.loadPdffromURL_SinglePage(""+url,
                                ""+title,pdfView,progressBar);
                        myapplication.loadPDFsize(""+url,""+title,size);
                        name.setText(title);
                        category.setText(categoryvalue);
                        description.setText(descriptionvalue);
                        viewcount.setText(vcount);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}