package com.example.guitartraina.activities.group_session.share_audio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.guitartraina.R;
import com.example.guitartraina.activities.group_session.Host;
import com.example.guitartraina.activities.group_session.NsdHost;
import com.example.guitartraina.databinding.ActivityPreAudioSessionBinding;
import com.example.guitartraina.ui.views.adapter.ClientListAdapter;
import com.example.guitartraina.activities.group_session.share_audio.sync_utilities.FileReceiver;
import com.example.guitartraina.activities.group_session.share_audio.sync_utilities.FileSender;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.GeneralSecurityException;

public class PreAudioSessionActivity extends AppCompatActivity {

    public ActivityPreAudioSessionBinding binding;
    private int userType;
    private NsdHost nsdHost;
    private FileSender fs;
    private Host host;

    private Uri uri;
    public ClientListAdapter adapter;
    private SharedPreferences archivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreAudioSessionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getEncryptedSharedPreferences(this);
        binding.tvDownloadingMediaText.setVisibility(View.GONE);
        binding.pbDownloadProgress.setVisibility(View.GONE);
        binding.tvDownloadProgress.setVisibility(View.GONE);

        Intent intent = getIntent();
        userType = intent.getIntExtra("user", 0);
        if(userType == 0){
            binding.btnStart.setEnabled(false);
            binding.tvHost.setText(getString(R.string.host_indicator, archivo.getString("email", getString(R.string.guest))));
            String path = getIntent().getStringExtra("path");
            uri = getIntent().getParcelableExtra("uri");
            nsdHost = new NsdHost(this);
            nsdHost.registerService();

            fs = new FileSender(path, 3078, this);

        }else{
            host = intent.getParcelableExtra("host");
            binding.tvDownloadingMediaText.setVisibility(View.VISIBLE);
            binding.pbDownloadProgress.setVisibility(View.VISIBLE);
            binding.tvDownloadProgress.setVisibility(View.VISIBLE);

            binding.btnStart.setVisibility(View.GONE);
            binding.tvHost.setText(getString(R.string.host_indicator, host.getHostName()));
            FileReceiver fr = new FileReceiver(host.getHostAddress(), 3078, PreAudioSessionActivity.this);
        }
        setUpClientsRv();
    }

    private void setUpClientsRv() {
        adapter = new ClientListAdapter(this);
        binding.clientsRv.setHasFixedSize(true);
        binding.clientsRv.setLayoutManager(new LinearLayoutManager(this));
        binding.clientsRv.setAdapter(adapter);
    }

    public void onFailure(String msg) {
        runOnUiThread(() -> {
            Toast.makeText(PreAudioSessionActivity.this, msg, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PreAudioSessionActivity.this, AudioClientActivity.class);
            startActivity(intent);
        });
    }

    public void onFinishDownload() {
        adapter.addClient(archivo.getString("email", getString(R.string.guest)));
    }


    public void setProgress(int progessInPercent, long downloaded, long totalSize) {
        runOnUiThread(() -> {
            binding.pbDownloadProgress.setProgress(progessInPercent);
            binding.tvDownloadProgress.setText("" + downloaded + "MB/" + totalSize + "MB");
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(userType == 0){
            nsdHost.unRegisterService();
            fs.harakiri();
            fs = null;
        }
    }

    public void goToPlayer(Uri uri) {
        Intent startPlayer = new Intent(getApplicationContext(), PlayerActivity.class);
        startPlayer.putExtra("uri", uri);
        startPlayer.putExtra("user", userType);
        if(userType==1){
            startPlayer.putExtra("host", host);
        }
        startActivity(startPlayer);
    }

    public void sendPlayerSignal(DataOutputStream dos, DataInputStream dis, Socket sock) {
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
                goToPlayer(uri);
            });
        });
    }
}
