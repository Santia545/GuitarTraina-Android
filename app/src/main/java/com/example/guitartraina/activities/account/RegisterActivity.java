package com.example.guitartraina.activities.account;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.guitartraina.R;
import com.example.guitartraina.api.IResult;
import com.example.guitartraina.api.VolleyService;

import java.nio.charset.StandardCharsets;


public class RegisterActivity extends AppCompatActivity {
    private EditText emailET, passwordET, confirmpasswordET, userNameET;
    private IResult resultCallback = null;
    private VolleyService volleyService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVolleyCallback();
        volleyService = new VolleyService(resultCallback, this);
        setContentView(R.layout.activity_register);
        Button loginBtn = findViewById(R.id.button);
        emailET = findViewById(R.id.editTextEmailAddress);
        passwordET = findViewById(R.id.editTextPassword);
        confirmpasswordET = findViewById(R.id.editTextConfirmPassword);
        userNameET = findViewById(R.id.editTextUserName);

        //debugTEST
        emailET.setText("a19100063@ceti.mx");
        passwordET.setText("Hh1a1.");
        confirmpasswordET.setText("Hh1a1.");
        userNameET.setText("wEEEESSS");
        loginBtn.setOnClickListener(view -> onClickRegister());
    }

    private void onClickRegister() {
        Email email;
        String userName;
        Password password;
        email = new Email(emailET.getText().toString());
        userName = userNameET.getText().toString();
        password = new Password(passwordET.getText().toString());
        if (!email.isValid()) {
            Toast.makeText(RegisterActivity.this, R.string.invalid_email_info, Toast.LENGTH_LONG).show();
        } else if (!password.isValid()) {
            Toast.makeText(RegisterActivity.this, R.string.invalid_password_info, Toast.LENGTH_LONG).show();
        } else if (!password.equals(new Password(confirmpasswordET.getText().toString()))) {
            Toast.makeText(RegisterActivity.this, R.string.passwords_match_error, Toast.LENGTH_LONG).show();
        } else if (userName.length() < 5) {
            Toast.makeText(RegisterActivity.this, R.string.username_length_error, Toast.LENGTH_LONG).show();
        } else {
            //Insertar en la BD quizá escapar las diagonales para evitar llamadas a la api
            User user = new User(email, userName, new EncryptedPassword(password), "Gratis");
            String url="/Users?email="+user.getEmail().getEmail()+"&username="+user.getUserName()+"&password="+user.getEncryptedPassword().getPassword();
            volleyService.postStringDataVolley(url);
        }
    }
    private void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, Object response) {
                Toast.makeText(RegisterActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                Log.d("notifySuccess", "Volley requester " + requestType);
                Log.d("notifySuccess", "Volley JSON post" + response);
                finish();
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                error.printStackTrace();
                String body="";
                if(error.networkResponse.data!=null) {
                    body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                }
                Toast.makeText(RegisterActivity.this, "failed: " + body +" "+error.networkResponse.statusCode, Toast.LENGTH_LONG).show();
                Log.d("notifyError", "Volley requester " + requestType);
                Log.d("notifyError", "Volley JSON post" + "That didn't work!" + error + " " + error.networkResponse.statusCode);
                Log.d("notifyError", "Error: " + error
                        + "\nStatus Code " + error.networkResponse.statusCode
                        + "\nResponse Data " + body
                        + "\nCause " + error.getCause()
                        + "\nmessage " + error.getMessage());
            }
        };
    }

}