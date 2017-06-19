package com.example.djohalo2.imtpmd_eindopdracht;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
                for (DataSnapshot vakSnapshot: dataSnapshot.getChildren()) {
                    Vak vak = vakSnapshot.getValue(Vak.class);
                    if(!vak.isKeuzevak()){
                        totaalStudiepunten += vak.getEc();
                    }

                    if(vak.isGehaald()){
                        gehaaldeStudiepunten += vak.getEc();
                    }
                }

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
