package com.example.guitartraina.activities.group_session.share_metronome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.guitartraina.R;
import com.example.guitartraina.activities.group_session.Host;
import com.example.guitartraina.activities.group_session.NsdHost;
import com.example.guitartraina.activities.group_session.share_metronome.sync_utilities.PreMetroClient;
import com.example.guitartraina.activities.group_session.share_metronome.sync_utilities.PreMetroHost;
import com.example.guitartraina.databinding.ActivityMetronomeHostBinding;
import com.example.guitartraina.ui.views.adapter.ClientListAdapter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.GeneralSecurityException;

public class MetronomeHostActivity extends AppCompatActivity {
    private NsdHost nsdHost;
    private ActivityMetronomeHostBinding binding;
    private SharedPreferences archivo;
    public ClientListAdapter adapter;
    private int userType;
    private Host host;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMetronomeHostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        userType = intent.getIntExtra("user", 0);
        if(userType == 0){
            getEncryptedSharedPreferences(this);
            binding.btnStart.setEnabled(false);
            binding.tvHost.setText(getString(R.string.host_indicator, archivo.getString("email", getString(R.string.guest))));
            nsdHost = new NsdHost(this);
            nsdHost.registerService();
            PreMetroHost prh = new PreMetroHost(3078, this);
        }else{
            host = intent.getParcelableExtra("host");
            PreMetroClient prc = new PreMetroClient(host.getHostAddress(),3078, this);
            binding.btnStart.setVisibility(View.GONE);
            binding.tvHost.setText(getString(R.string.host_indicator, host.getHostName()));
        }
        setUpClientsRv();

    }

    private void setUpClientsRv() {
        adapter = new ClientListAdapter(this);
        binding.clientsRv.setHasFixedSize(true);
        binding.clientsRv.setLayoutManager(new LinearLayoutManager(this));
        binding.clientsRv.setAdapter(adapter);
    }
    private void getEncryptedSharedPreferences(Context context) {
        String masterKeyAlias;
        archivo = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            archivo = EncryptedSharedPreferences.create("archivo", masterKeyAlias, context, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
    public void onFailure(String msg) {
        runOnUiThread(() -> {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MetronomeClientActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nsdHost.unRegisterService();
    }

    public void sendStartMetronomeSignal(DataOutputStream dos, DataInputStream dis, Socket sock) {
        runOnUiThread(() -> {
            binding.btnStart.setEnabled(true);
            binding.btnStart.setOnClickListener(v ->{
                String playerSignal = "PLAYER_START";
                new Thread(() -> {
                    try {
                        dos.writeUTF(playerSignal);
                        dos.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } finally {
                        try {
                            dis.close();
                            dos.close();
                            sock.close();
                        } catch (IOException e) {
                            Log.e("FILE_SENDER_HANDLER", e.toString());
                        }
                    }
                }).start();
                goToMetronome();
            });
        });
    }

    public void goToMetronome() {
        Intent metronome = new Intent(this, SharedMetronomeActivity.class);
        metronome.putExtra("user", userType);
        if(userType == 1){
            metronome.putExtra("host", host);
        }
        startActivity(metronome);
    }
}