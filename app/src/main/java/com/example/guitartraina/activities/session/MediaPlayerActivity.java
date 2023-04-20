package com.example.guitartraina.activities.session;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.guitartraina.R;


public class MediaPlayerActivity extends AppCompatActivity {

    private SoundPool soundPool;
    private int soundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SoundPool
        int MAX_STREAMS = 1;
        soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(this, R.raw.string0, 1);

        // Start AsyncTask to listen for data on the TCP socket
        new SocketListener(new IData() {
            @Override
            public void notifyData(Object data) {
                Toast.makeText(MediaPlayerActivity.this, "Conectado" + data.toString(), Toast.LENGTH_SHORT).show();

            }
            @Override
            public void notifyError(String error) {
                Toast.makeText(MediaPlayerActivity.this, "Error" +error, Toast.LENGTH_SHORT).show();
            }
        }, "192.168.1.103").start();
    }

}