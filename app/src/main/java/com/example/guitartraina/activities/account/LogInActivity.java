package com.example.guitartraina.activities.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.guitartraina.R;
import com.example.guitartraina.activities.MainActivity;
import com.example.guitartraina.api.IResult;
import com.example.guitartraina.api.VolleyService;
import com.google.android.material.textfield.TextInputLayout;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Objects;


public class LogInActivity extends AppCompatActivity {
    private IResult resultCallback = null;
    private VolleyService volleyService;
    private TextInputLayout emailET, passwordET;
    private SharedPreferences archivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVolleyCallback();
        volleyService = new VolleyService(resultCallback, this);
        setContentView(R.layout.activity_log_in);
        getEncryptedSharedPreferences();
        Button loginbtn = findViewById(R.id.button);
        Button guestBtn = findViewById(R.id.button1);
        emailET = findViewById(R.id.editTextTextEmailAddress);
        passwordET = findViewById(R.id.editTextTextPassword);
        TextView restorePasswordTV = findViewById(R.id.textView3);
        TextView createAccountTV = findViewById(R.id.textView4);

        //Debug code
        Objects.requireNonNull(emailET.getEditText()).setText("a19100060@ceti.mx");
        Objects.requireNonNull(passwordET.getEditText()).setText("Hh1a1.");

        guestBtn.setOnClickListener(view -> onClickGuest());
        loginbtn.setOnClickListener(view -> onClickLogIn());
        createAccountTV.setOnClickListener(view -> onClickRegister());
        restorePasswordTV.setOnClickListener(view -> onClickReset());
    }

    private void getEncryptedSharedPreferences() {
        String masterKeyAlias;
        archivo = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            archivo = EncryptedSharedPreferences.create(
                    "archivo",
                    masterKeyAlias,
                    LogInActivity.this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private void onClickReset() {
        Intent reset = new Intent(LogInActivity.this, ResetPasswordActivity.class);
        startActivity(reset);
    }

    private void onClickGuest() {
        logIn("0");
    }

    private void onClickRegister() {
        Intent register = new Intent(LogInActivity.this, RegisterActivity.class);
        startActivity(register);
    }

    //agregar el timeout
    private void onClickLogIn() {
        Email email;
        Password password;
        email = new Email(Objects.requireNonNull(Objects.requireNonNull(emailET.getEditText())).getText().toString());
        password = new Password(Objects.requireNonNull(Objects.requireNonNull(passwordET.getEditText())).getText().toString());
        if (email.isValid()) {
            if (password.isValid()) {
                SharedPreferences.Editor editor = archivo.edit();
                editor.putString("email",email.getEmail());
                editor.apply();
                User user = new User();
                user.setEmail(email);
                user.plainTextPassword = password.getPassword();
                String url = "/LogIn?email=" + user.getEmail().getEmail() + "&password=" + user.plainTextPassword;
                volleyService.getStringDataVolley(url);
            }else{
                passwordET.setError(getString(R.string.contrase_a_invalida));
            }
        } else {
            emailET.setError(getString(R.string.email_incorrecto));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (archivo.contains("idUsuario")) {
            launchHomeActivity();
        }
    }

    private void launchHomeActivity() {
        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, Object response) {
                Toast.makeText(LogInActivity.this, getString(R.string.exito), Toast.LENGTH_LONG).show();
                Log.d("notifySuccess", "Volley requester " + requestType);
                Log.d("notifySuccess", "Volley JSON post" + response);
                logIn(response.toString());
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                error.printStackTrace();
                String body = "";
                String errorCode = "";
                try {
                    errorCode = "" + error.networkResponse.statusCode;
                    body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String cause = "";
                if (error.getCause() != null) {
                    cause = error.getCause().getMessage();
                }
                Toast.makeText(LogInActivity.this, getString(R.string.fallido) + cause + " " + body + " ", Toast.LENGTH_LONG).show();
                Log.d("notifyError", "Volley requester " + requestType);
                Log.d("notifyError", "Volley JSON post" + "That didn't work!" + error + " " + errorCode);
                Log.d("notifyError", "Error: " + error
                        + "\nStatus Code " + errorCode
                        + "\nResponse Data " + body
                        + "\nCause " + error.getCause()
                        + "\nmessage " + error.getMessage());
            }
        };
    }

    private void logIn(String response) {
        SharedPreferences.Editor editor = archivo.edit();
        editor.putString("idUsuario", response);
        editor.apply();
        launchHomeActivity();
    }
}