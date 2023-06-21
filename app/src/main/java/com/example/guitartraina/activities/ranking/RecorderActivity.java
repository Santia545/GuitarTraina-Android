package com.example.guitartraina.activities.ranking;

import static android.media.AudioFormat.CHANNEL_IN_MONO;
import static android.media.AudioFormat.ENCODING_PCM_16BIT;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guitartraina.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.Map;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.GainProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.writer.WriterProcessor;

public class RecorderActivity extends AppCompatActivity {
    private EditText etTitle, etDesc;
    private ImageButton btnPlay, btnRecord, btnReRecord, btnPublish;
    private TextView tvSeconds, tvNoteCounter;
    private Thread recorderThread = null;
    private AudioDispatcher dispatcher = null;
    private final int SAMPLE_RATE = 44100;
    private final int RECORD_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_IN_MONO, ENCODING_PCM_16BIT);
    private final int RECORD_BUFFER_OVERLAP = RECORD_BUFFER_SIZE / 2;
    private final Runnable stopRecording = () -> {Toast.makeText(RecorderActivity.this, "Deteniendo", Toast.LENGTH_SHORT).show(); dispatcher.stop();};
    ;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        btnRecord = findViewById(R.id.record_btn);
        btnPlay = findViewById(R.id.play_btn);
        btnReRecord = findViewById(R.id.re_record_btn);
        btnPublish = findViewById(R.id.publish_btn);
        disablePlayReRecordPublishButtons();
        btnRecord.setOnClickListener(view -> {
            view.setEnabled(false);
            view.setAlpha(0.5f);
            recordAudio();
            handler.postDelayed(stopRecording, 5000);
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            };
            requestPermissionLauncher.launch(permissions);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ViewGroup parentView = findViewById(android.R.id.content);
            LinearLayout linearLayout = createInfoLayout();
            parentView.removeAllViews();
            parentView.addView(linearLayout);
        }
    }

    private void disablePlayReRecordPublishButtons() {
        btnPlay.setEnabled(false);
        btnReRecord.setEnabled(false);
        btnPublish.setEnabled(false);
        btnPlay.setAlpha(0.5f);
        btnReRecord.setAlpha(0.5f);
        btnPublish.setAlpha(0.5f);
    }

    private void recordAudio() {
        int CHANNELS = 1;
        int BIT_DEPTH = 16;
        boolean BIG_ENDIAN = false;
        int SAMPLE_RATE = 44100;
        TarsosDSPAudioFormat audioFormat = new TarsosDSPAudioFormat(TarsosDSPAudioFormat.Encoding.PCM_SIGNED, SAMPLE_RATE, BIT_DEPTH, CHANNELS, 2, 1, BIG_ENDIAN);
        double gain = getGainFromPreferences();
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, RECORD_BUFFER_SIZE, RECORD_BUFFER_OVERLAP);
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(RecorderActivity.this.getExternalFilesDir(null) + "/audioTest.wav", "rw");
        } catch (FileNotFoundException notFoundException) {
            File file = new File(RecorderActivity.this.getExternalFilesDir(null), "audioTest.wav");
            try {
                if (!file.exists()) {
                    file.createNewFile();
                    System.out.println("no existe");
                }
                randomAccessFile = new RandomAccessFile(file, "rw");
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
        AudioProcessor gainProcessor = new GainProcessor(gain);
        WriterProcessor writerProcessor = new WriterProcessor(audioFormat, randomAccessFile);
        dispatcher.addAudioProcessor(gainProcessor);
        dispatcher.addAudioProcessor(writerProcessor);
        recorderThread = new Thread(dispatcher, "Audio Dispatcher");
        recorderThread.setPriority(Thread.MAX_PRIORITY);
        recorderThread.start();
    }

    private double getGainFromPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RecorderActivity.this);
        int microphoneGain = sharedPreferences.getInt("microphone_gain", 10);
        return microphoneGain / 10.d;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(stopRecording);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private final ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                boolean allPermissionsGranted = true;
                for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
                    String permission = entry.getKey();
                    boolean isGranted = entry.getValue();
                    if (!isGranted) {
                        allPermissionsGranted = false;
                        // Handle the denied permission accordingly
                        // You can show an error message or take appropriate action
                        Toast.makeText(RecorderActivity.this, "Este modulo no puede funcionar sin el permiso: " + permission, Toast.LENGTH_SHORT).show();
                    }
                }
                if (allPermissionsGranted) {
                    // All permissions granted
                    Toast.makeText(RecorderActivity.this, "All permissions granted", Toast.LENGTH_SHORT).show();
                    RecorderActivity.this.recreate();
                }
            }
    );

    private LinearLayout createInfoLayout() {
        LinearLayout layout = new LinearLayout(RecorderActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        float dpi = this.getResources().getDisplayMetrics().density;
        layout.setPaddingRelative((int) (19 * dpi), 0, (int) (19 * dpi), 0);
        TextView tvInfo = new TextView(RecorderActivity.this);
        tvInfo.setText(R.string.info_permission_recorder);
        tvInfo.setTextSize(20.0f);
        tvInfo.setGravity(Gravity.CENTER);
        tvInfo.setPaddingRelative(0, 0, 0, (int) (19 * dpi));
        //create a button
        Button btnPermiss = new Button(RecorderActivity.this);
        btnPermiss.setText(R.string.dar_permiso);
        btnPermiss.setOnClickListener((View view) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", RecorderActivity.this.getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
            RecorderActivity.this.finishAfterTransition();
        });
        layout.addView(tvInfo);
        layout.addView(btnPermiss);
        return layout;
    }
}