package com.example.carati;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class viewCategories extends AppCompatActivity {
TextView txtcategorytitle;
FirebaseAuth firebaseAuth;
   private ArrayList<Model_Category> categoryArrayList;
   private  Adapter_Category adapter_category;
    RecyclerView recyclerView;
    private ArrayList<Model_Category> list;
    private EditText search;
    private ImageButton backbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_categories);
        //register the ui elemets
        txtcategorytitle = findViewById(R.id.txtcategorytitle);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        search = findViewById(R.id.search);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        backbtn = findViewById(R.id.backbtn);
        //back button functionality
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),admindashboard.class);
                startActivity(intent);
                finish();
            }
        });
        //load categories to the page
        loadCategories1();
        //search the categories
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapter_category.getFilter().filter(s);
                }
                catch(Exception e){

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    //load categories
    private void loadCategories1() {
        list = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot ds: snapshot.child("Categories").getChildren()){
                    if(ds.hasChild("category")&& ds.hasChild("id")&& ds.hasChild("uid")){
                        String cat1 = ds.child("category").getValue(String.class);
                        String cat2 =  ds.child("id").getValue(String.class);
                        String cat3 = ds.child("uid").getValue(String.class);
                        Model_Category items = new Model_Category(cat2,cat1,cat1);
                        list.add(items);
                    }


                }

                adapter_category = new Adapter_Category(list,viewCategories.this);
                recyclerView.setAdapter(adapter_category);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}