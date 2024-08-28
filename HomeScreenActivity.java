package com.example.carati;

import static com.example.carati.constants.MAX_BYTES_PDF;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carati.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenActivity extends AppCompatActivity  {
     DrawerLayout drawerLayout;
     NavigationView  navigationView;
    ActionBarDrawerToggle drawerToggle;
    ImageButton menubutton;
    Button  artsbtn, sciencebtn, newbtn;
    SearchView searchbook;
    ImageView bimg, bimg1;

    TextView txtcategory,home;
    RecyclerView bookdisplay;
    private ArrayList<ModelPdf> pdfArrayList;
    String categoryid="Science";
    String arts1= "arts";

    adapterpdf adapterpdf1;

FirebaseAuth auth;
DatabaseReference databaseReference;
List<retrievePDF> uploadedPDF;
FirebaseUser user;
String TAG ="Tag for Homescreen";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);
       //registering the ui elements
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView  = findViewById(R.id.nav_view);
        menubutton = findViewById(R.id.menubutton);
        bookdisplay = findViewById(R.id.bookdisplay);
        artsbtn = findViewById(R.id.artsbtn);

        sciencebtn = findViewById(R.id.sciencebtn);
        searchbook = findViewById(R.id.searchbook);
        bimg = findViewById(R.id.bimg);
        bimg1 = findViewById(R.id.bimg1);
        home = findViewById(R.id.home);


        //load pdf list
        uploadedPDF = new ArrayList<>();
        loadPDFlist("Science");
        //button to open menu
        menubutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();;
            }
        });
        //arts button and function to open books under the arts category
        artsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPDFlist("Arts");
                artsbtn.setBackgroundColor(Color.parseColor("#139F48"));
                sciencebtn.setBackgroundColor(Color.parseColor("#d52930"));
            }
        });
        //science button and function to open books under the arts category
        sciencebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPDFlist("Science");
                sciencebtn.setBackgroundColor(Color.parseColor("#139F48"));
                artsbtn.setBackgroundColor(Color.parseColor("#d52930"));
            }
        });
        //function to navigate to respective pages in the application
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.logout){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(item.getItemId()==R.id.sellbook){
                    Intent intent = new Intent(getApplicationContext(), uploadpdf.class);
                    startActivity(intent);
                    finish();
                }

                else if(item.getItemId()==R.id.booklibrary){
                    Intent intent = new Intent(getApplicationContext(), mybooks.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(HomeScreenActivity.this, "Your rented books", Toast.LENGTH_SHORT).show();
                }
                else if(item.getItemId()==R.id.home){
                    Intent intent = new Intent(getApplicationContext(), chatBot.class);
                    startActivity(intent);
                    finish();
                }
                else if(item.getItemId()==R.id.profile){
                    Intent intent = new Intent(getApplicationContext(), profileActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(item.getItemId()==R.id.myuploadedbooks){
                    Intent intent = new Intent(getApplicationContext(), myUploadedBooks.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(HomeScreenActivity.this, "Your books on rent", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });



        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user==null){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }

        //fucntion to filter pdfs



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    //function to load list of pdfs
   private void loadPDFlist( String category1) {
        pdfArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("booking");
        databaseReference.orderByChild("category").equalTo(category1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelPdf modelPdf = ds.getValue(ModelPdf.class);
                            pdfArrayList.add(modelPdf);

                        }
                        adapterpdf adapterpdf = new adapterpdf(HomeScreenActivity.this, pdfArrayList);
                        bookdisplay.setAdapter(adapterpdf);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, HomeScreenActivity.class));
    }
}