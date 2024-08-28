package com.example.carati;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupScreen extends AppCompatActivity {

    private TextView emailtext, passwordtext, textViewinfo,textViewlogin;
    private EditText emailtextfield,passwordtextfield;
    Button signupbtn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_screen);
        //register ui elements
        mAuth = FirebaseAuth.getInstance();
        emailtext = findViewById(R.id.emailtext);
        passwordtext = findViewById(R.id.passwordtext);
        textViewinfo = findViewById(R.id.textViewinfo);
        textViewlogin = findViewById(R.id.textViewlogin);

        emailtextfield = findViewById(R.id.emailtextfield);
        passwordtextfield = findViewById(R.id.passwordtextfield);

        signupbtn = findViewById(R.id.loginbtn);

        textViewlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class );
                startActivity(intent);
                finish();
            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = emailtextfield.getText().toString().trim();
                password = passwordtextfield.getText().toString().trim();

                if(!Utils_to_check_email.isValidEmail(email)){
                    Toast.makeText(SignupScreen.this, "please put in a valid email", Toast.LENGTH_SHORT).show();
                }
                if(password.length()<6){
                    Toast.makeText(SignupScreen.this, "Please input a password greater than 6", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(SignupScreen.this, "enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(SignupScreen.this, "enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    updateUserInfo();

                                } else {
                                    // If sign in fails, display a message to the user.

                                    Toast.makeText(SignupScreen.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    //update user info when the user signs up
    private void updateUserInfo() {
        long timestamp = System.currentTimeMillis();
        String uid = mAuth.getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("email",emailtextfield.getText().toString());
        hashMap.put("usertype","user");
        hashMap.put("timestamp", timestamp);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(SignupScreen.this, "Account created..", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });



    }
}