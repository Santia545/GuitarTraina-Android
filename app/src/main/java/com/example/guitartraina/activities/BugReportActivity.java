package com.example.guitartraina.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.guitartraina.R;
import com.example.guitartraina.api.IResult;
import com.example.guitartraina.api.VolleyService;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Objects;

public class BugReportActivity extends AppCompatActivity {
    private IResult resultCallback = null;
    private VolleyService volleyService;
    private TextInputLayout errorTitle, errorNumber, errorSteps, errorDescription;
    private SharedPreferences archivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVolleyCallback();
        volleyService = new VolleyService(resultCallback, this);
        setContentView(R.layout.activity_bug_report);
        getEncryptedSharedPreferences();
        errorTitle = findViewById(R.id.editTextBugTitle);
        errorNumber = findViewById(R.id.editTextErrorNumber);
        errorSteps = findViewById(R.id.editTextBugSteps);
        errorDescription = findViewById(R.id.editTextBugDescription);

        Button sendBtn = findViewById(R.id.button1);
        sendBtn.setOnClickListener(view -> onClickSendReport());

    }

    private void onClickSendReport() {
        String title, steps, description;
        title = Objects.requireNonNull(errorTitle.getEditText()).getText().toString();
        int number=0;
        try {
             number = Integer.parseInt(Objects.requireNonNull(errorNumber.getEditText()).getText().toString());
        }catch(Exception ignored){
        }
        steps = Objects.requireNonNull(errorSteps.getEditText()).getText().toString();
        description = Objects.requireNonNull(errorDescription.getEditText()).getText().toString();
        if (title.length() > 4 ) {
            if (number > 0) {
                if (steps.length() > 5) {
                    if (description.length() > 30) {
                        String url = "/ErrorReport?title=" + title + "&number=" + number + "&steps=" + steps + "&description=" + description+"&user="+ archivo.getString("username","defaultuser");
                        volleyService.postStringDataVolley(url);
                    } else {
                        errorDescription.setError(getString(R.string.error_longitud_descripcion));
                    }
                } else {
                    errorSteps.setError(getString(R.string.error_longitud_pasos));
                }

            } else {
                errorNumber.setError(getString(R.string.error_numero));
            }
        } else {
            errorTitle.setError(getString(R.string.error_longitud_titulo));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLogIn();
    }
    public void checkLogIn(){
        if (archivo.contains("idUsuario")) {
            if (archivo.getString("idUsuario", "notlogged").equals("0")) {
                Toast.makeText(this, R.string.guest_user_prohibited, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


    private void getEncryptedSharedPreferences() {
        String masterKeyAlias;
        archivo = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            archivo = EncryptedSharedPreferences.create(
                    "archivo",
                    masterKeyAlias,
                    BugReportActivity.this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, Object response) {
                Toast.makeText(BugReportActivity.this, "Successfully reported, thanks for the submission", Toast.LENGTH_LONG).show();
                Log.d("notifySuccess", "Volley requester " + requestType);
                Log.d("notifySuccess", "Volley JSON post" + response);
                finish();
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
                Toast.makeText(BugReportActivity.this, "failed: " + cause + " " + body + " ", Toast.LENGTH_LONG).show();
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
}