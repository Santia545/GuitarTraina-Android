package com.example.guitartraina.activities.group_session;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.guitartraina.R;
import com.example.guitartraina.databinding.ActivityHostBinding;

public class HostActivity extends AppCompatActivity {
    private String lastSelectedMediaPath;
    private Uri lastSelectedMediaUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHostBinding binding = ActivityHostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnPickMedia.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/mpeg");
            launchFileSelect.launch(intent);
        });

        binding.btnStartHosting.setOnClickListener(v -> {
            if(lastSelectedMediaPath != null) {
                Intent intent = new Intent(HostActivity.this, PreSessionActivity.class);
                intent.putExtra("path", lastSelectedMediaPath);
                intent.putExtra("uri",lastSelectedMediaUri);
                intent.putExtra("user",0);
                startActivity(intent);
            } else {
                Toast.makeText(HostActivity.this, getText(R.string.pick_file), Toast.LENGTH_SHORT).show();
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