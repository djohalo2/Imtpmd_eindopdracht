package com.example.djohalo2.imtpmd_eindopdracht;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.djohalo2.imtpmd_eindopdracht.Models.Vak;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class YearActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference ref = null;

        final ArrayList<Vak> vakkenLijst = new ArrayList<Vak>();

        Intent intent = getIntent();

        Bundle yearBundle = intent.getExtras();

        final ListView lv = (ListView) findViewById(R.id.vakken_lijst);
        final int jaar = yearBundle.getInt("jaar");
        final int keuzevak = yearBundle.getInt("keuzevak");

        ref = database.getReference("users/" + uid + "/vakken");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot vakSnapshot : dataSnapshot.getChildren()) {
                    Vak vak = vakSnapshot.getValue(Vak.class);
                    if (jaar == vak.getJaar() && !vak.isKeuzevak() && !vak.isGevolgd()) {
                        vakkenLijst.add(vak);
                    } else if (vak.isKeuzevak() && keuzevak == 1) {
                        vakkenLijst.add(vak);
                    }
                }

                Log.e("Vakkenlijst lengte", String.valueOf(vakkenLijst.size()));

                Spinner dropdown = (Spinner) findViewById(R.id.dropdown);

                fillList(vakkenLijst);

                dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        final ArrayList<Vak> filteredVakkenLijst = new ArrayList<>();

                        for (int i = 0; i < vakkenLijst.size(); i++) {
                            String vakPeriode = "Periode " + String.valueOf(vakkenLijst.get(i).getPeriode());
                            String selectedPeriode = String.valueOf(parent.getSelectedItem());

                            if (vakPeriode.equals(selectedPeriode)) {
                                filteredVakkenLijst.add(vakkenLijst.get(i));
                            } else if (selectedPeriode.equals("Alle periodes")) {
                                filteredVakkenLijst.add(vakkenLijst.get(i));
                            }
                        }

                        fillList(filteredVakkenLijst);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

            public void fillList(ArrayList<Vak> vakken){
                final ArrayList<Vak> vakjes = vakken;

                ListAdapter la = new ArrayAdapter<Vak>(YearActivity.this,
                        android.R.layout.simple_list_item_1,
                        vakjes);

                lv.setAdapter(la);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(YearActivity.this, VakActivity.class);
                        i.putExtra("vaknaam", vakjes.get(position).getNaam());
                        i.putExtra("positie", position);
                        startActivity(i);
                    }
                });
            }
        });


    }
}
