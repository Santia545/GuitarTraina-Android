package com.example.guitartraina.activities.metronome;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.example.guitartraina.R;
import com.example.guitartraina.ui.views.MetronomeView;

import java.io.InputStream;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.UniversalAudioInputStream;
import be.tarsos.dsp.io.android.AndroidAudioPlayer;

public class Metronome {
    private final Activity activity;
    private int index = 0;
    private int bpm = 120;
    private int time = 500;
    private boolean running = false;
    private int notesNumber = 4;
    private int noteType = 4;
    private int noteAccent = 0;

    public Metronome(Activity activity) {
        this.activity = activity;
    }

    public boolean isRunning() {
        return running;
    }

    public void setNoteType(int noteType) {
        if (this.noteType != noteType) {
            this.noteType = noteType;
            calculateTime();
        }
    }

    public void setNotesNumber(int noteNumber) {
        this.notesNumber = noteNumber;
    }

    public void run() {
        running = true;
        MetronomeView metronomeView = activity.findViewById(R.id.metronomeView);
        Handler handler = new Handler();
        Runnable playSound = new Runnable() {
            @Override
            public void run() {
                long nextTime=SystemClock.uptimeMillis() + time;
                Log.d("Time", SystemClock.uptimeMillis()+"" );
                int noteNumber = index % notesNumber;
                metronomeView.setNoteIndex(noteNumber);
                if (noteNumber == noteAccent) {
                    playSound("forte");
                } else {
                    playSound("piano");
                }
                //Log.d("Time", SystemClock.uptimeMillis()+"" );
                index++;
                if (running) {
                    handler.postAtTime(this, nextTime);
                }
            }
        };
        handler.postAtTime(playSound, SystemClock.uptimeMillis()+time);

    }

    private void playSound(String soundType) {
        MediaPlayer mediaPlayer = MediaPlayer.create(
                activity,
                activity.getResources().getIdentifier(activity.getPackageName() + ":raw/metronome_" + soundType, null, null));
        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        mediaPlayer.start();
    }


    public void pause() {
        running = false;
    }

    public void setBpm(int bpm) throws IllegalArgumentException {
        if (bpm > 500 || bpm < 1) {
            throw new IllegalArgumentException("BPM out of bounds");
        }
        if (this.bpm != bpm) {
            this.bpm = bpm;
            calculateTime();
        }
    }

    private void calculateTime() {
        if (noteType != 4) {
            this.time = Math.round((60000.f / bpm) / (noteType / 4.f));
        } else {
            this.time = Math.round(60000.f / bpm);
        }

    }

    public void setNoteAccent(int noteAccent) {
        this.noteAccent = noteAccent;
    }
}
