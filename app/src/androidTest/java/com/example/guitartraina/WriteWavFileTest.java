package com.example.guitartraina;

import static android.media.AudioFormat.CHANNEL_IN_MONO;
import static android.media.AudioFormat.ENCODING_PCM_16BIT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.Context;
import android.media.AudioRecord;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.GainProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.android.AndroidAudioPlayer;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.writer.WriterProcessor;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class WriteWavFileTest {
    Context appContext;
    private final int SAMPLE_RATE=44100;
    private final int RECORD_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_IN_MONO, ENCODING_PCM_16BIT);
    private final int RECORD_BUFFER_OVERLAP = RECORD_BUFFER_SIZE / 2;
    TarsosDSPAudioFormat audioFormat = new TarsosDSPAudioFormat(44100, 16, 1, true, false);

    @Test
    public void testWriteWavFile() {
        Looper.prepare();
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        recordAudio();
        long finaltime = System.currentTimeMillis()+5000;
        long time;
        do {
             time = System.currentTimeMillis();
        }while(time<finaltime);
        Toast.makeText(appContext, "Fin", Toast.LENGTH_SHORT).show();
    }
    private void recordAudio() {
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(44100, RECORD_BUFFER_SIZE, RECORD_BUFFER_OVERLAP);
        File file = new File(appContext.getExternalFilesDir(null), "audioTest.wav");
        if (file.exists()) {
            file.delete(); // Delete the file if it already exists
        }
        RandomAccessFile randomAccessFile = null;
        try {
            file.createNewFile(); // Create a new file
            randomAccessFile = new RandomAccessFile(file, "rw");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        AndroidAudioPlayer androidAudioPlayer = new AndroidAudioPlayer(audioFormat);
        WriterProcessor writerProcessor = new WriterProcessor(audioFormat, randomAccessFile);
        /*dispatcher.addAudioProcessor(new AudioProcessor() {
            @Override
            public boolean process(AudioEvent audioEvent) {

                return true;
            }

            @Override
            public void processingFinished() {

            }
        });*/
        dispatcher.addAudioProcessor(androidAudioPlayer);
        Thread recorderThread = new Thread(dispatcher, "Audio Dispatcher");
        recorderThread.setPriority(Thread.MAX_PRIORITY);
        recorderThread.start();
    }
}