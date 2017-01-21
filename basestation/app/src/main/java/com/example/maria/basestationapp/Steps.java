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
    private Button weekButton;
    private Button monthButton;

    private static final String TAG = "StepsScreen";

    private int[] stepsWeek = new int[7];
    private int[] stepsMonth = new int[31];

    public int dayStepsAmount;

    LineGraphSeries<DataPoint> seriesWeek;
    LineGraphSeries<DataPoint> seriesMonth;

    public String[] monthXAxis= new String[31];

    public GraphView graph;


    public Calendar now = Calendar.getInstance();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        for (int i=0; i<31; i++) {
            monthXAxis[i] = Integer.toString(i+1);
        }

        graph = (GraphView) findViewById(R.id.graph);
        GridLabelRenderer glr = graph.getGridLabelRenderer();
        glr.setPadding(50);
        seriesWeek = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, stepsWeek[0]),
                new DataPoint(1, stepsWeek[1]),
                new DataPoint(2, stepsWeek[2]),
                new DataPoint(3, stepsWeek[3]),
                new DataPoint(4, stepsWeek[4]),
                new DataPoint(5, stepsWeek[5]),
                new DataPoint(6, stepsWeek[6])
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

        weekButton = (Button) findViewById(R.id.weekSteps);
        weekButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                changeToMonthView();
            }
        });

        monthButton = (Button) findViewById(R.id.monthSteps);
        monthButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                changeToWeekView();
            }
        });
    }

    public void checkDayOfWeek(){
        switch(now.get(Calendar.DAY_OF_WEEK)){
            case(Calendar.MONDAY):
                stepsWeek[0] = dayStepsAmount;
                break;
            case(Calendar.TUESDAY):
                stepsWeek[1] = dayStepsAmount;
                break;
            case(Calendar.WEDNESDAY):
                stepsWeek[2] = dayStepsAmount;
                break;
            case(Calendar.THURSDAY):
                stepsWeek[3] = dayStepsAmount;
                break;
            case(Calendar.FRIDAY):
                stepsWeek[4] = dayStepsAmount;
                break;
            case(Calendar.SATURDAY):
                stepsWeek[5] = dayStepsAmount;
                break;
            case(Calendar.SUNDAY):
                stepsWeek[6] = dayStepsAmount;
                break;
            default:
                break;
        }
    }

    public void changeToMonthView(){
        DataPoint[] datapoints = new DataPoint[31];

        for (int i=0; i<31; i++) {
            datapoints[i] = new DataPoint(i, stepsMonth[i]);
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

    public void onMonthPressed(){
        changeToMonthView();
    }

    public void onWeekPressed(){
        changeToWeekView();
    }

    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}

