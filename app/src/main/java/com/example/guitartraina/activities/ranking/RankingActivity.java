package com.example.guitartraina.activities.ranking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.guitartraina.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RankingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        FloatingActionButton btnCreateChallenge = findViewById(R.id.fab);
        btnCreateChallenge.setOnClickListener(view -> {
            Intent recordActivity =new Intent(RankingActivity.this,RecorderActivity.class);
            startActivity(recordActivity);
        });
    }
}