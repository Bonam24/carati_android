package com.example.carati;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.carati.models.ModelPdf;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class profileActivity extends AppCompatActivity {
    TextView txtemail,txtbooks,booknumberdisplay,rbookdisplay,norbooks;
    EditText emaildisplay;

    ImageButton  backbtn;

    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelPdf> pdfArrayList;
    private adapterForBoughtBooks adapterForBoughtBooks1;

    private static  final String TAG = "PROFILE_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        //register ui elements
        txtemail = findViewById(R.id.txtemail);
        txtbooks = findViewById(R.id.txtbooks);
        norbooks = findViewById(R.id.norbooks);
        rbookdisplay = findViewById(R.id.rbookdisplay);


        emaildisplay = findViewById(R.id.emaildisplay);
        booknumberdisplay = findViewById(R.id.booknumberdisplay);

        booknumberdisplay.setText("5");
        rbookdisplay.setText("1");
        backbtn = findViewById(R.id.backbtn);

        firebaseAuth = FirebaseAuth.getInstance();
        //load user details
        loadUserDetails();

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


    private void loadUserDetails() {
        Log.d(TAG, "loadUserDetails: Loading user info...");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String email = ""+snapshot.child("email").getValue();

                        emaildisplay.setText(email);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}