package com.example.guitartraina.services;

import static android.content.ContentValues.TAG;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class GoogleAdmobService extends Service {
    private final Handler handler = new Handler();
    private Runnable runnable;
    private SharedPreferences archivo;

    @Override
    public void onCreate() {
        super.onCreate();
        getEncryptedSharedPreferences();
        boolean proStatus = !getUserType().equals("Pago");
        // 10 minutes = 600000 ms

        if(proStatus){
            runnable = () -> {
                Log.d(TAG, "GoogleAdmobService: showing intersitial ad...");
                showAd();
                handler.postDelayed(runnable, 600000);
            };
            handler.postDelayed(runnable, 600000);
        }
    }

    private void showAd() {
        Toast.makeText(this, "worales", Toast.LENGTH_SHORT).show();
    }

    private String getUserType() {
        if (archivo.contains("idUsuario")) {
            return archivo.getString("idUsuario", "notlogged");
        }
        return "0";
    }

    private void getEncryptedSharedPreferences() {
        String masterKeyAlias;
        archivo = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            archivo = EncryptedSharedPreferences.create(
                    "archivo",
                    masterKeyAlias,
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}