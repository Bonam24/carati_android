package com.example.carati;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carati.models.ModelPdf;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class myUploadedBooks extends AppCompatActivity {
    private TextView displaybook;
    private FirebaseAuth firebaseAuth;
    ImageButton backbtn;
    RecyclerView bookdisplay;
    private ArrayList<ModelPdf> pdfArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_uploaded_books);
        //register ui elements
        displaybook = findViewById(R.id.displaybook);
        backbtn = findViewById(R.id.backbtn);
        bookdisplay = findViewById(R.id.bookdisplay);
        firebaseAuth = FirebaseAuth.getInstance();
        //loads the rented books
        loadRentedBooks();
        //back button
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    //function to load rented books
    private void loadRentedBooks() {
        pdfArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .child("myUploadedBooks")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String bookid =""+ ds.child("bookid").getValue();
                            ModelPdf model = new ModelPdf();
                            model.setId(bookid);

                            pdfArrayList.add(model);

                        }
                        AdapterForRentBooks adapterForRentBooks = new AdapterForRentBooks(myUploadedBooks.this,pdfArrayList);
                        bookdisplay.setAdapter(adapterForRentBooks);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}