package com.otonio.sin.chamarras;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VideomainActivity extends AppCompatActivity {
    @Bind(R.id.btn_continue) Button _continueButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);

        VideoView videoView = (VideoView)findViewById(R.id.videoView);
        String path = "android.resource://" + getPackageName() + "/" + R.raw.video;
        System.out.println(path);
        videoView.setVideoURI(Uri.parse(path));
        videoView.start();

        _continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_main_home_activity();
            }
        });
    }

    private void call_main_home_activity(){
        Intent intent = new Intent(getApplicationContext(),HomemainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
