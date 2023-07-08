package com.example.guitartraina.activities.group_session;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.guitartraina.activities.group_session.sync_utilities.NsdClient;
import com.example.guitartraina.databinding.ActivityClientBinding;
import com.example.guitartraina.activities.group_session.adapter.HostListAdapter;

public class ClientActivity extends AppCompatActivity {

    private HostListAdapter adapter;
    private NsdClient nsdClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.guitartraina.databinding.ActivityClientBinding binding = ActivityClientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new HostListAdapter(this);

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
            startActivity(new Intent(this, HostActivity.class));
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        nsdClient.stopDiscovery();
    }
}