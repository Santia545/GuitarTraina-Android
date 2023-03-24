package com.example.guitartraina.activities.account;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.guitartraina.R;
import com.example.guitartraina.api.IResult;
import com.example.guitartraina.api.VolleyService;

import java.nio.charset.StandardCharsets;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText emailET, passwordET, confirmpasswordET, tokenET;
    private IResult resultCallback = null;
    private VolleyService volleyService;
    private String accountEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVolleyCallback();
        volleyService = new VolleyService(resultCallback, this);
        setContentView(R.layout.activity_reset_password);
        AlertDialog dialog = dialogBuilder();
        dialog.show();
        Button loginBtn = findViewById(R.id.button);
        emailET = findViewById(R.id.editTextEmailAddress);
        passwordET = findViewById(R.id.editTextPassword);
        confirmpasswordET = findViewById(R.id.editTextConfirmPassword);
        tokenET = findViewById(R.id.editTextToken);

        //debugTEST
        passwordET.setText("Hh1a1.");
        confirmpasswordET.setText("Hh1a1.");
        loginBtn.setOnClickListener(view -> onClickReset());
    }

    private AlertDialog dialogBuilder() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint("Email");
        AlertDialog dialog = new AlertDialog.Builder(ResetPasswordActivity.this)
                .setTitle(R.string.email_type_in)
                .setMessage(R.string.email_recovery_info)
                .setView(input)
                .setPositiveButton(R.string.send_code, (dialogInterface, i) -> {
                    if (new Email(input.getText().toString()).isValid()) {
                        accountEmail = input.getText().toString();
                        String url = "/Tokens?email="+accountEmail;
                        volleyService.postStringDataVolley(url);
                        emailET.setText(accountEmail);
                        emailET.setEnabled(false);
                        dialogInterface.dismiss();
                    }else{
                        Toast.makeText(ResetPasswordActivity.this,R.string.email_incorrecto,Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog1, which) -> dialog1.cancel())
                .setNeutralButton(R.string.existing_token, (dialogInterface, i) -> {

                })
                .setOnCancelListener(dialogInterface -> {
                    Toast.makeText(ResetPasswordActivity.this, R.string.cancelled, Toast.LENGTH_LONG).show();
                    finish();
                })
                .create();
        float dpi = this.getResources().getDisplayMetrics().density;

        dialog.setView(input, (int) (19 * dpi), (int) (5 * dpi), (int) (14 * dpi), (int) (5 * dpi));

        return dialog;
    }

    private void onClickReset() {
        Email email;
        String token;
        Password password;
        email = new Email(emailET.getText().toString());
        token = tokenET.getText().toString();
        password = new Password(passwordET.getText().toString());
        if (!email.isValid()) {
            Toast.makeText(ResetPasswordActivity.this, R.string.email_incorrecto, Toast.LENGTH_LONG).show();
        } else if (!password.isValid()) {
            Toast.makeText(ResetPasswordActivity.this, R.string.invalid_password_info, Toast.LENGTH_LONG).show();
        } else if (!password.equals(new Password(confirmpasswordET.getText().toString()))) {
            Toast.makeText(ResetPasswordActivity.this, R.string.passwords_match_error, Toast.LENGTH_LONG).show();
        } else if (token.length() != 6) {
            Toast.makeText(ResetPasswordActivity.this, R.string.token_input, Toast.LENGTH_LONG).show();
        } else {
            EncryptedPassword encryptedPassword = new EncryptedPassword(password);
            initVolleyCallback();
            String url = "/ChangePassword?email=" + email.getEmail() + "&token=" + token + "&password=" + encryptedPassword.getPassword();
            volleyService.putStringDataVolley(url);
        }
    }

    private void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, Object response) {
                Toast.makeText(ResetPasswordActivity.this, "Success: "+ response, Toast.LENGTH_LONG).show();
                Log.d("notifySuccess", "Volley requester " + requestType);
                Log.d("notifySuccess", "Volley JSON post" + response);
                if(requestType.equals("PUT")){
                finish();
                }

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                error.printStackTrace();
                String body = "";
                if (error.networkResponse.data != null) {
                    body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                }
                Toast.makeText(ResetPasswordActivity.this, "failed: " + body + " " + error.networkResponse.statusCode, Toast.LENGTH_LONG).show();
                Log.d("notifyError", "Volley requester " + requestType);
                Log.d("notifyError", "Volley JSON post" + "That didn't work!" + error+ " " + error.networkResponse.statusCode);
                Log.d("notifyError", "Error: " + error
                        + "\nStatus Code " + error.networkResponse.statusCode
                        + "\nResponse Data " + body
                        + "\nCause " + error.getCause()
                        + "\nmessage " + error.getMessage());
            }
        };
    }
}