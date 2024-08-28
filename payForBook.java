package com.example.carati;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carati.models.ModelPdf;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RaveUiManager;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class payForBook extends AppCompatActivity {
    Button button;
    TextView textView;
    ProgressDialog progressDialog;
    View view;
    String bookid;
    TextView bookdetailstv,categorylabel,category,authorlabel,author,sizelabel,size,viewcountlabel,viewcount,description,name;
    ImageButton backbtn;
    ProgressBar progressBar;
    Context context;
    String price;
    Double fprice;

    PDFView pdfView;
    String TAG=" Error_PAY_BOOK";

    private ArrayList<ModelPdf> pdfArrayList;
    private  adapterForBoughtBooks adapterForBoughtBooks1;
    FirebaseAuth firebaseAuth;

    private static final int REQUEST_CODE_SCHEDULE_EXACT_ALARM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pay_for_book);
        //register ui elements
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

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
        price = intent.getStringExtra("price");
        loadBookDetails();


        fprice = Double.valueOf(price)*2.73;

        button = findViewById(R.id.button);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makepay(view);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    //function to handle payment
    public  void  makepay(View view){
        progressDialog.show();
        final Activity activity = this;
        UUID uuid = UUID.randomUUID();

        new RaveUiManager(payForBook.this).setAmount(fprice)
                .setCurrency("NGN")
                .setEmail("bosene@usiu.ac.ke")
                .setfName("Bonam")
                .setlName("JR")
                .setPublicKey("FLWPUBK_TEST-26d9fae9b99fb47ae44dff13f371147c-X")
                .setEncryptionKey("FLWSECK_TESTcd8d1050924f")
                .setTxRef(uuid.toString())
                .acceptAccountPayments(true)
                .acceptCardPayments(true)
                .acceptMpesaPayments(true)
                .allowSaveCardFeature(true)
                .onStagingEnv(true)
                .isPreAuth(false)
                .shouldDisplayFee(true)
                .showStagingLabel(true)
                .initialize();

}
    //function to check if the payment was successfull
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        progressDialog.dismiss();
        if(requestCode== RaveConstants.RAVE_REQUEST_CODE && data!=null){
            String message = data.getStringExtra("response");
            if (resultCode== RavePayActivity.RESULT_SUCCESS){
                myapplication.addToCart(getApplicationContext(),bookid);
                Toast.makeText(this, "Book paid for Successfully", Toast.LENGTH_SHORT).show();

                //startActivity(new Intent(this,HomeScreenActivity.class));

            }
            else if (resultCode==RavePayActivity.RESULT_ERROR){
                Toast.makeText(this, "Erro"+ message, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onActivityResult: "+"Erro"+ message);
            }
            else if(resultCode==RavePayActivity.RESULT_CANCELLED){
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                //myapplication.removeFromFavoriteList(getApplicationContext(),bookid);
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }


    }


    //function to load the details of the book to be purchased
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