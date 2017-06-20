package com.example.djohalo2.imtpmd_eindopdracht.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.djohalo2.imtpmd_eindopdracht.Models.Vak;
import com.example.djohalo2.imtpmd_eindopdracht.R;
import com.example.djohalo2.imtpmd_eindopdracht.VakActivity;
import com.example.djohalo2.imtpmd_eindopdracht.YearActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AfgerondeVakkenFragment extends Fragment {

    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_afgeronde_vakken, container, false);
        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference ref = database.getReference("users/" + uid + "/vakken");

        final ListView lv = (ListView) getView().findViewById(R.id.vakken_lijst);
        final ArrayList<Vak> afgerondeVakken = new ArrayList<>();
        final ArrayList<Vak> behaaldeVakken = new ArrayList<>();
        final ArrayList<Vak> nietBehaaldeVakken = new ArrayList<>();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot vakSnapshot : dataSnapshot.getChildren()) {
                    Vak vak = vakSnapshot.getValue(Vak.class);

                    if(vak.isGevolgd()){
                        afgerondeVakken.add(vak);
                    }
                    if(vak.isGehaald()){
                        behaaldeVakken.add(vak);
                    }
                    if(!vak.isGehaald() && vak.isGevolgd()){
                        nietBehaaldeVakken.add(vak);
                    }
                }

                Spinner dropdown = (Spinner) getView().findViewById(R.id.dropdown);

                fillList(afgerondeVakken);

                dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedItem = String.valueOf(parent.getSelectedItem());
                        if(selectedItem.equals("Alle vakken")){
                            fillList(afgerondeVakken);
                        } else if(selectedItem.equals("Behaald")){
                            fillList(behaaldeVakken);
                        }else if(selectedItem.equals("Niet behaald")){
                            fillList(nietBehaaldeVakken);
                        }
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

                ListAdapter la = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1,
                        vakjes);

                lv.setAdapter(la);
            }
        });


    }



}
