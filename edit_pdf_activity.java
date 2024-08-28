package com.example.carati;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class edit_pdf_activity extends AppCompatActivity {

    TextView editinfo,bookcategory;
    EditText name,description,author,price;
    ImageButton backbtn;
    private  String bookid,bookcat;
    private ProgressDialog progressDialog;
    Button uploadbookbtn;
    private ArrayList<String> categoryTitleArrayList, categoryIDArrayList;
    private static final String TAG = "BOOK_EDIT_TAG";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_pdf);

        //register the textviews
        editinfo = findViewById(R.id.editinfo);
        bookcategory = findViewById(R.id.bookcategory);


        //register edittext
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        author = findViewById(R.id.author);
        price = findViewById(R.id.price);

        //image button
        backbtn = findViewById(R.id.backbtn);
        uploadbookbtn = findViewById(R.id.uploadbookbtn);

        uploadbookbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
                Intent intent = new Intent(getApplicationContext(), pdfListAdmin.class);
                intent.putExtra("category", bookcat);
                startActivity(intent);
                finish();
            }
        });

        bookid = getIntent().getStringExtra("bookid");
        bookcat = getIntent().getStringExtra("bookcategory");
        //set up progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        //load categories and book info
        loadCategories();
        loadBookInfo();
        bookcategory.setText(bookcat);

        bookcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pdfListAdmin.class);
                intent.putExtra("category", bookcat);
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
    String namenew="", authornew="";
    String descriptionnew = "";
   String pricenew="";
   //function to check whether the data is in right format
    private void validateData() {
        namenew = name.getText().toString().trim();
        authornew = author.getText().toString().trim();
        descriptionnew = description.getText().toString().trim();
        pricenew = price.getText().toString().trim();

        if(TextUtils.isEmpty(namenew)){
            Toast.makeText(this, "enter book name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(authornew)){
            Toast.makeText(this, "Enter author name", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(descriptionnew)){
            Toast.makeText(this, "Enter book description", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(pricenew)){
            Toast.makeText(this, "Enter price of book", Toast.LENGTH_SHORT).show();
        }
        else{
            updatePDF();
        }

    }
    //function to update the pdf
    private void updatePDF() {
        Log.d(TAG, "updatePDF: Updating pdf");
        progressDialog.setMessage("Updating book info");
        progressDialog.show();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("name", namenew);
        hashMap.put("description",descriptionnew);
        hashMap.put("author",authornew);
        hashMap.put("price",""+ pricenew);

        //start updating
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("booking");
        dbref.child(bookid)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onSuccess: Update successfull");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to upload due to"+ e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(edit_pdf_activity.this, "failed to update", Toast.LENGTH_SHORT).show();
                    }
                });


    }
    //function to load info about a book
    private void loadBookInfo() {
        Log.d(TAG, "loadBookInfo: loading book info");
        DatabaseReference refBook = FirebaseDatabase.getInstance().getReference("booking");
        refBook.child(bookid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        selectedCategoryID = ""+snapshot.child("id").getValue();
                        String description1 = ""+ snapshot.child("description").getValue();
                        String name1 = ""+ snapshot.child("name").getValue();
                        String price1 = "" + snapshot.child("price").getValue();
                        String author1 = "" + snapshot.child("author").getValue();
                        author.setText(author1);
                        price.setText(price1);
                        name.setText(name1);
                        description.setText(description1);

                        Log.d(TAG, "onDataChange: Loading category");
                        DatabaseReference refboocategory = FirebaseDatabase.getInstance().getReference("Categories");
                        refboocategory.child(selectedCategoryID)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String categ = ""+snapshot.child("category").getValue();
                                        bookcategory.setText(categ);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String selectedCategoryID, getSelectedCategoryTitle;
    private void categoryDialog(){
        //make string array from arraylist
        String[] categoriesArray = new String[categoryTitleArrayList.size()];
        for( int i=0; i<categoryTitleArrayList.size(); i++){
            categoriesArray[i]= categoryTitleArrayList.get(i);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose category")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedCategoryID  = categoryIDArrayList.get(which);
                        getSelectedCategoryTitle = categoryTitleArrayList.get(which);
                        bookcategory.setText(getSelectedCategoryTitle);
                    }
                }).show();

    }
    //function to load several book categories which exist in the database
    private void loadCategories() {
        categoryTitleArrayList = new ArrayList<>();
        categoryIDArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Categories");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear();
                categoryIDArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    String categ = ""+ ds.child("category").getValue();
                    String id = ""+ds.child("id").getValue();
                    categoryTitleArrayList.add(categ);
                    categoryIDArrayList.add(id);
                    Log.d(TAG, "onDataChange: ID"+id);
                    Log.d(TAG, "onDataChange: category"+categ);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}