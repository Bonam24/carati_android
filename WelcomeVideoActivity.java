package com.example.carati;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WelcomeVideoActivity extends AppCompatActivity {

    ImageView video;
    Button getstarted;
    ImageView logo;
    TextView message,welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_video);
        //register ui elements
        video = findViewById(R.id.video);
        getstarted = findViewById(R.id.getstarted);
        logo = findViewById(R.id.logo);
        welcome = findViewById(R.id.welcome);
        message = findViewById(R.id.message);

        /*Uri uri =  Uri.parse("android.resource://" + getPackageName()+ "/"+ R.raw.backgroundvideo);
        video.setVideoURI(uri);
        video.start();
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);

            }
        });

        */
        //start the gif picture
        getstarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, WelcomeVideoActivity.class));
    }
    /*
    @Override
    protected void onPostResume() {
        video.resume();
        super.onPostResume();
    }

    @Override
    protected void onRestart() {
        video.start();
        super.onRestart();
    }

    @Override
    protected void onPause() {
        video.suspend();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        video.stopPlayback();
        super.onDestroy();
    }*/
}