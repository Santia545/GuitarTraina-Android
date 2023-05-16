package com.example.guitartraina.ui.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class FrequencyView extends View {
    private Integer noteIndex;

    public FrequencyView(Context context) {
        super(context);
        init();
    }

    public FrequencyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FrequencyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Get the current theme of the activity
        Resources.Theme currentTheme = getContext().getTheme();
        // Create a new TypedValue object to hold the color value
        TypedValue typedValue = new TypedValue();
        // Retrieve the color value of the text color attribute from the current theme
        currentTheme.resolveAttribute(android.R.attr.textColor, typedValue, true);
        // Get the color value as an integer
        int textColor = typedValue.data;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}