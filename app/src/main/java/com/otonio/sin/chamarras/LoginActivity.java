package com.otonio.sin.chamarras;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.Bind;
import retrofit.Callback;
import retrofit.RetrofitError;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    public static final String USER_PREF = "MyPrefs";
    SharedPreferences sharedPreferences;

    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        sharedPreferences=getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);

        int[] photos={R.drawable.wallpaper1,R.drawable.wallpaper2,R.drawable.wallpaper3,R.drawable.wallpaper4,R.drawable.wallpaper5,R.drawable.wallpaper6,R.drawable.wallpaper9,R.drawable.wallpaper10,R.drawable.wallpaper11};
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollViewParent);
        Random ran = new Random();
        int i = ran.nextInt(photos.length);
        scrollView.setBackgroundResource(photos[i]);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignupActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        //Log.d(TAG, "Login");

        //*
        if (!validate()) {
            onLoginFailed("Verificar que los datos ingresados sean correctos.");
            return;
        }
        /* */

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Iniciando sesión...");
        progressDialog.show();

        final String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        Date now = c.getTime();
        String mTimeString = sdf.format(now);

        ConnectionManager.getService().loginUsuarioOtonioService(email, password, mTimeString, new Callback<ResponseLoginUsuario>() {
            @Override
            public void success(ResponseLoginUsuario result, retrofit.client.Response response) {
                if(result.getIdUser()>0){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("id_user",result.getIdUser());
                    editor.putString("user_name",result.getUser_name());
                    editor.putString("email",email);
                    editor.apply();

                    progressDialog.hide();
                    progressDialog.dismiss();
                    call_home_activity();
                }else{
                    progressDialog.setMessage(result.getMsg());
                    _loginButton.setEnabled(true);
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                }
                            },
                    2500);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void call_home_activity(){
        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Ingresa un email válido");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 16) {
            _passwordText.setError("La contraseña debe de tener entre 4 y 16 caracteres");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
