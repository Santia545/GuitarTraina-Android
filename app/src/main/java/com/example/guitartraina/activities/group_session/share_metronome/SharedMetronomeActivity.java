package com.example.guitartraina.activities.group_session.share_metronome;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.guitartraina.R;
import com.example.guitartraina.activities.group_session.Host;
import com.example.guitartraina.activities.group_session.share_metronome.sync_utilities.MetroSyncClient;
import com.example.guitartraina.activities.group_session.share_metronome.sync_utilities.MetroSyncServer;
import com.example.guitartraina.activities.metronome.Metronome;
import com.example.guitartraina.databinding.ActivitySharedMetronomeBinding;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class SharedMetronomeActivity extends AppCompatActivity {
    public ActivitySharedMetronomeBinding binding;
    private int usertype;
    private Metronome metronome;
    private int noteType;
    private int noteNumber;
    private MetroSyncServer syncServer;
    private MetroSyncClient syncClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySharedMetronomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        usertype = intent.getIntExtra("user", 0);

        metronome = new Metronome(this);
        if(usertype == 0){
            syncServer = new MetroSyncServer(this);
            try {
                InetAddress addr = InetAddress.getByName("127.0.0.1");
                syncClient = new MetroSyncClient(this, addr);
            } catch (UnknownHostException e) {
                Log.e(TAG, e.toString());
            }
            binding.controlsGroup.setVisibility(View.VISIBLE);
            addViewOnClickListeners();
        }else{
            Host host = getIntent().getParcelableExtra("host");
            syncClient = new MetroSyncClient(this, host.getHostAddress());
        }
    }
    public Metronome getMetronome() {
        return metronome;
    }
    private void addViewOnClickListeners() {
        Objects.requireNonNull(binding.beatsPerMinute.getEditText()).setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                String text = textView.getText().toString();
                if (text.equals("")) {
                    Toast.makeText(this, R.string.metronome_empty_bpm_error, Toast.LENGTH_SHORT).show();
                    binding.beatsPerMinute.getEditText().setText(R.string.defalut_bpm);
                    return true; // Consume the event
                }
                int bpm = Integer.parseInt(text);
                if (bpm > 500) {
                    Toast.makeText(this, R.string.metronome_invalid_bpm_error, Toast.LENGTH_SHORT).show();
                    binding.beatsPerMinute.getEditText().setText(R.string.defalut_bpm);
                    return true; // Consume the event
                }
                metronome.setBpm(bpm);
                return true;
            }
            return false;
        });
        binding.timeSignature.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int itemPosition, long l) {
                String[] timeSignature = adapterView.getItemAtPosition(itemPosition).toString().split("/");
                noteNumber = Integer.parseInt(timeSignature[0]);
                noteType = Integer.parseInt(timeSignature[1]);
                binding.metronomeView.setNotesNumber(noteNumber);
                metronome.setNotesNumber(noteNumber);
                metronome.setNoteType(noteType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        binding.metronomePlayBtn.setOnClickListener(view -> {
            if(metronome.isRunning()){
                syncServer.syncPlayState(false);
            }else{
                syncServer.syncPlayState(true);
            }
        });
        binding.switch1.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                metronome.setNoteAccent(0);
            }else{
                metronome.setNoteAccent(-1);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if(metronome.isRunning()){
            metronome.pause();
            binding.metronomePlayBtn.setText(R.string.play);
        }
    }
}