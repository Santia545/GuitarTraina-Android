package com.example.guitartraina.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.guitartraina.R;

public class ChordLibraryActivity extends AppCompatActivity {
    private Spinner rootNote;
    private Spinner chordType;
    private ImageView chordDiagram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chord_library);
        rootNote=findViewById(R.id.root_note);
        chordType=findViewById(R.id.chord_type);
        chordDiagram=findViewById(R.id.chordView);
        rootNote.setSelection(7);
        rootNote.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int itemPosition, long l) {
                String note = adapterView.getItemAtPosition(itemPosition).toString();
                Drawable drawable = AppCompatResources.getDrawable(ChordLibraryActivity.this,R.drawable.e_chord);
                chordDiagram.setImageDrawable(drawable);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        chordType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int itemPosition, long l) {
                String chord = adapterView.getItemAtPosition(itemPosition).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
}