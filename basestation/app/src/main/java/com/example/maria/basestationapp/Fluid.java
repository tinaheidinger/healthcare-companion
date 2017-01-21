package com.example.maria.basestationapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;

/**
 * Created by Marie on 18.01.2017.
 */

public class Fluid extends AppCompatActivity{
    private static final String TAG = "FluidScreen";

    private Button backButton;
    private SeekBar waterAmountSlider;
    private FloatingActionButton addWaterToDay;

    public int waterAmount = 0;
    public int dayWaterAmount = 0;

    private int waterMo;
    private int waterDi;
    private int waterMi;
    private int waterDo;
    private int waterFr;
    private int waterSa;
    private int waterSo;

    LineGraphSeries<DataPoint> series;

    public Calendar now = Calendar.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fluid);

        final GraphView graph = (GraphView) findViewById(R.id.graph);
        GridLabelRenderer glr = graph.getGridLabelRenderer();
        glr.setPadding(50);
        series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, waterMo),
                new DataPoint(1, waterDi),
                new DataPoint(2, waterMi),
                new DataPoint(3, waterDo),
                new DataPoint(4, waterFr),
                new DataPoint(5, waterSa),
                new DataPoint(6, waterSo)
        });

        graph.addSeries(series);
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[]{"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        backButton = (Button) findViewById(R.id.backButtonSteps);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();

            }
        });

        waterAmountSlider = (SeekBar) findViewById(R.id.waterSlider);
        waterAmountSlider.setOnSeekBarChangeListener(new waterListener());

        addWaterToDay = (FloatingActionButton) findViewById(R.id.addFluid);
        addWaterToDay.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                onAddWaterPressed();
            }
        });
    }

    private class waterListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onStartTrackingTouch(SeekBar seekBar){}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            switch (progress){
                case 0:
                    waterAmount = 0;
                    break;
                case 1:
                    waterAmount = 200;
                    break;
                case 2:
                    waterAmount = 300;
                    break;
                case 3:
                    waterAmount = 500;
                    break;
                case 4:
                    waterAmount = 750;
                    break;
                case 5:
                    waterAmount = 1000;
                    break;
            }

            final TextView waterText = (TextView) findViewById(R.id.addWaterText);
            waterText.setText(Integer.toString(waterAmount) + " " + getString(R.string.waterText));
        }
    }

    public void checkDayOfWeek(){
        switch(now.get(Calendar.DAY_OF_WEEK)){
            case(Calendar.MONDAY):
                waterMo = dayWaterAmount;
                break;
            case(Calendar.TUESDAY):
                waterDi = dayWaterAmount;
                break;
            case(Calendar.WEDNESDAY):
                waterMi = dayWaterAmount;
                break;
            case(Calendar.THURSDAY):
                waterDo = dayWaterAmount;
                break;
            case(Calendar.FRIDAY):
                waterFr = dayWaterAmount;
                break;
            case(Calendar.SATURDAY):
                waterSa = dayWaterAmount;
                break;
            case(Calendar.SUNDAY):
                waterSo = dayWaterAmount;
                break;
            default:
                break;
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public void onAddWaterPressed(){
        dayWaterAmount += waterAmount;
        checkDayOfWeek();
        series.resetData(new DataPoint[] {
                new DataPoint(0, waterMo),
                new DataPoint(1, waterDi),
                new DataPoint(2, waterMi),
                new DataPoint(3, waterDo),
                new DataPoint(4, waterFr),
                new DataPoint(5, waterSa),
                new DataPoint(6, waterSo)
        });

    }
}

