package com.otonio.sin.chamarras;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomemainActivity extends AppCompatActivity {
    public static final String USER_PREF = "MyPrefs";
    SharedPreferences sharedPreferences;

    @Bind(R.id.btn_start_challenge) Button _startChallengeButton;
    @Bind(R.id.btn_end_challenge) Button _endChallengeButton;
    @Bind(R.id.text_main) TextView _textMain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);
        ButterKnife.bind(this);

        sharedPreferences=getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        final int id_user = sharedPreferences.getInt("id_user",0);
        final ProgressDialog progressDialog = new ProgressDialog(HomemainActivity.this, R.style.AppTheme_Dark_Dialog);

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        final Date now = c.getTime();
        final String mTimeString = sdf.format(now);

        int[] photos={R.drawable.wallpaper1,R.drawable.wallpaper2,R.drawable.wallpaper3,R.drawable.wallpaper4,R.drawable.wallpaper5,R.drawable.wallpaper6,R.drawable.wallpaper9,R.drawable.wallpaper10,R.drawable.wallpaper11};
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollViewParent);
        Random ran = new Random();
        int i = ran.nextInt(photos.length);
        scrollView.setBackgroundResource(photos[i]);

        _startChallengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("START");
                _startChallengeButton.setEnabled(false);
                ConnectionManager.getService().empiezaReto(id_user, mTimeString, new Callback<ResponseEmpiezaReto>() {
                    @Override
                    public void success(ResponseEmpiezaReto result, Response response) {
                        if(result.getSuccess()>0){
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("¡Has empezado el reto!");
                            progressDialog.show();

                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.hide();
                                            progressDialog.dismiss();
                                            call_ads2_activity();
                                        }
                                    }, 1500);
                        }else{
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("Hubo un error. Intenta más tarde.");
                            progressDialog.show();
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            _startChallengeButton.setEnabled(true);
                                            progressDialog.hide();
                                        }
                                    }, 1500);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) { error.printStackTrace(); }
                });
            }
        });

        _endChallengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("STOP");
                _endChallengeButton.setEnabled(false);
                ConnectionManager.getService().terminaReto(id_user, mTimeString, new Callback<ResponseTerminaReto>() {
                    @Override
                    public void success(ResponseTerminaReto result, Response response) {
                        if(result.getSuccess()>0){
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("Reto terminado...");
                            progressDialog.show();

                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.hide();
                                            progressDialog.dismiss();
                                            call_ads2_activity();
                                        }
                                    }, 1500);
                        }else{
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("Hubo un error. Intenta más tarde.");
                            progressDialog.show();
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            _endChallengeButton.setEnabled(true);
                                            progressDialog.hide();
                                        }
                                    }, 1500);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) { error.printStackTrace(); }
                });
            }
        });

        ConnectionManager.getService().consultaUltimoReto(id_user, new Callback<ResponseUltimoReto>() {
            @Override
            public void success(ResponseUltimoReto result, retrofit.client.Response response) {
                if(result.getSuccess()>0){
                    if(!(result.getDate_end()).equals("")){
                        //RETO FINALIZADO
                        try {
                            Date date1 = sdf.parse(result.getDate_start());
                            Date date2 = sdf.parse(result.getDate_end());
                            String difference = getDateDifference(date1, date2);
                            _textMain.setText("Duraste " + difference + " con el reto");
                        } catch (ParseException e) { e.printStackTrace(); }
                    }else{
                        //RETO INICIADO
                        _startChallengeButton.setVisibility(View.INVISIBLE);
                        _endChallengeButton.setVisibility(View.VISIBLE);
                        try {
                            Date date1 = sdf.parse(result.getDate_start());
                            Date date2 = sdf.parse(sdf.format(now));
                            String difference = getDateDifference(date1, date2);
                            _textMain.setText("Llevas " + difference + " con el reto");
                        } catch (ParseException e) { e.printStackTrace(); }
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) { error.printStackTrace(); }
        });
    }

    private void call_ads2_activity(){
        Intent intent = new Intent(getApplicationContext(),Ads2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public String getDateDifference(Date startDate, Date endDate){
        long different = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf("%d days, %d hours, %d minutes, %d seconds%n", elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

        return String.valueOf(elapsedDays) + " días y " + String.valueOf(elapsedHours) + " horas";

    }

}
