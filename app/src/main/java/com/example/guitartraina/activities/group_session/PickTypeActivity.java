package com.example.guitartraina.activities.group_session;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.guitartraina.R;
import com.example.guitartraina.activities.group_session.share_audio.AudioClientActivity;
import com.example.guitartraina.activities.group_session.share_metronome.MetronomeClientActivity;
import com.example.guitartraina.databinding.ActivityPickTypeBinding;

public class PickTypeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPickTypeBinding binding = ActivityPickTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.shareAudio.setOnClickListener(v -> startActivity(new Intent(this, AudioClientActivity.class)));

        binding.shareMetronome.setOnClickListener(v -> startActivity(new Intent(this, MetronomeClientActivity.class)));
    }
}