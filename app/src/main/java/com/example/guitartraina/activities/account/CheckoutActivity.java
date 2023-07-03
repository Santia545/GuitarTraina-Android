package com.example.guitartraina.activities.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.android.volley.VolleyError;
import com.example.guitartraina.R;
import com.example.guitartraina.api.IResult;
import com.example.guitartraina.api.VolleyService;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.guitartraina.databinding.ActivityCheckoutBinding;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;

public class CheckoutActivity extends AppCompatActivity {

    // Arbitrarily-picked constant integer you define to track a request for payment data activity.
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;

    private ActivityCheckoutBinding layoutBinding;
    private View googlePayButton;
    private PaymentsClient paymentsClient;
    private IResult resultCallback = null;
    private VolleyService volleyService;
    private SharedPreferences archivo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUi();

        paymentsClient = PaymentsUtilities.createPaymentsClient(getApplication());
        fetchCanUseGooglePay();

        initVolleyCallback();
        volleyService = new VolleyService(resultCallback, this);
        getEncryptedSharedPreferences();
    }

    private void fetchCanUseGooglePay() {
        final JSONObject isReadyToPayJson = PaymentsUtilities.getIsReadyToPayRequest();
        if (isReadyToPayJson == null) {
            return;
        }

        // The call to isReadyToPay is asynchronous and returns a Task.
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString());
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(
                completedTask -> {
                    if (completedTask.isSuccessful()) {
                        googlePayButton.setVisibility(View.VISIBLE);
                    } else {
                        Log.w("isReadyToPay failed", completedTask.getException());
                        Toast.makeText(this, R.string.googlepay_status_unavailable, Toast.LENGTH_LONG).show();
                    }
                });
    }



    private void initializeUi() {
        layoutBinding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(layoutBinding.getRoot());

        // The Google Pay button is a layout file – take the root view
        googlePayButton = layoutBinding.googlePayButton.getRoot();
        googlePayButton.setOnClickListener(this::requestPayment);
    }

    public void requestPayment(View view) {
        // Disables the button to prevent multiple clicks.
        googlePayButton.setClickable(false);
        final Task<PaymentData> task = getLoadPaymentDataTask(1000);

        // Shows the payment sheet and forwards the result to the onActivityResult method.
        AutoResolveHelper.resolveTask(task, this, LOAD_PAYMENT_DATA_REQUEST_CODE);
    }
    public Task<PaymentData> getLoadPaymentDataTask(final long priceCents) {
        JSONObject paymentDataRequestJson = PaymentsUtilities.getPaymentDataRequest(priceCents);
        if (paymentDataRequestJson == null) {
            return null;
        }

        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.toString());
        return paymentsClient.loadPaymentData(request);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {
            switch (resultCode) {

                case AppCompatActivity.RESULT_OK:
                    PaymentData paymentData = PaymentData.getFromIntent(data);
                    handlePaymentSuccess(paymentData);
                    break;

                case AppCompatActivity.RESULT_CANCELED:
                    googlePayButton.setClickable(true);
                    break;

                case AutoResolveHelper.RESULT_ERROR:
                    Status status = AutoResolveHelper.getStatusFromIntent(data);
                    handleError(status);
                    googlePayButton.setClickable(true);
                    break;
            }
        }
    }

    private void handlePaymentSuccess(@Nullable PaymentData paymentData) {
        assert paymentData != null;
        final String paymentInfo = paymentData.toJson();

        try {
            JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".

            final JSONObject tokenizationData = paymentMethodData.getJSONObject("tokenizationData");
            final String token = tokenizationData.getString("token");
            final String email = archivo.getString("email", null);
            volleyService.putStringDataVolley("/Users?email="+email);
            Log.d("Google Pay token: ", token);
        } catch (JSONException e) {
            throw new RuntimeException("The selected garment cannot be parsed from the list of elements");
        }
    }

    private void handleError(@Nullable Status status) {
        String errorString = "Unknown error.";
        if (status != null) {
            int statusCode = status.getStatusCode();
            errorString = String.format(Locale.getDefault(), "Error code: %d", statusCode);
        }
        Log.e("loadPaymentData failed", errorString);
    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, Object response) {
                Toast.makeText(CheckoutActivity.this, getString(R.string.exito), Toast.LENGTH_LONG).show();
                Log.d("notifySuccess", "Volley requester " + requestType);
                Log.d("notifySuccess", "Volley JSON post" + response);
                changeUserType();
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
                Toast.makeText(CheckoutActivity.this, getString(R.string.fallido) + cause + " " + body + " ", Toast.LENGTH_LONG).show();
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

    private void changeUserType() {
        SharedPreferences.Editor editor = archivo.edit();
        editor.putString("idUsuario", "Pago");
        editor.apply();
    }

    private void getEncryptedSharedPreferences() {
        String masterKeyAlias;
        archivo = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            archivo = EncryptedSharedPreferences.create(
                    "archivo",
                    masterKeyAlias,
                    CheckoutActivity.this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}