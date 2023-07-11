package com.example.guitartraina.activities.group_session.share_audio;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.guitartraina.R;
import com.example.guitartraina.databinding.ActivityAudioHostBinding;

public class AudioHostActivity extends AppCompatActivity {
    private String lastSelectedMediaPath;
    private Uri lastSelectedMediaUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAudioHostBinding binding = ActivityAudioHostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnPickMedia.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/mpeg");
            launchFileSelect.launch(intent);
        });

        binding.btnStartHosting.setOnClickListener(v -> {
            if(lastSelectedMediaPath != null) {
                Intent intent = new Intent(AudioHostActivity.this, PreAudioSessionActivity.class);
                intent.putExtra("path", lastSelectedMediaPath);
                intent.putExtra("uri",lastSelectedMediaUri);
                intent.putExtra("user",0);
                startActivity(intent);
            } else {
                Toast.makeText(AudioHostActivity.this, getText(R.string.pick_file), Toast.LENGTH_SHORT).show();
            }

        });
    }

    ActivityResultLauncher<Intent> launchFileSelect = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                assert result.getData() != null;
                lastSelectedMediaUri = result.getData().getData();
                lastSelectedMediaPath = lastSelectedMediaUri.toString();
                Toast.makeText(this, lastSelectedMediaPath, Toast.LENGTH_SHORT).show();
            }
        }
    );

}