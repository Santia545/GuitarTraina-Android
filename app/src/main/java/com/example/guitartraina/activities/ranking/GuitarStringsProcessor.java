package com.example.guitartraina.activities.ranking;

import static android.media.AudioFormat.CHANNEL_IN_MONO;
import static android.media.AudioFormat.ENCODING_PCM_16BIT;

import android.media.AudioRecord;
import android.util.Log;

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
    Float amplitudeThreshold = 0.05f;
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
            Log.d("TAG", "pluck");
            float[] audioFloatBuffer = audioEvent.getFloatBuffer();
            PitchDetectionResult result = detector.getPitch(audioFloatBuffer);
            Log.d("Pitch", result.getPitch() + "probability: " + result.getProbability() + " loudness: " + audioEvent.getdBSPL());
            if(result.getProbability() > 0.85f) {
                //the algorithm can detect the pitch with a 90% accuracy
                Note note= processPitch(result.getPitch());
                if(Math.abs(note.getCentsOff())<20.){
                    Log.d("Note", note.getName());
                    //the note is close enough to an actual note
                    stringPluckTimes.put(note.getName(), System.currentTimeMillis());
                }
            }
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
    public Note processPitch(float pitchInHz) {
        double A4 = 440.0;
        double C0 = A4 * 0.03716272234383503;
        //Math.pow(2.0, -4.75) = 0.03716272234383503
        //Notation of pitches
        String[] name = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        //get how many steps pitch is above C0
        double r = 12.0 * Math.log(pitchInHz / C0) / 0.6931471805599453;
        //Math.log(2.0) = 0.6931471805599453;
        //get how many full half steps pitch is above C0
        int h = (int)Math.round(r);
        //get how far the actual pitch its from the closest full halfstep
        double diff = r - h;
        //turn steps into cents
        double cents = 100 * diff;
        String display;

        //Get in which octave the pitch its
        int octave = (int) Math.floor(h / 12.0);
        //Get which of the 12 notes the pitch is
        int n = h % 12;
        display=name[n] + octave;
        return new Note(display, cents);
    }

    private static class Note {
        public String getName() {
            return name;
        }

        public double getCentsOff() {
            return centsOff;
        }

        public Note(String name, double centsOff) {
            this.name = name;
            this.centsOff = centsOff;
        }

        private final String name;
        private final double centsOff;

    }
}
