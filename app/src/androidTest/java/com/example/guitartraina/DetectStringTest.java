package com.example.guitartraina;

import static android.media.AudioFormat.CHANNEL_IN_MONO;
import static android.media.AudioFormat.CHANNEL_OUT_MONO;
import static android.media.AudioFormat.ENCODING_PCM_16BIT;

import android.content.Context;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Looper;
import android.widget.Toast;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.guitartraina.activities.ranking.GuitarStringsProcessor;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.GainProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.UniversalAudioInputStream;
import be.tarsos.dsp.io.android.AndroidAudioPlayer;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.writer.WriterProcessor;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DetectStringTest {
    Context appContext;
    AudioDispatcher dispatcher;
    private final int SAMPLE_RATE=44100;
    private final int RECORD_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_IN_MONO, ENCODING_PCM_16BIT);

    @Test
    public void testWriteWavFile() {
        Looper.prepare();
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Toast.makeText(appContext,"running",Toast.LENGTH_SHORT).show();
        recordAudio();
        long finaltime = System.currentTimeMillis()+1000000;
        long time;
        do {
             time = System.currentTimeMillis();
        }while(time<finaltime);
        dispatcher.stop();

    }
    private void recordAudio() {
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, RECORD_BUFFER_SIZE, 0);
        GuitarStringsProcessor guitarStringsProcessor = new GuitarStringsProcessor();
        AudioProcessor gainProcessor = new GainProcessor(5.0);
        dispatcher.addAudioProcessor(gainProcessor);
        dispatcher.addAudioProcessor(guitarStringsProcessor);
        Thread recorderThread = new Thread(dispatcher, "Audio Dispatcher");
        recorderThread.setPriority(Thread.MAX_PRIORITY);
        recorderThread.start();
    }

}