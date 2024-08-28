package com.example.carati;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Patterns;
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
import com.google.firebase.auth.FirebaseAuth;

public class forgotPasswordActivity2 extends AppCompatActivity {
    private EditText email;
    private TextView txtchangePassword;
    private ImageButton backbtn;
    private Button btnchangePassword;
    FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password2);

        firebaseAuth = FirebaseAuth.getInstance();


        txtchangePassword = findViewById(R.id.txtchangePassword);
        backbtn = findViewById(R.id.backbtn);
        email = findViewById(R.id.email);

        btnchangePassword = findViewById(R.id.btnchangePassword);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        //handle back button
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        btnchangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    String emailaddress ="";
    private void validateData() {
        emailaddress = email.getText().toString();
        if(emailaddress.isEmpty()){
            Toast.makeText(this, "Enter email address", Toast.LENGTH_SHORT).show();
        }else if(!Patterns.EMAIL_ADDRESS.matcher(emailaddress).matches()){
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
        }
        else {
            recoverPassword();
        }
    }
    //function to send an email link to the users email address
    private void recoverPassword() {
        progressDialog.setMessage("Sending password recovery options to ..."+ emailaddress);
        progressDialog.show();
        firebaseAuth.sendPasswordResetEmail(emailaddress)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(forgotPasswordActivity2.this, "email reset sent to "+ emailaddress, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(forgotPasswordActivity2.this, "Failed to send due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}