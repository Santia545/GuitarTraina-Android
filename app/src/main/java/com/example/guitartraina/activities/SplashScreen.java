package com.example.guitartraina.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.guitartraina.R;
import com.example.guitartraina.activities.account.LogInActivity;
import com.example.guitartraina.services.PracticeNotificationService;


public class SplashScreen extends AppCompatActivity {
    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if (arePracticeNotificationsEnabled() && !isMyServiceRunning(PracticeNotificationService.class)) {
            Intent intent = new Intent(this, PracticeNotificationService.class);
            startService(intent);
        }
        if (arePostureNotificationsEnabled()) {

        }
    }

    private boolean arePostureNotificationsEnabled() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean("posture_notifications", false);
    }

    private boolean arePracticeNotificationsEnabled() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean("practice_notifications", false);
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler = new Handler();
        runnable = () -> {
            Intent logInActivity = new Intent(SplashScreen.this, LogInActivity.class);
            startActivity(logInActivity);
            finish();
        };
        handler.postDelayed(runnable, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
