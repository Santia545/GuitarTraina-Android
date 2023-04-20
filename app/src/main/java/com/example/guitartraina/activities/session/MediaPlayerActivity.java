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
        new SocketListener(data -> {
            Toast.makeText(MediaPlayerActivity.this, "Data recieved" + data.toString(), Toast.LENGTH_SHORT).show();
            soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);
        }, "192.168.1.254").start();
    }

}