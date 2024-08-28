package com.example.carati;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class uploadpdf extends AppCompatActivity {
Button uploadbookbtn;
TextView textviewuploadbook,textviewbookpdf,bookcategory;

EditText edittextbookname,edittextbookdescription,edittextbookprice,bookauthor;

private FirebaseAuth firebaseAuth;

private ProgressDialog progressDialog;
ImageButton attachbookicon, backbtn;
RelativeLayout toppart;
private ArrayList<Model_Category> model_categoryList;
private ArrayList<model_category_for_books> model_category_for_booksArrayList;
private ArrayList<String> categoryTitleArraylist,categoryIDArraylist;


//tag for debudding

    private static final  String TAG = "ADD_PDF_TAG";
    private static final int PDF_PICK_CODE = 1000;

    //pdf uri
    private Uri pdfuri=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_uploadpdf);
        /*Buttons*/
        backbtn = findViewById(R.id.backbtn);
        uploadbookbtn = findViewById(R.id.uploadbookbtn);
        toppart = findViewById(R.id.toppart);
        firebaseAuth =  FirebaseAuth.getInstance();
        loadCategories();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait");
        progressDialog.setCanceledOnTouchOutside(false);



        //textviews
        textviewbookpdf = findViewById(R.id.textviewbookpdf);
        textviewuploadbook = findViewById(R.id.textviewuploadbook);
        bookcategory = findViewById(R.id.bookcategory);

        //edittext

        edittextbookdescription = findViewById(R.id.edittextbookdescription);
        edittextbookprice = findViewById(R.id.edittextbookprice);
        edittextbookname = findViewById(R.id.edittextbookname);
        bookauthor = findViewById(R.id.bookauthor);

        attachbookicon = findViewById(R.id.attachbookicon);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });

        attachbookicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfpickIntent();
            }
        });
        uploadbookbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();

            }
        });

        bookcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryPickDialog();
            }
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    //load pdf categories for users to select
    private void loadCategories() {
        Log.d(TAG, "loadCategories: Loading pdf categories...");
        model_category_for_booksArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                model_category_for_booksArrayList.clear();
                categoryTitleArraylist = new ArrayList<>();
                categoryIDArraylist = new ArrayList<>();

                for(DataSnapshot ds:snapshot.child("Categories").getChildren()){
                    String cat1 = ds.child("category").getValue(String.class);
                    String categoryID = ds.child("id").getValue(String.class);
                    String categoryTitle = ds.child("category").getValue(String.class);
                    model_category_for_books items = new model_category_for_books(cat1);
                    model_category_for_booksArrayList.add(items);
                    categoryIDArraylist.add(categoryID);
                    categoryTitleArraylist.add(categoryTitle);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private String selectedcategoryID, selectedcategorytitle;
    //function to be able to pick an option in the pop up dialog
    private void categoryPickDialog() {
        Log.d(TAG, "categoryPickDialog: showing category pick dialog");


        String[] categoriesArray = new String[categoryTitleArraylist.size()];
        for(int i=0; i<categoryTitleArraylist.size(); i++){
            categoriesArray[i]= categoryTitleArraylist.get(i);

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Category" )
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    // handle item clicked
                        //get clicked item
                        //String category = categoriesArray[which];old one
                        selectedcategorytitle = categoryTitleArraylist.get(which);
                        selectedcategoryID = categoryIDArraylist.get(which);
                        //set to category textview
                        //bookcategory.setText(category); old one
                        bookcategory.setText(selectedcategorytitle);
                        Log.d(TAG, "onClick: selected category"+ category);
                    }
                }).show();
    }

    String name,description, category, price, author;
    //function to validate the data which is going to the datatabase
    private void validateData() {
    //get data

        Log.d(TAG, "validateData: Validating data");
        name = edittextbookname.getText().toString().trim();
        description = edittextbookdescription.getText().toString().trim();
        category = bookcategory.getText().toString().trim();
        author = bookauthor.getText().toString().trim();
        price = edittextbookprice.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "fill in the book name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "fill in the book description", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(category)){
            Toast.makeText(this, "fill in the book category", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(price)){
            Toast.makeText(this, "fill in the book price", Toast.LENGTH_SHORT).show();
        }
        else if(pdfuri==null){
            Toast.makeText(this, "pick pdf", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(author)){
            Toast.makeText(this, "enter book author", Toast.LENGTH_SHORT).show();
        }
        else{
            uploadPDFToStorage();
        }

    }

    private void uploadPDFToStorage() {
        Log.d(TAG, "uploadPDFToStorage: Uploading to storage");
        progressDialog.setMessage("uploading pdf");
        progressDialog.show();
        long timestamp  = System.currentTimeMillis();

        String filePathAndName ="Books/"+timestamp;

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfuri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: PDF uploaded to storage");
                        Log.d(TAG, "onSuccess: Getting pdf url");
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                            String uploadedPdfUrl = ""+uriTask.getResult();

                            //upload to  firebase
                            uploadpdfinfoToDb(uploadedPdfUrl,timestamp);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: PDF upload failed due to"+ e.getMessage());
                        Toast.makeText(uploadpdf.this, "pdf failed to upload due to"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



    }

    private void uploadpdfinfoToDb(String uploadedPdfUrl, long timestamp) {
        Log.d(TAG, "uploadpdfinfoToDb: Upload pdf to db storage");
        progressDialog.setMessage("Uploading to database");
        String uid = firebaseAuth.getUid();

        //set up data to upload
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id",""+timestamp);
        hashMap.put("uid",uid);
        hashMap.put("name", name);
        hashMap.put("description",description);
        hashMap.put("category", category);
        hashMap.put("author",author);
        hashMap.put("price",""+ price);
        hashMap.put("url", uploadedPdfUrl);
        hashMap.put("Timestamp",String.valueOf(timestamp));
        //added
        hashMap.put("viewsCount",0);
        //add the book under the user who has uploaded the book
        myapplication.myUploadedBooks(getApplicationContext(),""+timestamp);


        //db refernce
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("booking");
        databaseReference.child(String.valueOf(timestamp))
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onSuccess: Successfully uploaded");
                        Toast.makeText(getApplicationContext(), "Successfully uploaded", Toast.LENGTH_SHORT).show();
                        resetTheViews();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: Failed to upload due to"+e.getMessage());
                        Toast.makeText(getApplicationContext(), "Failed to upload due to", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //clear the ui elements of any text
    private void resetTheViews() {
        edittextbookname.setText("");
        edittextbookdescription.setText("");
        bookcategory.setText("");
        bookauthor.setText("");
        edittextbookprice.setText("");

    }

    //function to pick file from device
    private void pdfpickIntent() {
        Log.d(TAG, "pdfpickIntent: starting pdf pickIntent");
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select pdf"),PDF_PICK_CODE);

    }
    //function to check result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== RESULT_OK){
            if(requestCode==PDF_PICK_CODE){
                Log.d(TAG, "onActivityResult: PDF Picked");
                pdfuri = data.getData();
                Log.d(TAG, "onActivityResult: URI"+ pdfuri);
            }
        }
        else{
            Log.d(TAG, "onActivityResult: cancelled picking pdf");
            Toast.makeText(this, "cancelled picking pdf", Toast.LENGTH_SHORT).show();
        }
    }


}