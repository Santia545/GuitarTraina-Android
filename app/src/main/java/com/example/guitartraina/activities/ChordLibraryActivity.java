package com.example.guitartraina.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
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
import java.util.List;
import java.util.ListIterator;

public class ChordLibraryActivity extends AppCompatActivity {
    private Spinner rootNote;
    private Spinner chordType;
    private ImageButton btnNext, btnPrevious;
    private ImageView chordDiagram;
    private String note = "";
    private String direction = "";
    private TextView barreChordWarning, fretIndicator;
    private final String[] sixthStringNotes = new String[13];
    private final String[] fifthStringNotes = new String[13];
    private final String[] fourthStringNotes = new String[13];
    private List<Drawable> drawableList = new ArrayList<>();
    private ListIterator<Drawable> drawableListIterator = drawableList.listIterator();
    private int foregroundColor, backgroundColor;
    private int[] diagramIndexString = new int[]{6, 6, 5, 5, 4};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chord_library);
        Resources.Theme currentTheme = this.getTheme();
        TypedValue typedValue = new TypedValue();
        currentTheme.resolveAttribute(android.R.attr.colorForeground, typedValue, true);
        foregroundColor = typedValue.data;
        currentTheme.resolveAttribute(android.R.attr.colorBackground, typedValue, true);
        backgroundColor = typedValue.data;
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


        rootNote.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int itemPosition, long l) {
                note = adapterView.getItemAtPosition(itemPosition).toString().toLowerCase();
                if (note.contains("#")) {
                    note = note.replaceAll("\\W", "");
                }
                if (drawableListIterator.nextIndex() != 0) {
                    setFret(diagramIndexString[drawableListIterator.nextIndex() - 1]);
                } else {
                    setFret(6);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        chordType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int itemPosition, long l) {
                String chordType;
                diagramIndexString = new int[]{6, 6, 5, 5, 4};
                switch (itemPosition) {
                    case 1:
                        chordType = "m";
                        break;
                    case 2:
                        chordType = "5";
                        diagramIndexString = new int[]{6, 5, 4};
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
                drawableListIterator = drawableList.listIterator();
                setDiagram(drawableList.get(0), 6);
                btnPrevious.setEnabled(false);
                btnPrevious.setColorFilter(backgroundColor);
                btnNext.setEnabled(true);
                btnNext.setColorFilter(foregroundColor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        initItems();
        btnNext.setOnClickListener(view -> {
            if (!direction.equals("right")) {
                drawableListIterator.next();
            }
            Drawable drawable = drawableListIterator.next();
            int diagramIndex = drawableListIterator.nextIndex() - 1;
            setDiagram(drawable, diagramIndexString[diagramIndex]);
            if (!chordType.getSelectedItem().equals("5")) {
                if (diagramIndex == 1 || diagramIndex == 3) {
                    int fret = findFret(diagramIndexString[diagramIndex], true) - 3;
                    if (fret != 0) {
                        fretIndicator.setText(String.format("%d", fret));
                        barreChordWarning.setVisibility(View.VISIBLE);
                    } else {
                        fretIndicator.setText("");
                        barreChordWarning.setVisibility(View.INVISIBLE);
                    }
                }
            }
            Toast.makeText(ChordLibraryActivity.this, "Next", Toast.LENGTH_SHORT).show();
            btnPrevious.setEnabled(true);
            btnPrevious.setColorFilter(foregroundColor);
            if (!drawableListIterator.hasNext()) {
                btnNext.setEnabled(false);
                btnNext.setColorFilter(backgroundColor);
            }
            direction = "right";
        });
        btnPrevious.setOnClickListener(view -> {
            if (!direction.equals("left")) {
                drawableListIterator.previous();
            }
            Drawable drawable = drawableListIterator.previous();
            int diagramIndex = drawableListIterator.previousIndex() + 1;
            setDiagram(drawable, diagramIndexString[diagramIndex]);
            if (!chordType.getSelectedItem().equals("5")) {
                if (diagramIndex == 1 || diagramIndex == 3) {
                    int fret = findFret(diagramIndexString[diagramIndex],true) - 3;
                    if (fret != 0) {
                        fretIndicator.setText(String.format("%d", fret));
                        barreChordWarning.setVisibility(View.VISIBLE);
                    } else {
                        fretIndicator.setText("");
                        barreChordWarning.setVisibility(View.INVISIBLE);
                    }
                }
            }
            Toast.makeText(ChordLibraryActivity.this, "Previous", Toast.LENGTH_SHORT).show();
            btnNext.setEnabled(true);
            btnNext.setColorFilter(foregroundColor);
            if (!drawableListIterator.hasPrevious()) {
                btnPrevious.setEnabled(false);
                btnPrevious.setColorFilter(backgroundColor);
            }
            direction = "left";
        });
    }

    private void setDiagram(Drawable drawable, int string) {
        setFret(string);
        chordDiagram.setImageDrawable(drawable);
    }

    private void setFret(int string) {
        int fret = findFret(string,false);
        if (fret != 0) {
            fretIndicator.setText(String.format("%d", fret));
            barreChordWarning.setVisibility(View.VISIBLE);
        } else {
            fretIndicator.setText("");
            barreChordWarning.setVisibility(View.INVISIBLE);
        }
    }

    private int findFret(int string, boolean skipFirst) {
        switch (string) {
            case 6:
                for (int i = 0; i < sixthStringNotes.length; i++) {
                    if (rootNote.getSelectedItem().equals(sixthStringNotes[i])) {
                        if (!skipFirst && i != 0) return i;
                    }
                }
                break;
            case 5:
                for (int i = 0; i < fifthStringNotes.length; i++) {
                    if (rootNote.getSelectedItem().equals(fifthStringNotes[i])) {
                        if (!skipFirst && i != 0) return i;
                    }
                }
                break;
            case 4:
                for (int i = 0; i < fourthStringNotes.length; i++) {
                    if (rootNote.getSelectedItem().equals(fourthStringNotes[i])) {
                        return i;
                    }
                }
                break;
        }
        return 0;
    }

    private void initItems() {
        rootNote.setSelection(7);
        chordType.setSelection(0);
    }
}