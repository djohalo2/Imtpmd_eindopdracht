package com.example.djohalo2.imtpmd_eindopdracht;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.djohalo2.imtpmd_eindopdracht.Models.Vak;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int i = 0;
    int totaalStudiepunten;
    int gehaaldeStudiepunten;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int studiepuntenKeuzevakken = 12;

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference ref = database.getReference("users/" + uid + "/vakken");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("Count " ,""+dataSnapshot.getChildrenCount());
                totaalStudiepunten = 0;
                gehaaldeStudiepunten = 0;

                int[] ecPerJaar = new int[4];
                int[] nietGehaaldPerJaar = new int[4];

                for (DataSnapshot vakSnapshot: dataSnapshot.getChildren()) {
                    Vak vak = vakSnapshot.getValue(Vak.class);

                    int jaar = vak.getJaar();

                    if(vak.isGevolgd()){
                        switch(jaar){
                            case 1:
                                if(vak.isGehaald()){
                                    ecPerJaar[0] += vak.getEc();
                                } else {
                                    nietGehaaldPerJaar[0] += vak.getEc();
                                }
                                break;
                            case 2:
                                if(vak.isGehaald()){
                                    ecPerJaar[1] += vak.getEc();
                                } else {
                                    nietGehaaldPerJaar[1] += vak.getEc();
                                }
                                break;
                            case 3:
                                if(vak.isGehaald()){
                                    ecPerJaar[2] += vak.getEc();
                                } else {
                                    nietGehaaldPerJaar[2] += vak.getEc();
                                }
                                break;
                            case 4:
                                if(vak.isGehaald()){
                                    ecPerJaar[3] += vak.getEc();
                                } else {
                                    nietGehaaldPerJaar[3] += vak.getEc();
                                }
                                break;
                            default: break;
                        }
                    }

                    if(!vak.isKeuzevak()){
                        totaalStudiepunten += vak.getEc();
                    }

                    if(vak.isGehaald()){
                        gehaaldeStudiepunten += vak.getEc();
                    }
                }

                BarChart jaarOverzicht = (BarChart) findViewById(R.id.chart);

                List<BarEntry> gehaaldEntries = new ArrayList<>();
                List<BarEntry> totaalEntries = new ArrayList<>();
                List<BarEntry> tekortEntries = new ArrayList<>();

                for(int j = 0; j < ecPerJaar.length; j++){
                    totaalEntries.add(new BarEntry(j, 60, "Jaar " + String.valueOf((j + 1))));
                    gehaaldEntries.add(new BarEntry(j, ecPerJaar[j]));
                    tekortEntries.add(new BarEntry(j, nietGehaaldPerJaar[j]));
                }

                BarDataSet totaalSet = new BarDataSet(totaalEntries, "Totaal te behalen EC");
                BarDataSet gehaaldSet = new BarDataSet(gehaaldEntries, "Gehaalde EC");
                BarDataSet tekortSet = new BarDataSet(tekortEntries, "Missende EC");

                totaalSet.setColor(Color.parseColor("#2196F3"));
                gehaaldSet.setColor(Color.parseColor("#4CAF50"));
                tekortSet.setColor(Color.parseColor("#F44336"));

                float groupSpace = 0.06f;
                float barSpace = 0.02f;
                float barWidth = 0.20f;

                ArrayList<String> labels = new ArrayList<>();
                labels.add("Jaar 1");
                labels.add("Jaar 2");
                labels.add("Jaar 3");
                labels.add("Jaar 4");

                BarData barData = new BarData(totaalSet, gehaaldSet, tekortSet);
                barData.setBarWidth(barWidth);
                jaarOverzicht.setData(barData);

                //X-axis
                XAxis xAxis = jaarOverzicht.getXAxis();
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setCenterAxisLabels(true);
                xAxis.setDrawGridLines(false);
                xAxis.setAxisMaximum(6);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

                jaarOverzicht.groupBars(0, groupSpace, barSpace);
                jaarOverzicht.setFitBars(true);
                jaarOverzicht.invalidate();
                jaarOverzicht.animateY(1000);

                totaalStudiepunten += studiepuntenKeuzevakken;
                TextView studiepuntProgressBarText = (TextView) findViewById(R.id.progress_bar_text);

                ProgressBar studiepuntProgressBar = (ProgressBar) findViewById(R.id.studiepunt_progress_bar);
                double percentage = (double) gehaaldeStudiepunten / totaalStudiepunten;
                int progress = (int) Math.round(percentage * 100);
                studiepuntProgressBar.setProgress(progress);
                studiepuntProgressBarText.setText(String.valueOf(gehaaldeStudiepunten) + " / " + String.valueOf(totaalStudiepunten));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        Button keuzevakBtn = (Button)findViewById(R.id.keuzevak_btn);

        Button[] yearsArr = new Button[4];
        for(i = 0; i < yearsArr.length; i++){
            yearsArr[i] = (Button)findViewById(getResources().getIdentifier("year" + (i + 1) + "_btn", "id", getPackageName()));
        }

        yearsArr[0].setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                makeIntent("jaar", 1);
            }
        });

        yearsArr[1].setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                makeIntent("jaar", 2);
            }
        });

        yearsArr[2].setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                makeIntent("jaar", 3);
            }
        });

        yearsArr[3].setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
               makeIntent("jaar", 4);
            }
        });

        keuzevakBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                makeIntent("keuzevak", 1);
            }
        });

    }

    public void makeIntent(String intentName, int jaar){
        Intent intent = new Intent(MainActivity.this, YearActivity.class);
        intent.putExtra(intentName, jaar);
        startActivity(intent);
    }
}
