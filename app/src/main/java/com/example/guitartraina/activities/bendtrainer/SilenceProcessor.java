package com.example.guitartraina.activities.bendtrainer;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.SilenceDetector;

public class SilenceProcessor implements AudioProcessor {
    private final SilenceDetector silenceDetector;
    private final double threshold;
    public SilenceProcessor(SilenceDetector silenceDetector, double threshold) {
        this.silenceDetector=silenceDetector;
        this.threshold=threshold;
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        System.out.println(silenceDetector.currentSPL());
        if(silenceDetector.currentSPL()>threshold){
            System.out.println("Sound detected at:" + System.currentTimeMillis() + ", " + (int) (silenceDetector.currentSPL()) + "dB SPL\n");
        }
        return false;
    }

    @Override
    public void processingFinished() {

    }
}
