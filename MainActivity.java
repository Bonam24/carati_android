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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

private TextView emailtext, passwordtext, textViewinfo,textViewforgotinfo;
private EditText emailtextfield,passwordtextfield;
Button signupbtn,loginbtn;
FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        //function to register Ui elements
        emailtext = findViewById(R.id.emailtext);
        passwordtext = findViewById(R.id.passwordtext);
        textViewinfo = findViewById(R.id.textViewinfo);
        textViewforgotinfo = findViewById(R.id.textViewforgotinfo);

        emailtextfield = findViewById(R.id.emailtextfield);
        passwordtextfield = findViewById(R.id.passwordtextfield);

        loginbtn = findViewById(R.id.loginbtn);
        signupbtn = findViewById(R.id.signupbtn);
        //function to do forgot password
        textViewforgotinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, forgotPasswordActivity2.class));
            }
        });
        //function to do sign up
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupScreen.class);
                startActivity(intent);
            }
        });
        //function to do login
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = emailtextfield.getText().toString().trim();
                password = passwordtextfield.getText().toString().trim();

               if(checkdata(email,password)){
                   mAuth.signInWithEmailAndPassword(email, password)
                           .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                               @Override
                               public void onComplete(@NonNull Task<AuthResult> task) {
                                   if (task.isSuccessful()) {
                                       checkUser();
                                       Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();


                                   } else {
                                       Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();

                                       Toast.makeText(MainActivity.this, "Authentication failed.",
                                               Toast.LENGTH_SHORT).show();

                                   }
                               }
                           });
               }

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private boolean checkdata(String email, String password){
        if(TextUtils.isEmpty(email)){
            Toast.makeText(MainActivity.this, "enter email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(MainActivity.this, "enter password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    //function to check user data
    private void checkUser() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userType = ""+snapshot.child("usertype").getValue();

                        if(userType.equals("user")){
                            Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else if (userType.equals("admin")){
                            Intent intent = new Intent(getApplicationContext(), admindashboard.class);
                            startActivity(intent);
                            finish();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}