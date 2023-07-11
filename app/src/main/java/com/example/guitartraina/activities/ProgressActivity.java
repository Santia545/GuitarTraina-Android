package com.example.guitartraina.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;

import com.example.guitartraina.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;

public class ProgressActivity extends AppCompatActivity {
    private BarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        chart = findViewById(R.id.chartContainer);

        setupChart();
        setData();
    }

    private void setupChart() {
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        // Customize the appearance of the chart
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);

        // Customize the x-axis labels
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(25);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(270f);
        xAxis.setTextSize(20f);
    }

    private void setData() {
        // Mock data from API response
        List<DateValue> dateValues = getAPIResponse();

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < dateValues.size(); i++) {
            DateValue dateValue = dateValues.get(i);
            entries.add(new BarEntry(i, dateValue.getValue()));
            labels.add(dateValue.getDate());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Label");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextSize(20f);
        BarData barData = new BarData(dataSet);
        chart.setData(barData);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

        // Set the initial zoom level
        float visibleRange = 5f; // Set the number of visible bars on the chart
        chart.setVisibleXRangeMaximum(visibleRange);
        chart.invalidate();
    }

    // Example method to get API response
    private List<DateValue> getAPIResponse() {
        List<DateValue> dateValues = new ArrayList<>();
        // Make API request and populate dateValues with the response data
        // For this example, I'm adding dummy data
        dateValues.add(new DateValue("2023-06-01", 50));
        dateValues.add(new DateValue("2023-06-02", 60f));
        dateValues.add(new DateValue("2023-06-03", 70));
        dateValues.add(new DateValue("2023-06-04", 71));
        dateValues.add(new DateValue("2023-06-05", 72f));
        dateValues.add(new DateValue("2023-06-06", 74f));
        dateValues.add(new DateValue("2023-06-07", 76f));
        dateValues.add(new DateValue("2023-06-08", 78f));
        dateValues.add(new DateValue("2023-06-09", 79f));
        dateValues.add(new DateValue("2023-06-10", 80f));
        dateValues.add(new DateValue("2023-06-11", 81f));
        dateValues.add(new DateValue("2023-06-12", 82f));
        dateValues.add(new DateValue("2023-06-13", 83f));
        dateValues.add(new DateValue("2023-06-14", 84f));
        dateValues.add(new DateValue("2023-06-15", 85f));
        dateValues.add(new DateValue("2023-06-16", 86f));
        dateValues.add(new DateValue("2023-06-17", 87f));
        dateValues.add(new DateValue("2023-06-18", 88f));
        dateValues.add(new DateValue("2023-06-19", 89f));
        dateValues.add(new DateValue("2023-06-20", 90f));
        dateValues.add(new DateValue("2023-06-21", 91f));
        dateValues.add(new DateValue("2023-06-22", 92f));
        dateValues.add(new DateValue("2023-06-23", 93f));
        dateValues.add(new DateValue("2023-06-24", 94f));
        dateValues.add(new DateValue("2023-06-25", 95f));
        dateValues.add(new DateValue("2023-06-26", 96f));
        dateValues.add(new DateValue("2023-06-27", 97f));
        dateValues.add(new DateValue("2023-06-28", 98f));
        dateValues.add(new DateValue("2023-06-29", 99f));
        dateValues.add(new DateValue("2023-06-30", 100f));
        dateValues.add(new DateValue("2023-06-31", 100f));


        return dateValues;
    }

    // Example class representing date and value pair
    private static class DateValue {
        private String date;
        private float value;

        public DateValue(String date, float value) {
            this.date = date;
            this.value = value;
        }

        public String getDate() {
            return date;
        }

        public float getValue() {
            return value;
        }
    }
}