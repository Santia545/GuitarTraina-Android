package com.example.guitartraina.activities.metronome;

import android.app.Activity;
import android.text.format.Time;
import android.util.Log;

import com.example.guitartraina.R;
import com.example.guitartraina.ui.views.MetronomeView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


public class Metronome {
    private final Activity activity;
    private int index = 0;
    private int bpm = 120;
    private int time = 500;
    private boolean running = false;
    private int notesNumber = 4;
    private int noteType = 4;
    Time initTime;

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
        Runnable playSound = () -> {
            try {
                metronomeView.setNoteIndex(index % notesNumber);
                Log.d("Time", System.currentTimeMillis() + " " + index);
                Thread.sleep(time);
                index++;
                if (running) {
                    new Thread(Thread.currentThread()).start();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        new Thread(playSound, "launch thread").start();
    }
    public void runFor60Seconds() {
        final CountDownLatch latch = new CountDownLatch(1);
        final MetronomeView metronomeView = activity.findViewById(R.id.metronomeView);
        final long startTime = System.currentTimeMillis();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                running = false;
                latch.countDown();
            }
        }, 60000);

        Runnable playSound = () -> {
            try {
                while (running) {
                    metronomeView.setNoteIndex(index % notesNumber);
                    Log.d("Time", System.currentTimeMillis() + " " + index);
                    Thread.sleep(time);
                    index++;
                }
                latch.countDown();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        new Thread(playSound, "launch thread").start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        timer.cancel();
        long elapsedTime = System.currentTimeMillis() - startTime;
        int increments = index;
        int incrementsPerMinute = (int) (increments / (elapsedTime / 1000.0) * 60);

        Log.d("Metronome", "Elapsed time: " + elapsedTime + " ms");
        Log.d("Metronome", "Total increments: " + increments);
        Log.d("Metronome", "Increments per minute: " + incrementsPerMinute);
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
}
