package com.otonio.sin.chamarras;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    public static final String USER_PREF = "MyPrefs";
    SharedPreferences sharedPreferences;

    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginText;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        sharedPreferences=getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);

        int[] photos={R.drawable.wallpaper1,R.drawable.wallpaper2,R.drawable.wallpaper3,R.drawable.wallpaper4,R.drawable.wallpaper5,R.drawable.wallpaper6,R.drawable.wallpaper9,R.drawable.wallpaper10,R.drawable.wallpaper11};
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollViewParent);
        Random ran = new Random();
        int i = ran.nextInt(photos.length);
        scrollView.setBackgroundResource(photos[i]);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void signup() {
        //Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed("Verificar que los datos ingresados sean correctos.");
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Estamos creando tu cuenta...");
        progressDialog.show();

        final String name = _nameText.getText().toString();
        final String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        Date now = c.getTime();
        String mTimeString = sdf.format(now);

        ConnectionManager.getService().registraUsuarioOtonioService(name, email, password, mTimeString, new Callback<ResponseRegistroUsuario>() {
            @Override
            public void success(final ResponseRegistroUsuario result, retrofit.client.Response response) {
                if(result.getExito()>0){
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putInt("id_user",result.getIdUser());
                    editor.putString("user_name",name);
                    editor.putString("email",email);
                    editor.apply();

                    progressDialog.hide();
                    progressDialog.dismiss();
                    call_home_activity();
                }else{
                    progressDialog.setMessage(result.getMsg());
                    _signupButton.setEnabled(true);
                    new android.os.Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                if(result.getId_case()==2) {
                                    progressDialog.dismiss();
                                    call_login_activity();
                                }
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

    private void call_login_activity(){
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed(String text_msg) {
        Toast.makeText(getBaseContext(), text_msg, Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 4) {
            _nameText.setError("Por lo menos 4 caracteres");
            valid = false;
        } else {
            _nameText.setError(null);
        }


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