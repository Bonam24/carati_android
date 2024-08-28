package com.example.carati;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class chatBot extends AppCompatActivity {
    Button send;
    ImageButton backbtn;
    TextView displaytext;
    EditText prompt;
    ProgressBar progressBar;
    TextView displayAnswer;
    ImageButton read;
    TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_bot);
        //registration of buttons and other elements which are found in the UI
        send = findViewById(R.id.send);
        backbtn = findViewById(R.id.backbtn);
        displaytext = findViewById(R.id.displaytext);
        prompt = findViewById(R.id.prompt);
        progressBar = findViewById(R.id.progressBar);
        displayAnswer = findViewById(R.id.displayAnswer);
        read = findViewById(R.id.read);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeScreenActivity.class));
            }
        });
        //implements the read function
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status==TextToSpeech.SUCCESS){
                            tts.setLanguage(Locale.ENGLISH);
                            tts.setSpeechRate(1.0f);
                            String respond = displayAnswer.getText().toString();
                            if(respond==""){
                                Toast.makeText(chatBot.this, "No text to read", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                tts.speak(respond,TextToSpeech.QUEUE_ADD,null);
                            }

                        }
                    }
                });
            }
        });
        //implements the function to prompt the ai to give a response
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeminiPro model = new GeminiPro();
                String query = prompt.getText().toString();
                progressBar.setVisibility(View.VISIBLE);

                displayAnswer.setText("");
                prompt.setText("");

                model.getResponse(query, new ResponseCallback() {
                    @Override
                    public void onResponse(String response) {
                        displayAnswer.setText(response);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(chatBot.this, "error"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
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
}