package com.example.guitartraina.ui.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.Locale;

public class FrequencyView extends View {
    private static final int TEXT_SIZE = 50;
    private static final int[] NOTE_COLORS = {Color.RED, Color.YELLOW, Color.GREEN};
    private double cents;

    private Paint paintText;
    private Paint paintGraph;
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
        paintText = new Paint();
        paintGraph = new Paint();
        paintGraph.setTextAlign(Paint.Align.CENTER);
        paintGraph.setTextSize(TEXT_SIZE*2);
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
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int colorIndex = getCentsColor(cents);
        paintGraph.setColor(NOTE_COLORS[colorIndex]);
        float graphPosition = map(centerX, cents);
        canvas.drawText("|", graphPosition, 250 + TEXT_SIZE, paintGraph);
        canvas.drawText(String.format(Locale.getDefault(), "%.2f",  cents), centerX, centerY + 80 + TEXT_SIZE, paintText);
    }
    public int map(int center, double mCentsDiff) {
        int borders= (int) (20*this.getResources().getDisplayMetrics().density);
        if (mCentsDiff > 50) {
            return (center*2)-borders;
        } else if (mCentsDiff<-50) {
            return borders;
        }
        int input_end= 50;
        int input_start =-50;
        float output_end = (center*2)-borders;
        int input_range = input_end - input_start;
        float output_range = output_end - borders;
        double output = (mCentsDiff - input_start)*output_range / input_range + borders;
        return (int)output;
    }
    private int getCentsColor(double cents) {
        if (Math.abs(cents) > 10) {
            return 0; // red color
        } else if (Math.abs(cents) > 5) {
            return 1; // yellow color
        } else {
            return 2; // green color
        }
    }
}