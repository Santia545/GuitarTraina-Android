package com.example.guitartraina.activities.group_session.share_audio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.guitartraina.activities.group_session.NsdClient;
import com.example.guitartraina.databinding.ActivityAudioClientBinding;
import com.example.guitartraina.ui.views.adapter.HostListAdapter;

public class AudioClientActivity extends AppCompatActivity {

    private HostListAdapter adapter;
    private NsdClient nsdClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAudioClientBinding binding = ActivityAudioClientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new HostListAdapter(this, PreAudioSessionActivity.class);

        binding.rvHostList.setLayoutManager(layoutManager);
        binding.rvHostList.setAdapter(adapter);

        nsdClient = new NsdClient(getApplicationContext(), adapter);
        nsdClient.discoverServices();

        binding.btnRefresh.setOnClickListener(v -> {
            nsdClient.stopDiscovery();
            adapter.clear();

            nsdClient = new NsdClient(getApplicationContext(), adapter);
            nsdClient.discoverServices();
        });

        binding.btnHost.setOnClickListener(v -> {
            startActivity(new Intent(this, AudioHostActivity.class));
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        nsdClient.stopDiscovery();
    }
}