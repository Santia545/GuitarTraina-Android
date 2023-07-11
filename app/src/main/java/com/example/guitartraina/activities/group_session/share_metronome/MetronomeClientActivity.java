package com.example.guitartraina.activities.group_session.share_metronome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.guitartraina.R;
import com.example.guitartraina.activities.group_session.NsdClient;
import com.example.guitartraina.activities.group_session.share_audio.AudioClientActivity;
import com.example.guitartraina.activities.group_session.share_audio.AudioHostActivity;
import com.example.guitartraina.activities.group_session.share_audio.PreAudioSessionActivity;
import com.example.guitartraina.databinding.ActivityMetronomeBinding;
import com.example.guitartraina.databinding.ActivityMetronomeClientBinding;
import com.example.guitartraina.ui.views.adapter.HostListAdapter;

public class MetronomeClientActivity extends AppCompatActivity {
    private HostListAdapter adapter;
    private NsdClient nsdClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMetronomeClientBinding binding = ActivityMetronomeClientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new HostListAdapter(this, MetronomeHostActivity.class);

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
            startActivity(new Intent(this, MetronomeHostActivity.class));
            finish();
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        nsdClient.stopDiscovery();
    }
}