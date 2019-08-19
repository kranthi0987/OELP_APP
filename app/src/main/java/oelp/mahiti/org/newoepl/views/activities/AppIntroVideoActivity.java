package oelp.mahiti.org.newoepl.views.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.VideoView;

import oelp.mahiti.org.newoepl.R;

public class AppIntroVideoActivity extends AppCompatActivity {


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intro_video);
        VideoView video_view = findViewById(R.id.VideoView);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + "raw/"+ "intro_vid");

//        MediaController mediaControl = new MediaController(this);
        video_view.setMediaController(null);
        Button rlNextButton = findViewById(R.id.btnNext);

        video_view.setVideoURI(uri);
        video_view.start();


        rlNextButton.setOnClickListener(view -> {
            Intent intent = new Intent(AppIntroVideoActivity.this, HomeActivity.class);
            intent.putExtra("UnitClick", true);
            startActivity(intent);
            AppIntroVideoActivity.this.finish();
        });


    }
}
