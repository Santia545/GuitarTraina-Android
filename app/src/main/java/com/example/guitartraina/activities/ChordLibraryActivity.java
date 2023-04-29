package com.example.guitartraina.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guitartraina.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ChordLibraryActivity extends AppCompatActivity {
    private Spinner rootNote;
    private Spinner chordType;
    private ImageButton btnNext, btnPrevious;
    private ImageView chordDiagram;
    private String note = "";
    private boolean isSharp = false;
    private TextView barreChordWarning, fretIndicator;
    private final String[] sixthStringNotes = new String[13];
    private final String[] fifthStringNotes = new String[13];
    private final String[] fourthStringNotes = new String[13];
    private List<Drawable> drawableList = new ArrayList<>();
    ListIterator<Drawable>drawableListIterator = drawableList.listIterator();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chord_library);
        String[] notes = getResources().getStringArray(R.array.notes);
        for (int i = 0; i <= notes.length; i++) {
            sixthStringNotes[i] = notes[(7 + i) % notes.length];
            fifthStringNotes[i] = notes[i % notes.length];
            fourthStringNotes[i] = notes[(5 + i) % notes.length];
        }
        barreChordWarning = findViewById(R.id.textView2);
        fretIndicator = findViewById(R.id.fret_indicator);
        rootNote = findViewById(R.id.root_note);
        chordType = findViewById(R.id.chord_type);
        chordDiagram = findViewById(R.id.chordView);
        btnNext = findViewById(R.id.next_chord_btn);
        btnPrevious = findViewById(R.id.before_chord_btn);
        btnPrevious.setEnabled(false);

        rootNote.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int itemPosition, long l) {
                note = adapterView.getItemAtPosition(itemPosition).toString().toLowerCase();
                if (note.contains("#")) {
                    isSharp = true;
                    note = note.replaceAll("\\W", "");
                    return;
                }
                isSharp = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        chordType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int itemPosition, long l) {
                String chordType;
                switch (itemPosition) {
                    case 1:
                        chordType = "m";
                        break;
                    case 2:
                        chordType = "5";
                        break;
                    case 3:
                        chordType = "7";
                        break;
                    case 4:
                        chordType = "m7";
                        break;
                    default:
                        chordType = "";
                }

                String[] caged = new String[]{"e", "g", "a", "c", "d"};
                Class<R.drawable> res = R.drawable.class;
                drawableList = new ArrayList<>();
                for (String chord : caged) {
                    try {
                        Field field = res.getField(chord + chordType + "_chord");
                        int drawableId = field.getInt(null);
                        drawableList.add(AppCompatResources.getDrawable(ChordLibraryActivity.this, drawableId));
                    } catch (Exception ignored) {
                    }
                }
                drawableListIterator= drawableList.listIterator();
                chordDiagram.setImageDrawable(drawableListIterator.next());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        initItems();
        btnNext.setOnClickListener(view -> {
            chordDiagram.setImageDrawable(drawableListIterator.next());
            Toast.makeText(ChordLibraryActivity.this, "Next", Toast.LENGTH_SHORT).show();
            btnPrevious.setEnabled(true);
            if(!drawableListIterator.hasNext()){
                btnNext.setEnabled(false);
            }
        });
        btnPrevious.setOnClickListener(view -> {
            chordDiagram.setImageDrawable(drawableListIterator.previous());
            Toast.makeText(ChordLibraryActivity.this, "Previous", Toast.LENGTH_SHORT).show();
            btnNext.setEnabled(true);
            if(!drawableListIterator.hasPrevious()){
                btnPrevious.setEnabled(false);
            }
        });
    }

    private void initItems() {
        rootNote.setSelection(7);
        chordType.setSelection(0);
    }
}