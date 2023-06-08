package com.example.guitartraina.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Bundle;
import android.widget.Button;

import com.example.guitartraina.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.InputStream;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.UniversalAudioInputStream;
import be.tarsos.dsp.io.android.AndroidAudioPlayer;


public class EarTrainerActivity extends AppCompatActivity {
    FloatingActionButton[] tile= new FloatingActionButton[12];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ear_trainer);
        tile[0]=findViewById(R.id.tile1);
        tile[1]=findViewById(R.id.tile2);
        tile[2]=findViewById(R.id.tile3);
        tile[3]=findViewById(R.id.tile4);
        tile[4]=findViewById(R.id.tile5);
        tile[5]=findViewById(R.id.tile6);
        tile[6]=findViewById(R.id.tile7);
        tile[7]=findViewById(R.id.tile8);
        tile[8]=findViewById(R.id.tile9);
        tile[9]=findViewById(R.id.tile10);
        tile[10]=findViewById(R.id.tile11);
        tile[11]=findViewById(R.id.tile12);
        for (int i = 0; i < 12; i++) {
            int finalI = i;
            tile[i].setOnClickListener(view -> playSound(finalI+1));
        }
    }
    private void playSound(int tileIndex) {
        int BIT_DEPTH = 16;
        boolean BIG_ENDIAN = false;
        int SAMPLE_RATE = 44100;
        int BUFFER_SIZE=AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

        TarsosDSPAudioFormat audioFormat = new TarsosDSPAudioFormat(TarsosDSPAudioFormat.Encoding.PCM_SIGNED, SAMPLE_RATE, BIT_DEPTH, 1, 2, 1, BIG_ENDIAN);
        InputStream wavStream;
        wavStream = getResources().openRawResource(getResources().getIdentifier(EarTrainerActivity.this.getPackageName() + ":raw/tile"+String.valueOf(tileIndex) , null, null));
        UniversalAudioInputStream audioStream = new UniversalAudioInputStream(wavStream, audioFormat);
        AudioDispatcher dispatcher = new AudioDispatcher(audioStream, BUFFER_SIZE, BUFFER_SIZE/2);

        AndroidAudioPlayer player = new AndroidAudioPlayer(audioFormat);
        dispatcher.addAudioProcessor(player);
        dispatcher.skip(0.1);
        Thread audioDispatcher = new Thread(dispatcher, "Audio Dispatcher");
        audioDispatcher.start();
    }

}