package com.example.carati;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class addbookcategory extends AppCompatActivity {
    //initializing variables
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    EditText categoryET;
    Button submitbtn;
    ImageButton backbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_addbookcategory);
        //registering UI elements
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        submitbtn = findViewById(R.id.submitbtn);
        categoryET = findViewById(R.id.categoryET);
        //function for submitting the category data
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
                categoryET.setText("");
            }
        });
        backbtn= findViewById(R.id.backbtn);
        //function for back button
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), admindashboard.class);
                startActivity(intent);
                finish();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
private  String category;
    private void validateData() {
        //validating the data to be sent to the database
       category = categoryET.getText().toString().trim();

       if(TextUtils.isEmpty(category)){
           Toast.makeText(this, "Please  enter category", Toast.LENGTH_SHORT).show();
       }
       else{
           addCategoryFirebase();
       }

    }
    //function to access database and add a category to it
    private void addCategoryFirebase() {
        progressDialog.setMessage("Adding category...");
        progressDialog.show();
        //put elements in hashmap
        long timestamp = System.currentTimeMillis();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id",""+timestamp);
        hashMap.put("category",""+category);
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("uid",""+firebaseAuth.getUid());
        //saving elements in datatabase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Categories");
        databaseReference.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        progressDialog.dismiss();
                        Toast.makeText(addbookcategory.this, "successfully added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(addbookcategory.this, "failed to add"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });




    }
}