package com.example.carati;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class pdfListAdmin extends AppCompatActivity {
    String categoryTitle;
    TextView listofbooks;
    ImageButton backbtn;
    RecyclerView bookRv;
    EditText search;

    private ArrayList<modelPdfAdmin> pdfAdminArrayList;
    private  AdapterPfdAdmin adapterPfdAdmin;
    private  static  final String TAG = "PDF_LIST_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pdf_list_admin);
        //register ui elements
        listofbooks = findViewById(R.id.listofbooks);
        backbtn = findViewById(R.id.backbtn);
        bookRv = findViewById(R.id.bookRv);
        search = findViewById(R.id.search);
        //function to listen for search whena element is searched
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try{
                    adapterPfdAdmin.getFilter().filter(s);
                }
                catch (Exception e){
                    Log.d(TAG, "onTextChanged: "+ e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), viewCategories.class);
                intent.putExtra("category", categoryTitle);
                startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();
        categoryTitle = intent.getStringExtra("category");//the category title for padlist




        loadPdfList();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    //load pdf list
    private void loadPdfList() {
    pdfAdminArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("booking");
        ref.orderByChild("category").equalTo(categoryTitle)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfAdminArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            modelPdfAdmin model = ds.getValue(modelPdfAdmin.class);
                            pdfAdminArrayList.add(model);
                            Log.d(TAG, "onDataChange: "+ model.getName());
                        }
                        adapterPfdAdmin = new AdapterPfdAdmin(pdfListAdmin.this, pdfAdminArrayList);
                        bookRv.setAdapter(adapterPfdAdmin);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}