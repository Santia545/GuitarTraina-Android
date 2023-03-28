package com.example.guitartraina.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.guitartraina.R;
import com.example.guitartraina.activities.account.LogInActivity;
import com.example.guitartraina.services.PostureNotificationService;


public class SplashScreen extends AppCompatActivity {
    Handler handler;
    Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Intent intent = new Intent(this, PostureNotificationService.class);
        startService(intent);

        /*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPreferences.getString("language_default", "Not FOUND");
        Resources res =this.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        if(!language.equals("Español")){
            conf.setLocale(new Locale("en".toLowerCase()));
        }
        res.updateConfiguration(conf, dm);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler= new Handler();
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
