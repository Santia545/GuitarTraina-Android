package com.example.guitartraina.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.guitartraina.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.InputStream;

import java.util.Locale;
import java.util.Random;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.UniversalAudioInputStream;
import be.tarsos.dsp.io.android.AndroidAudioPlayer;


public class EarTrainerActivity extends AppCompatActivity {
    private final String[] tilesNotes = new String[]{"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    private final FloatingActionButton[] tile = new FloatingActionButton[12];
    private final Button[] options = new Button[4];
    private Button repeatSound;
    private View.OnClickListener right;
    private View.OnClickListener wrong;
    private TextView TVprogress;
    private int counter = 1;
    private int rightAnswers = 0;
    private int wrongAnswers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ear_trainer);
        repeatSound = findViewById(R.id.repeat);
        TVprogress = findViewById(R.id.progressTV);
        TVprogress.setText(String.format(Locale.getDefault(), "Question: %d/15", counter));
        tile[0] = findViewById(R.id.tile1);
        tile[1] = findViewById(R.id.tile2);
        tile[2] = findViewById(R.id.tile3);
        tile[3] = findViewById(R.id.tile4);
        tile[4] = findViewById(R.id.tile5);
        tile[5] = findViewById(R.id.tile6);
        tile[6] = findViewById(R.id.tile7);
        tile[7] = findViewById(R.id.tile8);
        tile[8] = findViewById(R.id.tile9);
        tile[9] = findViewById(R.id.tile10);
        tile[10] = findViewById(R.id.tile11);
        tile[11] = findViewById(R.id.tile12);
        options[0] = findViewById(R.id.opc1);
        options[1] = findViewById(R.id.opc2);
        options[2] = findViewById(R.id.opc3);
        options[3] = findViewById(R.id.opc4);
        for (int i = 0; i < 12; i++) {
            int finalI = i;
            tile[i].setOnClickListener(view -> playSound(finalI + 1));
        }
        right = view -> {
            counter++;
            rightAnswers++;
            nextQuestionDialog(true);
        };
        wrong = view -> {
            counter++;
            wrongAnswers++;
            nextQuestionDialog(false);
        };

    }

    private void nextQuestionDialog(boolean answer) {
        dialogBuilder2(answer).show();
    }

    private AlertDialog dialogBuilder2(boolean answer) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setPositiveButton("Siguiente", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    if (counter < 16) {
                        nextQuestion();
                    } else {
                        endDialog();
                    }
                })
                .create();
        if (answer) {
            dialog.setTitle("Respuesta Correcta");
        } else {
            dialog.setTitle("Respuesta Incorrecta");
        }
        return dialog;
    }

    private void endDialog() {
        dialogBuilder().show();
    }

    private AlertDialog dialogBuilder() {

        return new AlertDialog.Builder(this)
                .setTitle("Sesion Finalizada!")
                .setMessage("Puntacion: " + String.format(Locale.getDefault(), "%.2f", ((double) rightAnswers / 15.) * 100) + "\nAciertos: " + rightAnswers + "\nFallos:" + wrongAnswers)
                .setPositiveButton("Nueva Sesion", (dialogInterface, i) -> {
                    saveProgress();
                    recreate();
                    dialogInterface.dismiss();
                })
                .setNegativeButton("Salir", (dialog1, which) -> dialog1.cancel())
                .setOnCancelListener(dialogInterface ->
                        finish()
                )
                .create();
    }

    private void saveProgress() {
    }

    private void nextQuestion() {
        TVprogress.setText(String.format(Locale.getDefault(), "Pregunta: %d/15", counter));
        genQuestion();
    }

    private void playSound(int tileIndex) {
        int BIT_DEPTH = 16;
        boolean BIG_ENDIAN = false;
        int SAMPLE_RATE = 44100;
        int BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

        TarsosDSPAudioFormat audioFormat = new TarsosDSPAudioFormat(TarsosDSPAudioFormat.Encoding.PCM_SIGNED, SAMPLE_RATE, BIT_DEPTH, 1, 2, 1, BIG_ENDIAN);
        InputStream wavStream;
        wavStream = getResources().openRawResource(getResources().getIdentifier(EarTrainerActivity.this.getPackageName() + ":raw/tile" + tileIndex, null, null));
        UniversalAudioInputStream audioStream = new UniversalAudioInputStream(wavStream, audioFormat);
        AudioDispatcher dispatcher = new AudioDispatcher(audioStream, BUFFER_SIZE, BUFFER_SIZE / 2);

        AndroidAudioPlayer player = new AndroidAudioPlayer(audioFormat);
        dispatcher.addAudioProcessor(player);
        dispatcher.skip(0.1);
        Thread audioDispatcher = new Thread(dispatcher, "Audio Dispatcher");
        audioDispatcher.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        genQuestion();
    }

    private void genQuestion() {
        int counter = 1;
        int[] randomNumbersArray = new int[4];
        int count = 0;
        while (count < 4) {
            long seed = System.currentTimeMillis();
            Random random = new Random(seed);
            int randomNumber = random.nextInt(12);

            // Check if the generated random number is already in the randomNumbersArray
            boolean isDuplicate = false;
            for (int i = 0; i < count; i++) {
                if (randomNumbersArray[i] == randomNumber) {
                    isDuplicate = true;
                    break;
                }
            }

            // If the number is unique, add it to the array
            if (!isDuplicate) {
                randomNumbersArray[count] = randomNumber;
                count++;
            }
        }

        long seed = System.currentTimeMillis();
        Random random = new Random(seed);


        playSound(randomNumbersArray[0] + 1);
        int randomOption = random.nextInt(4);
        for (int i = 0; i < 4; i++) {
            if (i == randomOption) {
                options[i].setOnClickListener(right);
                options[i].setText(String.format("%s", tilesNotes[randomNumbersArray[0]]));
            } else {
                options[i].setOnClickListener(wrong);
                options[i].setText(String.format("%s", tilesNotes[randomNumbersArray[counter]]));
                counter++;
            }
        }
        repeatSound.setOnClickListener(view -> playSound(randomNumbersArray[0] + 1));
    }
}