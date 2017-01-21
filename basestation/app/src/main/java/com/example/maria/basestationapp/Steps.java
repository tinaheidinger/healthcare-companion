package com.example.maria.basestationapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;

/**
 * Created by Marie on 20.01.2017.
 */

public class Steps extends AppCompatActivity {
    private Button backButton;

    private static final String TAG = "StepsScreen";

    private int stepsMo;
    private int stepsDi;
    private int stepsMi;
    private int stepsDo;
    private int stepsFr;
    private int stepsSa;
    private int stepsSo;

    public int dayStepsAmount;

    LineGraphSeries<DataPoint> series;

    public Calendar now = Calendar.getInstance();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        final GraphView graph = (GraphView) findViewById(R.id.graph);
        GridLabelRenderer glr = graph.getGridLabelRenderer();
        glr.setPadding(50);
        series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, stepsMo),
                new DataPoint(1, stepsDi),
                new DataPoint(2, stepsMi),
                new DataPoint(3, stepsDo),
                new DataPoint(4, stepsFr),
                new DataPoint(5, stepsSa),
                new DataPoint(6, stepsSo)
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

    }

    public void checkDayOfWeek(){
        switch(now.get(Calendar.DAY_OF_WEEK)){
            case(Calendar.MONDAY):
                stepsMo = dayStepsAmount;
                break;
            case(Calendar.TUESDAY):
                stepsDi = dayStepsAmount;
                break;
            case(Calendar.WEDNESDAY):
                stepsMi = dayStepsAmount;
                break;
            case(Calendar.THURSDAY):
                stepsDo = dayStepsAmount;
                break;
            case(Calendar.FRIDAY):
                stepsFr = dayStepsAmount;
                break;
            case(Calendar.SATURDAY):
                stepsSa = dayStepsAmount;
                break;
            case(Calendar.SUNDAY):
                stepsSo = dayStepsAmount;
                break;
            default:
                break;
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}

