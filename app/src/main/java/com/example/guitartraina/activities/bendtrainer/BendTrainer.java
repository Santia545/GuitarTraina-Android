package com.example.guitartraina.activities.bendtrainer;

import static android.media.AudioFormat.CHANNEL_IN_MONO;
import static android.media.AudioFormat.ENCODING_PCM_16BIT;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.AudioRecord;
import android.util.Log;

import androidx.preference.PreferenceManager;


import com.example.guitartraina.R;
import com.example.guitartraina.ui.views.FrequencyView;


import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.GainProcessor;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchProcessor;

public class BendTrainer {

    private final Activity activity;
    private Thread pitchDetectorThread = null;
    private AudioDispatcher dispatcher = null;
    private final int SAMPLE_RATE = 44100;
    private final int RECORD_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_IN_MONO, ENCODING_PCM_16BIT);
    private final int RECORD_BUFFER_OVERLAP = RECORD_BUFFER_SIZE / 2;

    public BendTrainer(Activity activity) {
        this.activity = activity;
    }

    public void run() {
        double gain = getGainFromPreferences();
        int sensibility = getSensibilityFromPreferences();
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, RECORD_BUFFER_SIZE, RECORD_BUFFER_OVERLAP);
        PitchDetectionHandler pdh = (result, e) -> {
            FrequencyView frequencyView = activity.findViewById(R.id.frequencyView);
            final float pitchInHz = result.getPitch();
            //Log.d("Values", "Gain: "+gain+" Sens: "+sensibility);
            //Log.d("Pitch", pitchInHz +"probability: "+result.getProbability()+" loudness: "+e.getdBSPL());
            if (pitchInHz == -1 || result.getProbability() < 0.90f ) {
                return;
            }
            //Log.d("Pitch", pitchInHz + "probability: " + result.getProbability() + " loudness: " + e.getdBSPL());

        };
        AudioProcessor gainProcessor = new GainProcessor(gain);
        AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, SAMPLE_RATE, RECORD_BUFFER_SIZE, pdh);
        double threshold = SilenceDetector.DEFAULT_SILENCE_THRESHOLD;
        SilenceDetector silenceDetector = new SilenceDetector(threshold,false);
        AudioProcessor silenceProcessor = new SilenceProcessor(silenceDetector, threshold);
        dispatcher.addAudioProcessor(gainProcessor);
        dispatcher.addAudioProcessor(silenceDetector);
        dispatcher.addAudioProcessor(silenceProcessor);
        dispatcher.addAudioProcessor(pitchProcessor);
        pitchDetectorThread = new Thread(dispatcher, "Audio Dispatcher");
        pitchDetectorThread.setPriority(Thread.MAX_PRIORITY);
        pitchDetectorThread.start();
    }

    private int getSensibilityFromPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        int microphoneSensibility = sharedPreferences.getInt("tuner_sensibility", -100);
        return -microphoneSensibility;
    }

    private double getGainFromPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        int microphoneGain = sharedPreferences.getInt("microphone_gain", 10);
        return microphoneGain / 10.d;
    }


    public void stop() {
        if (dispatcher != null && !dispatcher.isStopped()) {
            dispatcher.stop();
            dispatcher = null;
        }
        if (pitchDetectorThread != null) {
            try {
                pitchDetectorThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            pitchDetectorThread = null;
        }
    }

    private double getCentsOff(float pitchInHz, double expectedFrequency) {
        //Math.log(2.0) = 0.6931471805599453;
        //12*100
        return 1200 * Math.log(pitchInHz / expectedFrequency) / 0.6931471805599453;
    }

}
