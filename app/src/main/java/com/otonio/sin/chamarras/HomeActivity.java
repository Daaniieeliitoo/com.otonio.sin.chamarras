package com.otonio.sin.chamarras;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import java.util.Random;

import butterknife.ButterKnife;
import butterknife.Bind;

public class HomeActivity extends AppCompatActivity {
    public static final String USER_PREF = "MyPrefs";
    SharedPreferences sharedPreferences;

    @Bind(R.id.btn_continue) Button _continueButton;
    @Bind(R.id.btn_knyou) Button _knyouButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        sharedPreferences=getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);

        int[] photos={R.drawable.knyouads1,R.drawable.knyouads2,R.drawable.knyouads3,R.drawable.knyouads4,R.drawable.knyouads5,R.drawable.knyouads6,R.drawable.knyouads7,R.drawable.knyouads8,R.drawable.knyouads9,R.drawable.knyouads10};
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        Random ran = new Random();
        int i = ran.nextInt(photos.length);
        scrollView.setBackgroundResource(photos[i]);

        _continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_video_activity();
            }
        });

        _knyouButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                //https://play.google.com/store/apps/details?id=com.knyou.knyoulite
                //market://details?id=com.knyou.knyoulite
                intent.setData(Uri.parse("market://details?id=com.knyou.knyoulite"));
                startActivity(intent);
            }
        });
    }

    private void call_video_activity(){
        Intent intent = new Intent(getApplicationContext(),VideomainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
