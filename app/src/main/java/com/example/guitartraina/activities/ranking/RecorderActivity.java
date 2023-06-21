package com.example.guitartraina.activities.ranking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.guitartraina.R;

public class RecorderActivity extends AppCompatActivity {
    private EditText etTitle, etDesc;
    private ImageButton btnPlay, btnRecord, btnReRecord, btnPublish;
    private TextView tvSeconds, tvNoteCounter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        btnRecord=findViewById(R.id.record_btn);
        btnPlay=findViewById(R.id.play_btn);
        btnRecord.setOnClickListener(view -> {
            view.setEnabled(false);

        });
    }
}