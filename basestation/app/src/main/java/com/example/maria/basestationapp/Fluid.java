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

import static com.example.maria.basestationapp.R.id.monthFluid;
import static com.example.maria.basestationapp.R.id.weekFluid;

/**
 * Created by Marie on 18.01.2017.
 */

public class Fluid extends AppCompatActivity{
    private static final String TAG = "FluidScreen";

    private Button backButton;
    private Button weekButton;
    private Button monthButton;

    private SeekBar waterAmountSlider;
    private FloatingActionButton addWaterToDay;

    public int waterAmount = 0;
    public int dayWaterAmount = 0;

    private int[] waterWeek = new int[7];
    private int[] waterMonth = new int[31];

    LineGraphSeries<DataPoint> seriesWeek;
    LineGraphSeries<DataPoint> seriesMonth;

    public String[] monthXAxis= new String[31];

    public GraphView graph;

    public Calendar now = Calendar.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fluid);

        for (int i=0; i<31; i++) {
            monthXAxis[i] = new String(Integer.toString(i+1));
        }

        graph = (GraphView) findViewById(R.id.graph);
        GridLabelRenderer glr = graph.getGridLabelRenderer();
        glr.setPadding(50);

        seriesWeek = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, waterWeek[0]),
                new DataPoint(1, waterWeek[1]),
                new DataPoint(2, waterWeek[2]),
                new DataPoint(3, waterWeek[3]),
                new DataPoint(4, waterWeek[4]),
                new DataPoint(5, waterWeek[5]),
                new DataPoint(6, waterWeek[6])
        });

        graph.addSeries(seriesWeek);

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

        weekButton = (Button) findViewById(weekFluid);
        weekButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                changeToMonthView();
            }
        });

        monthButton = (Button) findViewById(monthFluid);
        monthButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                changeToWeekView();
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

    public void changeToMonthView(){
        DataPoint[] datapoints = new DataPoint[31];
/*
        seriesMonth = new LineGraphSeries<>(new DataPoint[] {

                new DataPoint(0, waterWeek[0]),
                new DataPoint(1, waterWeek[1]),
                new DataPoint(2, waterWeek[2]),
                new DataPoint(3, waterWeek[3]),
                new DataPoint(4, waterWeek[4]),
                new DataPoint(5, waterWeek[5]),
                new DataPoint(6, waterWeek[6])
        });
*/
        for (int i=0; i<31; i++) {
            datapoints[i] = new DataPoint(i, waterMonth[i]);
        }
        seriesMonth = new LineGraphSeries<>(datapoints);
        graph.addSeries(seriesMonth);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[]{"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
    }

    public void changeToWeekView(){
        graph.addSeries(seriesWeek);

        StaticLabelsFormatter staticLabelsFormatterMonth = new StaticLabelsFormatter(graph);
        staticLabelsFormatterMonth.setHorizontalLabels(monthXAxis);
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatterMonth);
    }

    public void checkDayOfWeek(){
        switch(now.get(Calendar.DAY_OF_WEEK)){
            case(Calendar.MONDAY):
                waterWeek[0] = dayWaterAmount;
                break;
            case(Calendar.TUESDAY):
                waterWeek[1] = dayWaterAmount;
                break;
            case(Calendar.WEDNESDAY):
                waterWeek[2] = dayWaterAmount;
                break;
            case(Calendar.THURSDAY):
                waterWeek[3] = dayWaterAmount;
                break;
            case(Calendar.FRIDAY):
                waterWeek[4] = dayWaterAmount;
                break;
            case(Calendar.SATURDAY):
                waterWeek[5] = dayWaterAmount;
                break;
            case(Calendar.SUNDAY):
                waterWeek[6] = dayWaterAmount;
                break;
            default:
                break;
        }
    }

    public void onBackPressed(){
        super.onBackPressed();
        this.finish();
    }

    public void onMonthPressed(){
        changeToMonthView();
    }

    public void onWeekPressed(){
        changeToWeekView();
    }

    public void onAddWaterPressed(){
        dayWaterAmount += waterAmount;
        checkDayOfWeek();
        seriesWeek.resetData(new DataPoint[] {
                new DataPoint(0, waterWeek[0]),
                new DataPoint(1, waterWeek[1]),
                new DataPoint(2, waterWeek[2]),
                new DataPoint(3, waterWeek[3]),
                new DataPoint(4, waterWeek[4]),
                new DataPoint(5, waterWeek[5]),
                new DataPoint(6, waterWeek[6])
        });
    }
}

