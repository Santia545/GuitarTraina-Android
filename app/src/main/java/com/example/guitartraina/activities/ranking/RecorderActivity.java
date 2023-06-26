package com.example.guitartraina.activities.ranking;

import static android.media.AudioFormat.CHANNEL_IN_MONO;
import static android.media.AudioFormat.CHANNEL_OUT_MONO;
import static android.media.AudioFormat.ENCODING_PCM_16BIT;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.guitartraina.R;
import com.example.guitartraina.activities.account.LogInActivity;
import com.example.guitartraina.api.IResult;
import com.example.guitartraina.api.VolleyService;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.GainProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.UniversalAudioInputStream;
import be.tarsos.dsp.io.android.AndroidAudioPlayer;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.writer.WriterProcessor;

public class RecorderActivity extends AppCompatActivity {
    private class AudioModel {
        public byte[] AudioData;
        public String FileName;
    }
    private IResult resultCallback = null;
    private VolleyService volleyService;

    private EditText etTitle, etDesc;
    private ImageButton btnPlay, btnRecord, btnReRecord, btnPublish;
    private TextView tvSeconds, tvNoteCounter;
    private Thread recorderThread = null;
    private AudioDispatcher dispatcher = null;
    int CHANNELS = 1;
    int BIT_DEPTH = 16;
    boolean BIG_ENDIAN = false;
    private final int SAMPLE_RATE = 44100;
    TarsosDSPAudioFormat audioFormat = new TarsosDSPAudioFormat(TarsosDSPAudioFormat.Encoding.PCM_SIGNED, SAMPLE_RATE, BIT_DEPTH, CHANNELS, 2, 1, BIG_ENDIAN);
    private final int RECORD_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_IN_MONO, ENCODING_PCM_16BIT);
    private final int RECORD_BUFFER_OVERLAP = RECORD_BUFFER_SIZE / 2;
    private final Runnable stopRecording = () -> {
        Toast.makeText(RecorderActivity.this, "Deteniendo", Toast.LENGTH_SHORT).show();
        dispatcher.stop();
        togglePlayReRecordPublishButtons(true);
    };

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        initVolleyCallback();
        volleyService = new VolleyService(resultCallback, this);
        etTitle=findViewById(R.id.title_et);
        etDesc=findViewById(R.id.description_et);
        btnRecord = findViewById(R.id.record_btn);
        btnPlay = findViewById(R.id.play_btn);
        btnReRecord = findViewById(R.id.re_record_btn);
        btnPublish = findViewById(R.id.publish_btn);
        togglePlayReRecordPublishButtons(false);
        //debug code
        etTitle.setText("Test");
        btnPlay.setEnabled(true);
        btnPublish.setEnabled(true);

        btnRecord.setOnClickListener(view -> {
            view.setEnabled(false);
            view.setAlpha(0.5f);
            recordAudio();
            handler.postDelayed(stopRecording, 2500);
        });
        btnPublish.setOnClickListener(view -> {
            if(etTitle.getText().toString().equals("")){
                Toast.makeText(RecorderActivity.this, "title cant be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            File file = new File(getExternalFilesDir(null), "audioTest.wav");
            String filePath = file.getPath();
            byte [] audioData= readAudioFileData(filePath);
            // Convert byte array to Base64 string
            String base64Audio = android.util.Base64.encodeToString(audioData, android.util.Base64.DEFAULT);

            // Create the JSON object
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("audioData", base64Audio);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


            //debug code
            volleyService.postDataVolley("/Ranking",jsonObject);

            //volleyService.postDataVolley("/Ranking?title="+etTitle.getText().toString()+"&email="+"a19100060@ceti.mx"+"&notes="+10+"&description="+"test", jsonObject);
        });
        btnPlay.setOnClickListener(view -> playSound());
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

    private void togglePlayReRecordPublishButtons(boolean enabled) {
        if (!enabled) {
            btnPlay.setEnabled(false);
            btnReRecord.setEnabled(false);
            btnPublish.setEnabled(false);
            btnPlay.setAlpha(0.5f);
            btnReRecord.setAlpha(0.5f);
            btnPublish.setAlpha(0.5f);
        } else {
            btnPlay.setEnabled(true);
            btnReRecord.setEnabled(true);
            btnPublish.setEnabled(true);
            btnPlay.setAlpha(1f);
            btnReRecord.setAlpha(1f);
            btnPublish.setAlpha(1f);
        }

    }

    private double getGainFromPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RecorderActivity.this);
        int microphoneGain = sharedPreferences.getInt("microphone_gain", 10);
        return microphoneGain / 10.d;
    }

    private void recordAudio() {
        double gain = getGainFromPreferences();
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, RECORD_BUFFER_SIZE, RECORD_BUFFER_OVERLAP);
        AudioProcessor gainProcessor = new GainProcessor(gain);
        File file = new File(getExternalFilesDir(null), "audioTest.wav");
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
        dispatcher.addAudioProcessor(gainProcessor);
        dispatcher.addAudioProcessor(writerProcessor);
        dispatcher.addAudioProcessor(androidAudioPlayer);
        recorderThread = new Thread(dispatcher, "Audio Dispatcher");
        recorderThread.setPriority(Thread.MAX_PRIORITY);
        recorderThread.start();
    }

    private void playSound() {
        int BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_OUT_MONO, ENCODING_PCM_16BIT);
        File file = new File(getExternalFilesDir(null), "audioTest.wav");
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            UniversalAudioInputStream audioStream = new UniversalAudioInputStream(fileInputStream, audioFormat);
            AudioDispatcher dispatcher = new AudioDispatcher(audioStream, BUFFER_SIZE, BUFFER_SIZE / 2);
            AndroidAudioPlayer player = new AndroidAudioPlayer(audioFormat);
            dispatcher.addAudioProcessor(player);
            Thread audioDispatcher = new Thread(dispatcher, "Audio Dispatcher");
            audioDispatcher.start();

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the IOException
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recorderThread != null) {
            try {
                recorderThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
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

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, Object response) {
                Toast.makeText(RecorderActivity.this, getString(R.string.exito), Toast.LENGTH_LONG).show();
                Log.d("notifySuccess", "Volley requester " + requestType);
                Log.d("notifySuccess", "Volley JSON post" + response);
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                error.printStackTrace();
                String body = "";
                String errorCode = "";
                try {
                    errorCode = "" + error.networkResponse.statusCode;
                    body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String cause = "";
                if (error.getCause() != null) {
                    cause = error.getCause().getMessage();
                }
                Toast.makeText(RecorderActivity.this, getString(R.string.fallido) + cause + " " + body + " ", Toast.LENGTH_LONG).show();
                Log.d("notifyError", "Volley requester " + requestType);
                Log.d("notifyError", "Volley JSON post" + "That didn't work!" + error + " " + errorCode);
                Log.d("notifyError", "Error: " + error
                        + "\nStatus Code " + errorCode
                        + "\nResponse Data " + body
                        + "\nCause " + error.getCause()
                        + "\nmessage " + error.getMessage());
            }
        };
    }

    private byte[] readAudioFileData(String filePath) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filePath));
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
        } catch (IOException e) {
            Log.e("Error", "Failed to read audio file. Error: " + e.getMessage());
        }
        return outputStream.toByteArray();
    }
}
