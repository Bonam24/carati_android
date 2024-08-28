package com.example.carati;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class admindashboard extends AppCompatActivity {
TextView adminwelcome;
Button addcartegorybtn,viewcat,addbooks,viewbooks;
ImageView logoutbtn;
    FirebaseAuth auth;
    FirebaseUser user;

    //Arraylist to store category



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admindashboard);
        adminwelcome = findViewById(R.id.adminwelcome);
       addcartegorybtn = findViewById(R.id.addcartegorybtn);
       viewcat = findViewById(R.id.viewcat);
       logoutbtn = findViewById(R.id.logoutbtn);
       addbooks = findViewById(R.id.addbooks);
       viewbooks = findViewById(R.id.viewbooks);
        // add categories button
       addbooks.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(admindashboard.this, viewCategories.class);
               startActivity(intent);
               finish();
           }
       });
        //view books category
       viewbooks.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(admindashboard.this, viewCategories.class);
               startActivity(intent);
               finish();
           }
       });

        //view categorries button
        viewcat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(admindashboard.this, viewCategories.class);
                startActivity(intent);
                finish();
            }
        });
        //ad categories button
        addcartegorybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),addbookcategory.class);
                startActivity(intent);
            }
        });
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user==null){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
        //log out button
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), WelcomeVideoActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }




}