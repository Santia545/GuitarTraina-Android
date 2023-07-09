package com.example.guitartraina.activities.ranking;

import static android.media.AudioFormat.CHANNEL_IN_MONO;
import static android.media.AudioFormat.ENCODING_PCM_16BIT;

import android.media.AudioRecord;

import java.util.HashMap;
import java.util.Map;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.pitch.FastYin;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchDetector;

public class GuitarStringsProcessor implements AudioProcessor {
    Map<String, Long> stringPluckTimes = new HashMap<>();
    Float previousAmplitude = 0.0f;
    Float amplitudeThreshold = 0.2f;
    PitchDetector detector;

    public GuitarStringsProcessor(){
        int SAMPLE_RATE = 44100;
        int RECORD_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_IN_MONO, ENCODING_PCM_16BIT);
        detector= new FastYin(SAMPLE_RATE, RECORD_BUFFER_SIZE);
    }
    @Override
    public boolean process(AudioEvent audioEvent) {
        float[] audioBuffer = audioEvent.getFloatBuffer();
        // Calculate the current maximum amplitude of the audio buffer
        float currentAmplitude = calculateMaxAmplitude(audioBuffer);

        if (isAmplitudeRising(previousAmplitude, currentAmplitude) && currentAmplitude > amplitudeThreshold) {
            // Pluck event detected
            float[] audioFloatBuffer = audioEvent.getFloatBuffer();
            PitchDetectionResult result = detector.getPitch(audioFloatBuffer);
            stringPluckTimes.put("any", System.currentTimeMillis());
        }

        previousAmplitude = currentAmplitude;

        return true;
    }

    @Override
    public void processingFinished() {

    }
    private float calculateMaxAmplitude(float[] audioBuffer) {
        float maxAmplitude = 0.0f;

        for (float sample : audioBuffer) {
            float amplitude = Math.abs(sample);
            if (amplitude > maxAmplitude) {
                maxAmplitude = amplitude;
            }
        }

        return maxAmplitude;
    }
    private boolean isAmplitudeRising(float previousAmplitude, float currentAmplitude) {
        float amplitudeDifference = currentAmplitude - previousAmplitude;
        return amplitudeDifference > 0;
    }


}
